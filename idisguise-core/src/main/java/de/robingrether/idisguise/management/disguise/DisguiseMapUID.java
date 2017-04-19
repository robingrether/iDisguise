package de.robingrether.idisguise.management.disguise;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.PlayerHelper;

public class DisguiseMapUID extends DisguiseMap {
	
	private final Map<UUID, Disguise> disguises;
	
	public DisguiseMapUID(Map<?, Disguise> map) {
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
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguises.containsKey(offlinePlayer.getUniqueId());
	}
	
	public boolean isDisguised(Entity entity) {
		return disguises.containsKey(entity.getUniqueId());
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguises.get(offlinePlayer.getUniqueId());
	}
	
	public Disguise getDisguise(Entity entity) {
		return disguises.get(entity.getUniqueId());
	}
	
	public Map<UUID, Disguise> getMap() {
		return disguises;
	}
	
	public Set<UUID> getDisguisedPlayers() {
		return disguises.keySet();
	}
	
	public void updateDisguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		disguises.put(offlinePlayer.getUniqueId(), disguise);
	}
	
	public void updateDisguise(Entity entity, Disguise disguise) {
		disguises.put(entity.getUniqueId(), disguise);
	}
	
	public Disguise removeDisguise(OfflinePlayer offlinePlayer) {
		return disguises.remove(offlinePlayer.getUniqueId());
	}
	
	public Disguise removeDisguise(Entity entity) {
		return disguises.remove(entity.getUniqueId());
	}
	
}