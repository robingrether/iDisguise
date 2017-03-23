package de.robingrether.idisguise.management.player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import static de.robingrether.idisguise.management.Reflection.*;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.VersionHelper;

import net.minecraft.util.com.mojang.authlib.GameProfile;

public class PlayerHelperUID17 extends PlayerHelper {
	
	// only lower case names
	private final Map<String, GameProfile> gameProfiles = new ConcurrentHashMap<String, GameProfile>();
	private final Map<String, Object> currentlyLoading =  new ConcurrentHashMap<String, Object>();
	
	public String getCaseCorrectedName(String name) {
		return Bukkit.getOfflinePlayer(name).getName();
	}
	
	public UUID getUniqueId(String name) {
		return Bukkit.getOfflinePlayer(name).getUniqueId();
	}
	
	public String getName(UUID uniqueId) {
		return Bukkit.getOfflinePlayer(uniqueId).getName();
	}
	
	public GameProfile getGameProfile(UUID uniqueId, String skinName, String displayName) {
		GameProfile localGameProfile = new GameProfile(uniqueId, displayName.length() <= 16 ? displayName : skinName);
		if(gameProfiles.containsKey(skinName.toLowerCase(Locale.ENGLISH))) {
			localGameProfile.getProperties().putAll(gameProfiles.get(skinName.toLowerCase(Locale.ENGLISH)).getProperties());
		}
		return localGameProfile;
	}
	
	public synchronized void loadGameProfileAsynchronously(final String skinName) {
		if(gameProfiles.containsKey(skinName.toLowerCase(Locale.ENGLISH)) || currentlyLoading.containsKey(skinName.toLowerCase(Locale.ENGLISH))) {
			return;
		}
		currentlyLoading.put(skinName.toLowerCase(Locale.ENGLISH), new Object());
		Bukkit.getScheduler().runTaskAsynchronously(iDisguise.getInstance(), new Runnable() {
			
			public void run() {
				GameProfile gameProfile = loadGameProfile(skinName);
				if(gameProfile != null) {
					gameProfiles.put(skinName.toLowerCase(Locale.ENGLISH), gameProfile);
				}
				synchronized(currentlyLoading.get(skinName.toLowerCase(Locale.ENGLISH))) {
					currentlyLoading.remove(skinName.toLowerCase(Locale.ENGLISH)).notifyAll();
				}
			}
			
		});
	}
	
	public boolean isGameProfileLoaded(String skinName) {
		return gameProfiles.containsKey(skinName.toLowerCase(Locale.ENGLISH));
	}
	
	public void waitForGameProfile(String skinName) {
		if(currentlyLoading.containsKey(skinName.toLowerCase(Locale.ENGLISH))) {
			try {
				synchronized(currentlyLoading.get(skinName.toLowerCase(Locale.ENGLISH))) {
					currentlyLoading.get(skinName.toLowerCase(Locale.ENGLISH)).wait(10000L);
				}
			} catch(InterruptedException e) {
			}
		}
	}
	
	private GameProfile loadGameProfile(String skinName) {
		try {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(skinName);
			GameProfile gameProfile = (GameProfile)(offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer));
			if(gameProfile.getProperties().isEmpty()) {
				MinecraftSessionService_fillProfileProperties.invoke(MinecraftServer_getSessionService.invoke(MinecraftServer_getServer.invoke(null)), gameProfile, true);
			}
			return gameProfile;
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
			}
		}
		return null;
	}
	
}