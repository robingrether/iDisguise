package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.Disguise;

public class DisguiseList {
	
	private Map<UUID, Disguise> disguises;
	
	public DisguiseList() {
		disguises = new ConcurrentHashMap<UUID, Disguise>();
	}
	
	public DisguiseList(ConcurrentHashMap<UUID, Disguise> map) {
		disguises = map;
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