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

import static de.robingrether.idisguise.management.Reflection.*;

public final class EntityIdList {
	
	private EntityIdList() {}
	
	private static Map<Integer, Player> players;
	
	public static void init() {
		players = new ConcurrentHashMap<Integer, Player>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
	}
	
	public static void addPlayer(Player player) {
		players.put(player.getEntityId(), player);
	}
	
	public static void removePlayer(Player player) {
		players.remove(player.getEntityId());
	}
	
	public static Player getPlayerByEntityId(int entityId) {
		return players.get(entityId);
	}
	
	public static LivingEntity getEntityByEntityId(int entityId) {
		for(World world : Bukkit.getWorlds()) {
			try {
				Object entity = World_getEntityById.invoke(CraftWorld_getHandle.invoke(world), entityId);
				if(entity != null) {
					Object bukkitEntity = Entity_getBukkitEntity.invoke(entity);
					if(bukkitEntity instanceof LivingEntity) {
						return (LivingEntity)bukkitEntity;
					}
					break;
				}
			} catch(Exception e) {
			}
		}
		return null;
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
	
}
