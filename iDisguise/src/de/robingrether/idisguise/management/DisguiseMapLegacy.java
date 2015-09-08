package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.disguise.Disguise;

public class DisguiseMapLegacy {
	
	private Map<String, Disguise> disguises;
	
	public DisguiseMapLegacy() {
		disguises = new ConcurrentHashMap<String, Disguise>();
	}
	
	public DisguiseMapLegacy(Map<String, Disguise> map) {
		disguises = new ConcurrentHashMap<String, Disguise>(map);
	}
	
	public DisguiseMapLegacy(DisguiseMap presentMap) {
		disguises = new ConcurrentHashMap<String, Disguise>();
		Map<UUID, Disguise> map = presentMap.getMap();
		for(Entry<UUID, Disguise> entry : map.entrySet()) {
			disguises.put(Bukkit.getOfflinePlayer(entry.getKey()).getName(), entry.getValue());
		}
	}
	
	public boolean isDisguised(String player) {
		return disguises.containsKey(player);
	}
	
	public Disguise getDisguise(String player) {
		return disguises.get(player);
	}
	
	public Map<String, Disguise> getMap() {
		return disguises;
	}
	
	public Set<String> getPlayers() {
		return disguises.keySet();
	}
	
	public void putDisguise(String player, Disguise disguise) {
		disguises.put(player, disguise);
	}
	
	public Disguise removeDisguise(String player) {
		return disguises.remove(player);
	}
	
}