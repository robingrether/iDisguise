package de.robingrether.idisguise.management.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
	
	public static Entity getEntityByEntityId(int entityId) {
		for(World world : Bukkit.getWorlds()) {
			try {
				Object entity = World_getEntityById.invoke(CraftWorld_getHandle.invoke(world), entityId);
				if(entity != null) {
					return (Entity)Entity_getBukkitEntity.invoke(entity);
				}
			} catch(Exception e) {
			}
		}
		return null;
	}
	
}
