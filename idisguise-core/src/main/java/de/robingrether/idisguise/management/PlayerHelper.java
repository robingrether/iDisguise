package de.robingrether.idisguise.management;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class PlayerHelper {
	
	private static PlayerHelper instance;
	
	public static PlayerHelper getInstance() {
		return instance;
	}
	
	static void setInstance(PlayerHelper instance) {
		PlayerHelper.instance = instance;
	}
	
	private Map<Integer, Player> players;
	
	public PlayerHelper() {
		players = new HashMap<Integer, Player>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
	}
	
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
	
	public abstract Object getGameProfile(UUID uniqueId, String skinName, String displayName);
	
	public abstract void loadGameProfileAsynchronously(String skinName);
	
	public abstract void registerGameProfile(Player player);
	
	public abstract boolean isGameProfileLoaded(String skinName);
	
	public abstract void waitForGameProfile(String skinName);
	
}