package de.robingrether.idisguise.management.impl.v1_7_R4;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PacketHelper;

public class DisguiseManagerImpl extends DisguiseManager {
	
	private Field fieldName, fieldOnline;
	
	public DisguiseManagerImpl() {
		try {
			fieldName = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
			fieldName.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldOnline = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			fieldOnline.setAccessible(true);
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
		return new PacketPlayOutPlayerInfo(player.getName(), true, ((CraftPlayer)player).getHandle().ping);
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
		Disguise oldDisguise = disguiseList.getDisguise(player.getUniqueId());
		PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo();
		Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
		try {
			fieldOnline.setBoolean(packetPlayerInfoRemove, false);
			if(oldDisguise instanceof PlayerDisguise) {
				fieldName.set(packetPlayerInfoRemove, ((PlayerDisguise)oldDisguise).getName());
			} else {
				fieldName.set(packetPlayerInfoRemove, player.getName());
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
				sendPacket(observer, packetPlayerInfoAdd);
			}
		} catch(Exception e) {
		}
		disguiseList.putDisguise(player.getUniqueId(), disguise);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(((PlayerDisguise)disguise).getName());
			if(((PlayerDisguise)disguise).isGhost()) {
				GhostFactory.instance.addPlayer(((PlayerDisguise)disguise).getName());
				GhostFactory.instance.addGhost(player);
			}
		}
		Packet packetDestroy = getDestroyPacket(player);
		
		Packet packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		for(Player observer : player.getWorld().getPlayers()) {
			if(observer == player) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetSpawn);
		}
	}
	
	public synchronized Disguise undisguise(Player player) {
		Disguise disguise = disguiseList.removeDisguise(player.getUniqueId());
		if(disguise == null) {
			return null;
		}
		PacketPlayOutPlayerInfo packetPlayerInfoRemove = new PacketPlayOutPlayerInfo();
		Packet packetPlayerInfoAdd = getPlayerInfoPacket(player);
		try {
			fieldOnline.setBoolean(packetPlayerInfoRemove, false);
			if(disguise instanceof PlayerDisguise) {
				fieldName.set(packetPlayerInfoRemove, ((PlayerDisguise)disguise).getName());
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				sendPacket(observer, packetPlayerInfoRemove);
				sendPacket(observer, packetPlayerInfoAdd);
			}
		} catch(Exception e) {
		}
		if(disguise.getType().equals(DisguiseType.GHOST)) {
			GhostFactory.instance.removeGhost(player);
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
	
}