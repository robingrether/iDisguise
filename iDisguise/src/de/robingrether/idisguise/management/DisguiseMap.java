package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.Disguise;

public class DisguiseMap {
	
	private Map<UUID, Disguise> disguises;
	
	public DisguiseMap() {
		disguises = new ConcurrentHashMap<UUID, Disguise>();
	}
	
	public DisguiseMap(Map<UUID, Disguise> map) {
		disguises = new ConcurrentHashMap<UUID, Disguise>(map);
	}
	
	public DisguiseMap(DisguiseMapLegacy legacyMap) {
		disguises = new ConcurrentHashMap<UUID, Disguise>();
		Map<String, Disguise> map = legacyMap.getMap();
		for(Entry<String, Disguise> entry : map.entrySet()) {
			disguises.put(PlayerHelper.instance.getUniqueId(entry.getKey()), entry.getValue());
		}
	}
	
	public boolean isDisguised(UUID player) {
		return disguises.containsKey(player);
	}
	
	public Disguise getDisguise(UUID player) {
		return disguises.get(player);
	}
	
	public Map<UUID, Disguise> getMap() {
		return disguises;
	}
	
	public Set<UUID> getPlayers() {
		return disguises.keySet();
	}
	
	public void putDisguise(UUID player, Disguise disguise) {
		disguises.put(player, disguise);
	}
	
	public Disguise removeDisguise(UUID player) {
		return disguises.remove(player);
	}
	
}