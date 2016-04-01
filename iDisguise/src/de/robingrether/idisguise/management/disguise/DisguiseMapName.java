package de.robingrether.idisguise.management.disguise;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.PlayerHelper;

public class DisguiseMapName extends DisguiseMap {
	
	private final Map<String, Disguise> disguises;
	
	public DisguiseMapName(Map<?, Disguise> map) {
		if(map != null && !map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof String) {
				disguises = new ConcurrentHashMap<String, Disguise>((Map<String, Disguise>)map);
			} else if(map.keySet().iterator().next() instanceof UUID) {
				disguises = new ConcurrentHashMap<String, Disguise>();
				for(Entry<UUID, Disguise> entry : ((Map<UUID, Disguise>)map).entrySet()) {
					String name = PlayerHelper.getInstance().getName(entry.getKey());
					if(name != null) {
						disguises.put(name.toLowerCase(Locale.ENGLISH), entry.getValue());
					}
				}
			} else {
				disguises = new ConcurrentHashMap<String, Disguise>();
			}
		} else {
			disguises = new ConcurrentHashMap<String, Disguise>();
		}
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguises.containsKey(offlinePlayer.getName().toLowerCase(Locale.ENGLISH));
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguises.get(offlinePlayer.getName().toLowerCase(Locale.ENGLISH));
	}
	
	public Map<String, Disguise> getMap() {
		return disguises;
	}
	
	public Set<String> getDisguisedPlayers() {
		return disguises.keySet();
	}
	
	public void updateDisguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		disguises.put(offlinePlayer.getName().toLowerCase(Locale.ENGLISH), disguise);
	}
	
	public Disguise removeDisguise(OfflinePlayer offlinePlayer) {
		return disguises.remove(offlinePlayer.getName().toLowerCase(Locale.ENGLISH));
	}
	
}