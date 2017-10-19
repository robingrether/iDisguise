package de.robingrether.idisguise.management.util;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.ProfileHelper;

public final class DisguiseMap {
	
	private final Map<UUID, Disguise> disguises;
	
	private DisguiseMap(Map<?, Disguise> map) {
		if(map != null && !map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof UUID) {
				disguises = new ConcurrentHashMap<UUID, Disguise>((Map<UUID, Disguise>)map);
			} else if(map.keySet().iterator().next() instanceof String) {
				disguises = new ConcurrentHashMap<UUID, Disguise>();
				for(Entry<String, Disguise> entry : ((Map<String, Disguise>)map).entrySet()) {
					disguises.put(ProfileHelper.getInstance().getUniqueId(entry.getKey()), entry.getValue());
				}
			} else {
				disguises = new ConcurrentHashMap<UUID, Disguise>();
			}
		} else {
			disguises = new ConcurrentHashMap<UUID, Disguise>();
		}
	}
	
	public boolean isDisguised(UUID disguisable) {
		return disguises.containsKey(disguisable);
	}
	
	public Disguise getDisguise(UUID disguisable) {
		return disguises.get(disguisable);
	}
	
	public Map<UUID, Disguise> getMap() {
		return disguises;
	}
	
	public Set<UUID> getDisguisedEntities() {
		return disguises.keySet();
	}
	
	public void updateDisguise(UUID disguisable, Disguise disguise) {
		disguises.put(disguisable, disguise);
	}
	
	public Disguise removeDisguise(UUID disguisable) {
		return disguises.remove(disguisable);
	}
	
	public static DisguiseMap emptyMap() {
		return new DisguiseMap(null);
	}
	
	public static DisguiseMap fromMap(Map<?, Disguise> map) {
		return new DisguiseMap(map);
	}
	
}