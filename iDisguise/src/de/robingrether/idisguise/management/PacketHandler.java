package de.robingrether.idisguise.management;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.api.PlayerInteractDisguisedPlayerEvent;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;

import static de.robingrether.idisguise.management.Reflection.*;
import de.robingrether.util.ObjectUtil;

public class PacketHandler {
	
	private static PacketHandler instance;
	
	public static PacketHandler getInstance() {
		return instance;
	}
	
	static void setInstance(PacketHandler instance) {
		PacketHandler.instance = instance;
	}
	
	public Object handlePacketPlayInUseEntity(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayInUseEntity_entityId.getInt(packet));
		boolean attack;
		if(VersionHelper.require1_7()) {
			attack = PacketPlayInUseEntity_getAction.invoke(packet).equals(EnumEntityUseAction_ATTACK.get(null));
		} else {
			attack = PacketPlayInUseEntity_action.getInt(packet) == 1;
		}
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player) && !attack) {
			if(ObjectUtil.equals(DisguiseManager.getInstance().getDisguise(player).getType(), DisguiseType.SHEEP, DisguiseType.WOLF)) {
				BukkitRunnable runnable = new BukkitRunnable() {
					
					public void run() {
						DisguiseManager.getInstance().resendPackets(player);
						observer.updateInventory();
					}
					
				};
				runnable.runTaskLater(iDisguise.getInstance(), 2L);
			}
			Bukkit.getPluginManager().callEvent(new PlayerInteractDisguisedPlayerEvent(observer, player));
			return null;
		}
		return packet;
	}
	
	public Object[] handlePacketPlayOutNamedEntitySpawn(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutNamedEntitySpawn_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player)) {
			Object[] spawnPackets = PacketHelper.getInstance().getPackets(player);
			if(PacketPlayOutSpawnEntityLiving.isInstance(spawnPackets[0]) && DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				byte yaw = PacketPlayOutSpawnEntityLiving_yaw.getByte(spawnPackets[0]);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutSpawnEntityLiving_yaw.setByte(spawnPackets[0], yaw);
			} else if(PacketPlayOutSpawnEntity.isInstance(spawnPackets[0]) && DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
//				if(TODO) {
//					PacketPlayOutSpawnEntity_x.set TODO
//				}
			}
			return spawnPackets;
		}
		return new Object[] {packet};
	}
	
	public Object handlePacketPlayOutPlayerInfo(final Player observer, final Object packet) throws Exception {
		if(VersionHelper.require1_8()) {
			Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
			List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(customizablePacket);
			List itemsToAdd = new ArrayList();
			List itemsToRemove = new ArrayList();
			for(Object playerInfo : playerInfoList) {
				OfflinePlayer offlinePlayer = (OfflinePlayer)Bukkit_getOfflinePlayer.invoke(null, GameProfile_getProfileId.invoke(PlayerInfoData_getProfile.invoke(playerInfo)));
				if(offlinePlayer != null && offlinePlayer != observer && DisguiseManager.getInstance().isDisguised(offlinePlayer)) {
					Object newPlayerInfo = PacketHelper.getInstance().getPlayerInfo(offlinePlayer, customizablePacket, (Integer)PlayerInfoData_getPing.invoke(playerInfo), PlayerInfoData_getGamemode.invoke(playerInfo), PlayerInfoData_getDisplayName.invoke(playerInfo));
					itemsToRemove.add(playerInfo);
					if(newPlayerInfo != null) {
						itemsToAdd.add(newPlayerInfo);
					}
				}
			}
			playerInfoList.removeAll(itemsToRemove);
			playerInfoList.addAll(itemsToAdd);
			return customizablePacket;
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ChatColor.stripColor((String)PacketPlayOutPlayerInfo_playerName.get(packet)));
			if(offlinePlayer != null && offlinePlayer != observer && DisguiseManager.getInstance().isDisguised(offlinePlayer)) {
				String name = (String)PacketHelper.getInstance().getPlayerInfo(offlinePlayer, null, 0, null, PacketPlayOutPlayerInfo_playerName.get(packet));
				if(name != null) {
					Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
					PacketPlayOutPlayerInfo_playerName.set(customizablePacket, name);
					return customizablePacket;
				}
				return null;
			}
			return packet;
		}
	}
	
	public Object handlePacketPlayOutBed(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutBed_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player) && !(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise)) {
			return null;
		}
		return packet;
	}
	
	public Object handlePacketPlayOutAnimation(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutAnimation_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player) && !(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise)) {
			if(DisguiseManager.getInstance().getDisguise(player) instanceof MobDisguise) {
				if(VersionHelper.require1_7() ? PacketPlayOutAnimation_animationType.getInt(packet) == 2 : PacketPlayOutAnimation_animationType.getInt(packet) == 3) {
					return null;
				}
			} else if(DisguiseManager.getInstance().getDisguise(player) instanceof ObjectDisguise) {
				if(VersionHelper.require1_7() ? ObjectUtil.equals(PacketPlayOutAnimation_animationType.getInt(packet), 0, 2, 3) : ObjectUtil.equals(PacketPlayOutAnimation_animationType.getInt(packet),  1, 3, 5)) {
					return null;
				}
			}
		}
		return packet;
	}
	
	public Object handlePacketPlayOutEntityMetadata(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutEntityMetadata_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player) && !(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise)) {
			Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
			boolean living = DisguiseManager.getInstance().getDisguise(player) instanceof MobDisguise;
			List metadataList = (List)PacketPlayOutEntityMetadata_metadataList.get(customizablePacket);
			List itemsToRemove = new ArrayList();
			for(Object metadataItem : metadataList) {
				int metadataId = PacketHelper.getInstance().getMetadataId(metadataItem);
				if(metadataId > 0 && !(living && metadataId >= 6 && metadataId <= 9)) {
					itemsToRemove.add(metadataItem);
				}
			}
			metadataList.removeAll(itemsToRemove);
			return customizablePacket;
		}
		return packet;
	}
	
	public Object handlePacketPlayOutEntity(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutEntity_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player)) {
			if(DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
				byte yaw = PacketPlayOutEntity_yaw.getByte(customizablePacket);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutEntity_yaw.setByte(customizablePacket, yaw);
				return customizablePacket;
			} else if(DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
//				if(TODO) {
					Object customizablePacket = PacketPlayOutEntityTeleport_new.newInstance();
					PacketPlayOutEntityTeleport_entityId.setInt(customizablePacket, player.getEntityId());
					PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(player.getLocation().getX()));
					PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(player.getLocation().getY()));
					PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(player.getLocation().getZ()));
					PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, (byte)(player.getLocation().getYaw() * 256 / 360));
					PacketPlayOutEntityTeleport_pitch.setByte(customizablePacket, (byte)(player.getLocation().getPitch() * 256 / 360));
					PacketPlayOutEntityTeleport_isOnGround.setBoolean(customizablePacket, PacketPlayOutEntity_isOnGround.getBoolean(packet));
					return customizablePacket;
