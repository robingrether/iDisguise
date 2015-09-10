package de.robingrether.idisguise.management.impl.v1_8_R1;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.PlayerInfoData;
import net.minecraft.server.v1_8_R1.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.DisguiseMapLegacy;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PacketHelper;
import de.robingrether.idisguise.management.PlayerHelper;

public class DisguiseManagerImpl extends DisguiseManager {
	
	private Field fieldListInfo;
	private DisguiseMap disguiseMap = new DisguiseMap();
	
	public DisguiseManagerImpl() {
		try {
			fieldListInfo = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			fieldListInfo.setAccessible(true);
		} catch(Exception e) {
		}
	}
	
	public Packet getSpawnPacket(Player player) {
		Packet packetSpawn;
		Disguise disguise = getDisguise(player);
		if(disguise == null) {
			packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		} else {
			packetSpawn = (Packet)PacketHelper.instance.getPacket(player, disguise);
		}
		return packetSpawn;
	}
	
	protected Packet getPlayerInfoPacket(Player player) {
		return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle());
	}
	
	protected Packet getDestroyPacket(Player player) {
		return new PacketPlayOutEntityDestroy(player.getEntityId());
	}
	
	private synchronized void sendPacket(Player player, Object packet) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packet);
	}
	
	public void sendPacketLater(final Player player, final Object packet, long delay) {
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				sendPacket(player, packet);
			}
		};
		runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("iDisguise"), delay);
	}
	
	public synchronized void disguise(Player player, Disguise disguise) {
		Disguise oldDisguise = disguiseMap.getDisguise(player.getUniqueId());
		if(oldDisguise == null || oldDisguise instanceof PlayerDisguise) {
			PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle());
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
			}
			if(oldDisguise != null && oldDisguise.getType().equals(DisguiseType.GHOST)) {
				GhostFactory.instance.removeGhost(player);
			}
		}
		disguiseMap.putDisguise(player.getUniqueId(), disguise);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(((PlayerDisguise)disguise).getName());
			if(((PlayerDisguise)disguise).isGhost()) {
				GhostFactory.instance.addPlayer(((PlayerDisguise)disguise).getName());
				GhostFactory.instance.addGhost(player);
			}
		}
		Packet packetDestroy = getDestroyPacket(player);
		Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
		Packet packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetPlayerInfoAdd);
			sendPacket(observer, packetSpawn);
		}
	}
	
	public synchronized Disguise undisguise(Player player) {
		Disguise disguise = disguiseMap.removeDisguise(player.getUniqueId());
		if(disguise == null) {
			return null;
		}	
		if(disguise instanceof PlayerDisguise) {
			try {
				PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[0]);
				List<PlayerInfoData> list = (List<PlayerInfoData>)fieldListInfo.get(packetPlayerInfoRemove);
				list.add(new PlayerInfoData(packetPlayerInfoRemove, (GameProfile)PlayerHelper.instance.getGameProfile(((PlayerDisguise)disguise).getName()), ((CraftPlayer)player).getHandle().ping, ((CraftPlayer)player).getHandle().playerInteractManager.getGameMode(), null));
				for(Player observer : Bukkit.getOnlinePlayers()) {
					if(observer == player) {
						continue;
					}
					sendPacket(observer, packetPlayerInfoRemove);
				}
			} catch(Exception e) {
			}
			if(disguise.getType().equals(DisguiseType.GHOST)) {
				GhostFactory.instance.removeGhost(player);
			}
		}
		Packet packetDestroy = getDestroyPacket(player);
		Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
		Packet packetSpawn = getSpawnPacket(player);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(player.getName());
		}
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetPlayerInfoAdd);
			sendPacket(observer, packetSpawn);
		}
		updateAttributes(player);
		return disguise;
	}
	
	public void undisguiseAll() {
		for(UUID player : disguiseMap.getPlayers()) {
			if(Bukkit.getPlayer(player) != null) {
				undisguise(Bukkit.getPlayer(player));
			} else {
				disguiseMap.removeDisguise(player);
			}
		}
	}
	
	public synchronized void updateAttributes(Player player, Player observer) {
		if(observer == player) {
			return;
		}
		Packet[] packets = new Packet[6];
		int entityId = player.getEntityId();
		Location location = player.getLocation();
		EntityEquipment equipment = player.getEquipment();
		packets[0] = new PacketPlayOutEntityEquipment(entityId, 0, CraftItemStack.asNMSCopy(equipment.getItemInHand()));
		packets[1] = new PacketPlayOutEntityEquipment(entityId, 1, CraftItemStack.asNMSCopy(equipment.getBoots()));
		packets[2] = new PacketPlayOutEntityEquipment(entityId, 2, CraftItemStack.asNMSCopy(equipment.getLeggings()));
		packets[3] = new PacketPlayOutEntityEquipment(entityId, 3, CraftItemStack.asNMSCopy(equipment.getChestplate()));
		packets[4] = new PacketPlayOutEntityEquipment(entityId, 4, CraftItemStack.asNMSCopy(equipment.getHelmet()));
		Entity entity = ((CraftPlayer)player).getHandle();
		packets[5] = new PacketPlayOutEntityHeadRotation(entity, (byte)(location.getYaw() * 256 / 360));
		for(int i = 0; i < packets.length; i++) {
			sendPacketLater(observer, packets[i], 20L);
		}
	}
	
	protected synchronized void updateAttributes(Player player) {
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			updateAttributes(player, observer);
		}
	}
	
	public boolean isDisguised(Player player) {
		return disguiseMap.isDisguised(player.getUniqueId());
	}
	
	public Disguise getDisguise(Player player) {
		return disguiseMap.getDisguise(player.getUniqueId());
	}
	
	public int getOnlineDisguiseCount() {
		int count = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isDisguised(player)) {
				count++;
			}
		}
		return count;
	}
	
	public Set<OfflinePlayer> getDisguisedPlayers() {
		Set<OfflinePlayer> set = new HashSet<OfflinePlayer>();
		for(UUID player : disguiseMap.getMap().keySet()) {
			set.add(Bukkit.getOfflinePlayer(player));
		}
		return set;
	}
	
	public Disguise removeDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.removeDisguise(offlinePlayer.getUniqueId());
	}
	
	public Map getDisguises() {
		return disguiseMap.getMap();
	}
	
	public void updateDisguises(Map map) {
		if(!map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof UUID) {
				disguiseMap = new DisguiseMap(map);
			} else if(map.keySet().iterator().next() instanceof String) {
				disguiseMap = new DisguiseMap(new DisguiseMapLegacy(map));
			}
		}
	}
	
}