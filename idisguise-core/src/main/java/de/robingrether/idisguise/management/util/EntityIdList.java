package de.robingrether.idisguise.management.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.VersionHelper;

//import static de.robingrether.idisguise.management.Reflection.*;

public final class EntityIdList {
	
	private EntityIdList() {}
	
	private static Map<Integer, LivingEntity> entities;
	
	public static void init() {
		legacyMode = !VersionHelper.requireVersion("v1_8_R2"); 			// are we before 1.8.3 (1_8_R2) ?
		entities = new ConcurrentHashMap<Integer, LivingEntity>();
		for(World world : Bukkit.getWorlds()) {
			for(LivingEntity livingEntity : world.getLivingEntities())
			entities.put(livingEntity.getEntityId(), livingEntity);
		}
	}
	
	public static void addEntity(LivingEntity livingEntity) {
		entities.put(livingEntity.getEntityId(), livingEntity);
	}
	
	public static void removeEntity(int entityId) {
		entities.remove(entityId);
	}
	
	public static void removeEntity(LivingEntity livingEntity) {
		entities.remove(livingEntity.getEntityId());
	}
	
	public static Player getPlayerByEntityId(int entityId) {
		LivingEntity livingEntity = entities.get(entityId);
		return livingEntity instanceof Player ? (Player)livingEntity : null;
	}
	
	public static LivingEntity getEntityByEntityId(int entityId) {
		return entities.get(entityId);
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
	
	private static boolean legacyMode = false;		// are we before 1.8.3 (1_8_R2) ?
	
	public static LivingEntity getClosestEntity(Location location, double maxDistance) {
		List<Entity> nearbyEntities = new ArrayList<Entity>(legacyMode ? location.getWorld().getEntities() : location.getWorld().getNearbyEntities(location, maxDistance, maxDistance, maxDistance));
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
	
}
