package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.disguise.DisguiseMapName;
import de.robingrether.idisguise.management.disguise.DisguiseMapUID;

public abstract class DisguiseMap {
	
	public abstract boolean isDisguised(OfflinePlayer offlinePlayer);
	
	public abstract Disguise getDisguise(OfflinePlayer offlinePlayer);
	
	public abstract Map<?, Disguise> getMap();
	
	public abstract Set<?> getDisguisedPlayers();
	
	public abstract void updateDisguise(OfflinePlayer offlinePlayer, Disguise disguise);
	
	public abstract Disguise removeDisguise(OfflinePlayer offlinePlayer);
	
	public static DisguiseMap emptyMap() {
		if(VersionHelper.useGameProfiles()) {
			return new DisguiseMapUID(null);
		} else {
			return new DisguiseMapName(null);
		}
	}
	
	public static DisguiseMap fromMap(Map<?, Disguise> map) {
		if(VersionHelper.useGameProfiles()) {
			return new DisguiseMapUID(map);
		} else {
			return new DisguiseMapName(map);
		}
	}
	
}