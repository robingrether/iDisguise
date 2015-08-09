package de.robingrether.idisguise.management;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;

public class DisguiseManager {
	
	private static volatile DisguiseList disguiseList = new DisguiseList();
	private static boolean[] attributes = new boolean[1]; // (0) show names
	
	public static Packet<?> getSpawnPacket(Player player) {
		Packet<?> packetSpawn;
		Disguise disguise = getDisguise(player);
		if(disguise == null) {
			packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		} else if(disguise instanceof PlayerDisguise) {
			packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
			try {
				Field field = packetSpawn.getClass().getDeclaredField("b");
				field.setAccessible(true);
				field.set(packetSpawn, ProfileUtil.getUniqueId(((PlayerDisguise)disguise).getName()));
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			Entity entity = disguise.getEntity(((CraftPlayer)player).getHandle().world, player.getLocation(), player.getEntityId());
			if(attributes[0] && entity instanceof EntityInsentient) {
				((EntityInsentient)entity).setCustomName(player.getName());
			}
			if(entity instanceof EntityLiving) {
				packetSpawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)entity);
			} else {
				packetSpawn = new PacketPlayOutSpawnEntity(entity, EntityTypes.a(entity));
			}
		}
		return packetSpawn;
	}
	
	public static Packet<?> getPlayerInfoPacket(Player player) {
		return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle());
	}
	
	public static Packet<?> getDestroyPacket(Player player) {
		return new PacketPlayOutEntityDestroy(player.getEntityId());
	}
	
	public static synchronized void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendPacketLater(final Player player, final Packet<?> packet, long delay) {
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				sendPacket(player, packet);
			}
		};
		runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("iDisguise"), delay);
	}
	
	public static synchronized void disguiseToAll(Player player, Disguise disguise) {
		if(isDisguised(player)) {
			undisguiseToAll(player);
		}
		disguiseList.putDisguise(player.getUniqueId(), disguise);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(((PlayerDisguise)disguise).getName());
			if(((PlayerDisguise)disguise).isGhost()) {
				GhostFactory.addPlayer(Bukkit.getOfflinePlayer(ProfileUtil.getUniqueId(((PlayerDisguise)disguise).getName())));
				GhostFactory.addGhost(player);
			}
		}
		Packet<?> packetDestroy = getDestroyPacket(player);
		Packet<?> packetPlayerInfo = getPlayerInfoPacket(player);
		Packet<?> packetSpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(!observer.getWorld().equals(player.getWorld())) {
				continue;
			}
			if(observer.getName().equalsIgnoreCase(player.getName())) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetPlayerInfo);
			sendPacket(observer, packetSpawn);
		}
	}
	
	public static synchronized Disguise undisguiseToAll(Player player) {
		Disguise disguise = disguiseList.removeDisguise(player.getUniqueId());
		if(disguise == null) {
			return null;
		}
		GhostFactory.removeGhost(player);
		Packet<?> packetDestroy = getDestroyPacket(player);
		Packet<?> packetPlayerInfo = getPlayerInfoPacket(player);
		Packet<?> packetSpawn = getSpawnPacket(player);
		if(disguise instanceof PlayerDisguise) {
			player.setDisplayName(player.getName());
		}
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(!observer.getWorld().equals(player.getWorld())) {
				continue;
			}
			if(observer.getName().equalsIgnoreCase(player.getName())) {
				continue;
			}
			sendPacket(observer, packetDestroy);
			sendPacket(observer, packetPlayerInfo);
			sendPacket(observer, packetSpawn);
		}
		updateAttributes(player);
		return disguise;
	}
	
	public static synchronized void undisguiseAll() {
		for(UUID player : disguiseList.getPlayers()) {
			if(Bukkit.getPlayer(player) != null) {
				undisguiseToAll(Bukkit.getPlayer(player));
			} else {
				disguiseList.removeDisguise(player);
			}
		}
	}
	
	public static synchronized void updateAttributes(Player player, Player observer) {
		if(!observer.getWorld().equals(player.getWorld())) {
			return;
		}
		if(observer.getName().equalsIgnoreCase(player.getName())) {
			return;
		}
		Packet<?>[] packets = new Packet<?>[6];
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
	
	public static synchronized void updateAttributes(Player player) {
		for(Player observer : Bukkit.getOnlinePlayers()) {
			updateAttributes(player, observer);
		}
	}
	
	public static boolean isDisguised(Player player) {
		return disguiseList.isDisguised(player.getUniqueId());
	}
	
	public static Disguise getDisguise(Player player) {
		return disguiseList.getDisguise(player.getUniqueId());
	}
	
	public static int getOnlineDisguiseCount() {
		int count = 0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(isDisguised(p)) {
				count++;
			}
		}
		return count;
	}
	
	public static DisguiseList getDisguiseList() {
		return disguiseList;
	}
	
	public static void setDisguiseList(DisguiseList disguiseList) {
		DisguiseManager.disguiseList = disguiseList;
	}
	
	public static void setAttribute(int index, boolean value) {
		attributes[index] = value;
	}
	
}