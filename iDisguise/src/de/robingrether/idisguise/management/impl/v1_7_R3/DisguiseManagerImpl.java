package de.robingrether.idisguise.management.impl.v1_7_R3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R3.PacketPlayOutPlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.DisguiseMapLegacy;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PacketHelper;
import de.robingrether.idisguise.management.impl.v1_7_R3.ChannelRegisterImpl.PlayerConnectionInjected;

public class DisguiseManagerImpl extends DisguiseManager {
	
	private DisguiseMap disguiseMap = new DisguiseMap();
	
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
		PacketPlayOutPlayerInfo packetInfo = null;
		Disguise disguise = getDisguise(player);
		if(disguise == null) {
			packetInfo = new PacketPlayOutPlayerInfo(player.getName(), true, ((CraftPlayer)player).getHandle().ping);
		} else if(disguise instanceof PlayerDisguise) {
			packetInfo = new PacketPlayOutPlayerInfo(((PlayerDisguise)disguise).getName(), true, ((CraftPlayer)player).getHandle().ping);
		}
		return packetInfo;
	}
	
	protected Packet getDestroyPacket(Player player) {
		return new PacketPlayOutEntityDestroy(player.getEntityId());
	}
	
	private synchronized void sendPacket(Player player, Object packet) {
		if(packet == null) {
			return;
		}
		((PlayerConnectionInjected)((CraftPlayer)player).getHandle().playerConnection).sendPacket((Packet)packet, true);
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
		if(oldDisguise == null) {
			PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo(player.getName(), false, ((CraftPlayer)player).getHandle().ping);
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
			}
		} else if(oldDisguise instanceof PlayerDisguise) {
			PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo(((PlayerDisguise)oldDisguise).getName(), false, ((CraftPlayer)player).getHandle().ping);
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
			}
			if(oldDisguise.getType().equals(DisguiseType.GHOST)) {
				GhostFactory.instance.removeGhost(player);
			}
		}
		disguiseMap.putDisguise(player.getUniqueId(), disguise);
		if(disguise instanceof PlayerDisguise) {
			Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoAdd);
			}
			player.setDisplayName(((PlayerDisguise)disguise).getName());
			if(((PlayerDisguise)disguise).isGhost()) {
				GhostFactory.instance.addPlayer(((PlayerDisguise)disguise).getName());
				GhostFactory.instance.addGhost(player);
			}
		}
		Packet packetDestroy = getDestroyPacket(player);
		Packet packetSpawn = getSpawnPacket(player);
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetSpawn);
		}
		updateAttributes(player);
	}
	
	public synchronized Disguise undisguise(Player player) {
		Disguise disguise = disguiseMap.removeDisguise(player.getUniqueId());
		if(disguise == null) {
			return null;
		}
		Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
		if(disguise instanceof PlayerDisguise) {
			PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo(((PlayerDisguise)disguise).getName(), false, ((CraftPlayer)player).getHandle().ping);
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
				sendPacket(observer, packetPlayerInfoAdd);
			}
			if(disguise.getType().equals(DisguiseType.GHOST)) {
				GhostFactory.instance.removeGhost(player);
			}
		} else {
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoAdd);
			}
		}
		Packet packetDestroy = getDestroyPacket(player);
		Packet packetSpawn = getSpawnPacket(player);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(player.getName());
		}
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			sendPacket(observer, packetDestroy);
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