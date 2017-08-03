package de.robingrether.idisguise.management;

import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class ProfileHelper {
	
	private static ProfileHelper instance;
	
	public static ProfileHelper getInstance() {
		return instance;
	}
	
	static void setInstance(ProfileHelper instance) {
		ProfileHelper.instance = instance;
	}
	
	public abstract String getCaseCorrectedName(String name);
	
	public abstract UUID getUniqueId(String name);
	
	public abstract Object getGameProfile(UUID uniqueId, String skinName, String displayName);
	
	public abstract void loadGameProfileAsynchronously(String skinName);
	
	public abstract void registerGameProfile(Player player);
	
	public abstract boolean isGameProfileLoaded(String skinName);
	
	public abstract void waitForGameProfile(String skinName);
	
}