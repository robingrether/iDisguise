package de.robingrether.idisguise.management.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.VersionHelper;

import static de.robingrether.idisguise.management.Reflection.*;

public final class EntityIdList {
	
	private EntityIdList() {}
	
	private static Map<Integer, UUID> entityUIDs;
	private static Map<UUID, String> playerNames;
	private static Map<UUID, EntityType> entityTypes;
	
	public static void init() {
		entityUIDs = new ConcurrentHashMap<Integer, UUID>();
		playerNames = new ConcurrentHashMap<UUID, String>();
		entityTypes = new ConcurrentHashMap<UUID, EntityType>();
		for(World world : Bukkit.getWorlds()) {
			for(LivingEntity livingEntity : world.getLivingEntities()) {
				addEntity(livingEntity);
			}
		}
	}
	
	public static void addEntity(LivingEntity livingEntity) {
		entityUIDs.put(livingEntity.getEntityId(), livingEntity.getUniqueId());
		entityTypes.put(livingEntity.getUniqueId(), livingEntity.getType());
		if(livingEntity instanceof Player) playerNames.put(livingEntity.getUniqueId(), livingEntity.getName());
	}
	
	public static void removeEntity(int entityId) {
		UUID uniqueId = entityUIDs.remove(entityId);
		if(uniqueId != null) {
			entityTypes.remove(uniqueId);
			playerNames.remove(uniqueId);
		}
	}
	
	public static void removeEntity(LivingEntity livingEntity) {
		removeEntity(livingEntity.getEntityId());
	}
	
	public static Player getPlayerByEntityId(int entityId) {
		UUID uniqueId = entityUIDs.get(entityId);
		return Bukkit.getPlayer(uniqueId);
	}
	
	public static UUID getEntityUIDByEntityId(int entityId) {
		return entityUIDs.get(entityId);
//		for(World world : Bukkit.getWorlds()) {
//			try {
//				Object entity = World_getEntityById.invoke(CraftWorld_getHandle.invoke(world), entityId);
//				if(entity != null) {
//					Object bukkitEntity = Entity_getBukkitEntity.invoke(entity);
//					if(bukkitEntity instanceof LivingEntity) {
//						return (LivingEntity)bukkitEntity;
//					}
//					break;
//				}
//			} catch(Exception e) {
//			}
//		}
//		return null;
	}
	
	public static LivingEntity getEntityByEntityId(int entityId) {
		if(VersionHelper.require1_12()) {
			Entity entity = Bukkit.getEntity(entityUIDs.get(entityId));
			return entity instanceof LivingEntity ? (LivingEntity)entity : null;
		} else {
			try {
				Object entity = MinecraftServer_getEntityByUID.invoke(MinecraftServer_getServer.invoke(null), entityUIDs.get(entityId));
				return entity != null && Entity_getBukkitEntity.invoke(entity) instanceof LivingEntity ? (LivingEntity)Entity_getBukkitEntity.invoke(entity) : null;
			} catch(IllegalAccessException|InvocationTargetException e) {
			}
			return null;
		}
	}
	
	public static LivingEntity getEntityByUID(UUID uniqueId) {
		if(VersionHelper.require1_12()) {
			Entity entity = Bukkit.getEntity(uniqueId);
			return entity instanceof LivingEntity ? (LivingEntity)entity : null;
		} else {
			try {
				Object entity = MinecraftServer_getEntityByUID.invoke(MinecraftServer_getServer.invoke(null), uniqueId);
				return entity != null && Entity_getBukkitEntity.invoke(entity) instanceof LivingEntity ? (LivingEntity)Entity_getBukkitEntity.invoke(entity) : null;
			} catch(IllegalAccessException|InvocationTargetException e) {
			}
			return null;
		}
	}
	
	public static LivingEntity getClosestEntity(Location location, double maxDistance) {
		List<Entity> nearbyEntities = new ArrayList<Entity>(location.getWorld().getNearbyEntities(location, maxDistance, maxDistance, maxDistance));
		for(Iterator<Entity> iterator = nearbyEntities.iterator(); iterator.hasNext();) {
			if(!(iterator.next() instanceof LivingEntity)) {
				iterator.remove();
			}
		}
		LivingEntity closestEntity = null;
		double closestDistanceSquared = Double.MAX_VALUE;
		for(Entity entity : nearbyEntities) {
			double distanceSquared = entity.getLocation().distanceSquared(location);
			if(distanceSquared < closestDistanceSquared) {
				closestEntity = (LivingEntity)entity;
				closestDistanceSquared = distanceSquared;
			}
		}
		return Math.sqrt(closestDistanceSquared) <= maxDistance ? closestEntity : null;
	}
	
	public static EntityType getEntityType(UUID uniqueId) {
		return entityTypes.get(uniqueId);
	}
	
	public static boolean isEnderDragon(UUID uniqueId) {
		return EntityType.ENDER_DRAGON.equals(getEntityType(uniqueId));
	}
	
	public static boolean isPlayer(UUID uniqueId) {
		return EntityType.PLAYER.equals(getEntityType(uniqueId));
	}
	
	public static String getPlayerName(UUID uniqueId) {
		return playerNames.get(uniqueId);
	}
	
}
