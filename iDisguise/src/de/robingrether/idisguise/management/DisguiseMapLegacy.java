package de.robingrether.idisguise.management;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
			String name = PlayerHelper.instance.getName(entry.getKey());
			if(name != null) {
				disguises.put(name.toLowerCase(Locale.ENGLISH), entry.getValue());
			}
		}
	}
	
	public boolean isDisguised(String player) {
		return disguises.containsKey(player.toLowerCase(Locale.ENGLISH));
	}
	
	public Disguise getDisguise(String player) {
		return disguises.get(player.toLowerCase(Locale.ENGLISH));
	}
	
	public Map<String, Disguise> getMap() {
		return disguises;
	}
	
	public Set<String> getPlayers() {
		return disguises.keySet();
	}
	
	public void putDisguise(String player, Disguise disguise) {
		disguises.put(player.toLowerCase(Locale.ENGLISH), disguise);
	}
	
	public Disguise removeDisguise(String player) {
		return disguises.remove(player.toLowerCase(Locale.ENGLISH));
	}
	
}