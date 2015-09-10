package de.robingrether.idisguise.management;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class PlayerHelper {
	
	public static PlayerHelper instance;
	
	private Map<Integer, Player> players;
	
	public synchronized void addPlayer(Player player) {
		players.put(player.getEntityId(), player);
	}
	
	public synchronized void removePlayer(Player player) {
		players.remove(player.getEntityId());
	}
	
	public Player getPlayerByEntityId(int entityId) {
		return players.get(entityId);
	}
	
	public abstract String getCaseCorrectedName(String name);
	
	public abstract UUID getUniqueId(String name);
	
	public abstract String getName(UUID uniqueId);
	
	public abstract Object getGameProfile(String name);
	
	protected PlayerHelper() {
		players = new HashMap<Integer, Player>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
	}
	
}