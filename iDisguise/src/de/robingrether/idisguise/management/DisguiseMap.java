package de.robingrether.idisguise.management;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.storage.Storage;

public class DisguiseMap {
	
	private final Map<UUID, Disguise> disguises;
	
	/*public DisguiseMap(Map<?, Disguise> map) {
		if(map != null && !map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof UUID) {
				disguises = new ConcurrentHashMap<UUID, Disguise>((Map<UUID, Disguise>)map);
			} else if(map.keySet().iterator().next() instanceof String) {
				disguises = new ConcurrentHashMap<UUID, Disguise>();
				for(Entry<String, Disguise> entry : ((Map<String,Disguise>)map).entrySet()) {
					disguises.put(PlayerHelper.getInstance().getUniqueId(entry.getKey()), entry.getValue());
				}
			} else {
				disguises = new ConcurrentHashMap<UUID, Disguise>();
			}
		} else {
			disguises = new ConcurrentHashMap<UUID, Disguise>();
		}
	}*/
	
	public DisguiseMap() {
		disguises = new ConcurrentHashMap<UUID, Disguise>();
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguises.containsKey(offlinePlayer.getUniqueId());
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguises.get(offlinePlayer.getUniqueId());
	}
	
	public Map<UUID, Disguise> getMap() {
		return Collections.unmodifiableMap(disguises);
	}
	
	public Set<UUID> getDisguisedPlayers() {
		return Collections.unmodifiableSet(disguises.keySet());
	}
	
	public void updateDisguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		disguises.put(offlinePlayer.getUniqueId(), disguise);
		Storage.getInstance().update(offlinePlayer, disguise);
	}
	
	public Disguise removeDisguise(OfflinePlayer offlinePlayer) {
		Storage.getInstance().update(offlinePlayer, null);
		return disguises.remove(offlinePlayer.getUniqueId());
	}
	
	public void putAll(Map<?, Disguise> map) {
		if(map != null && !map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof UUID) {
				disguises.putAll((Map<UUID, Disguise>)map);
			} else if(map.keySet().iterator().next() instanceof String) {
				for(Entry<String, Disguise> entry : ((Map<String,Disguise>)map).entrySet()) {
					disguises.put(PlayerHelper.getInstance().getUniqueId(entry.getKey()), entry.getValue());
				}
			}
		}
	}
	
}