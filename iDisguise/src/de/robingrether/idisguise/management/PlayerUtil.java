package de.robingrether.idisguise.management;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {
	
	private static HashMap<Integer, Player> players;
	
	public static synchronized void addPlayer(Player player) {
		players.put(player.getEntityId(), player);
	}
	
	public static synchronized void removePlayer(Player player) {
		players.remove(player.getEntityId());
	}
	
	public static Player getPlayerByEntityId(int entityId) {
		return players.get(entityId);
	}
	
	static {
		players = new HashMap<Integer, Player>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
	}
	
}