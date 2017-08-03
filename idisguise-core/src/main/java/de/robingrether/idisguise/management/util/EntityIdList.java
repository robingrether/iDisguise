package de.robingrether.idisguise.management.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
	
}
