package de.robingrether.idisguise.management.player;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

import static de.robingrether.idisguise.management.Reflection.*;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.VersionHelper;

public class PlayerHelperUID18 extends PlayerHelper {
	
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
			ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
			GameProfileRepository_findProfilesByNames.invoke(MinecraftServer_getGameProfileRepository.invoke(MinecraftServer_getServer.invoke(null)), new String[] {skinName}, Agent.MINECRAFT, callback);
			GameProfile gameProfile = callback.getGameProfile();
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
	
	private class ProfileLookupCallbackImpl implements ProfileLookupCallback {
		
		private GameProfile gameProfile;
		
		public void onProfileLookupSucceeded(GameProfile gameProfile) {
			this.gameProfile = gameProfile;
		}
		
		public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
			this.gameProfile = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameProfile.getName()).getBytes(StandardCharsets.UTF_8)), gameProfile.getName());
		}
		
		public GameProfile getGameProfile() {
			return gameProfile;
		}
		
	}
	
}