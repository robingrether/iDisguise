package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.disguise.DisguiseMapUID;

public abstract class DisguiseMap {
	
	public abstract boolean isDisguised(OfflinePlayer offlinePlayer);
	
	public abstract Disguise getDisguise(OfflinePlayer offlinePlayer);
	
	public abstract Map<?, Disguise> getMap();
	
	public abstract Set<?> getDisguisedPlayers();
	
	public abstract void updateDisguise(OfflinePlayer offlinePlayer, Disguise disguise);
	
	public abstract Disguise removeDisguise(OfflinePlayer offlinePlayer);
	
	public static DisguiseMap emptyMap() {
		return new DisguiseMapUID(null);
	}
	
	public static DisguiseMap fromMap(Map<?, Disguise> map) {
		return new DisguiseMapUID(map);
	}
	
}