//				}
			}
		}
		return packet;
	}
	
	public Object handlePacketPlayOutEntityTeleport(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutEntityTeleport_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().isDisguised(player)) {
			if(DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
				byte yaw = PacketPlayOutEntityTeleport_yaw.getByte(customizablePacket);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, yaw);
				return customizablePacket;
			} else if(DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
//				if(TODO) {
					Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
					PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(player.getLocation().getX()));
					PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(player.getLocation().getY()));
					PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(player.getLocation().getZ()));
					return customizablePacket;
//				}
			}
		}
		return packet;
	}
	
	public Object handlePacketPlayOutUpdateAttributes(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutUpdateAttributes_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().getDisguise(player) instanceof ObjectDisguise) {
			return null;
		}
		return packet;
	}
	
	public Object handlePacketPlayOutCollect(final Player observer, final Object packet) throws Exception {
		final Player player = PlayerHelper.getInstance().getPlayerByEntityId(PacketPlayOutCollect_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.getInstance().getDisguise(player) instanceof ObjectDisguise) {
			return null;
		}
		return packet;
	}
	
	public Object handlePacketPlayOutNamedSoundEffect(final Player observer, final Object packet) throws Exception {
		String soundEffect = PacketHelper.getInstance().soundEffectToString(PacketPlayOutNamedSoundEffect_soundEffect.get(packet));
		if(Sounds.isSoundFromPlayer(soundEffect)) {
			Object entityHuman = VersionHelper.require1_9() ? World_findNearbyPlayer.invoke(Entity_world.get(CraftPlayer_getHandle.invoke(observer)), PacketPlayOutNamedSoundEffect_x.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_y.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_z.getInt(packet) / 8.0, 1.0, false) : World_findNearbyPlayer.invoke(Entity_world.get(CraftPlayer_getHandle.invoke(observer)), PacketPlayOutNamedSoundEffect_x.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_y.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_z.getInt(packet) / 8.0, 1.0);
			if(EntityPlayer.isInstance(entityHuman)) {
				final Player player = (Player)EntityPlayer_getBukkitEntity.invoke(entityHuman);
				if(player != null && player != observer) {
					if(DisguiseManager.getInstance().getDisguise(player) instanceof MobDisguise) {
						String newSoundEffect = Sounds.replaceSoundFromPlayer(soundEffect, ((MobDisguise)DisguiseManager.getInstance().getDisguise(player)));
						if(newSoundEffect != null) {
							Object customizablePacket = PacketHelper.getInstance().clonePacket(packet);
							PacketPlayOutNamedSoundEffect_soundEffect.set(customizablePacket, PacketHelper.getInstance().soundEffectFromString(newSoundEffect));
							return customizablePacket;
						}
						return null;
					} else if(DisguiseManager.getInstance().getDisguise(player) instanceof ObjectDisguise) {
						return null;
					}
				}
			}
		}
		return packet;
	}
	
}