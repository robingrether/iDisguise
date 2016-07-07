package de.robingrether.idisguise.management.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import static de.robingrether.idisguise.management.Reflection.*;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.VersionHelper;
import net.minecraft.util.com.mojang.authlib.Agent;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.ProfileLookupCallback;

public class PlayerHelperUID17 extends PlayerHelper {
	
	private final Map<String, GameProfile> gameProfiles = new ConcurrentHashMap<String, GameProfile>();
	
	public synchronized String getCaseCorrectedName(String name) {
		try {
			ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
			GameProfileRepository_findProfilesByNames.invoke(MinecraftServer_getGameProfileRepository.invoke(MinecraftServer_getServer.invoke(null)), new String[] {name}, Agent.MINECRAFT, callback);
			return callback.getGameProfile().getName();
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
			}
		}
		return name;
	}
	
	public synchronized UUID getUniqueId(String name) {
		try {
			ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
			GameProfileRepository_findProfilesByNames.invoke(MinecraftServer_getGameProfileRepository.invoke(MinecraftServer_getServer.invoke(null)), new String[] {name}, Agent.MINECRAFT, callback);
			return callback.getGameProfile().getId();
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
			}
		}
		return UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
	}
	
	public synchronized String getName(UUID uniqueId) {
		return Bukkit.getOfflinePlayer(uniqueId).getName();
	}
	
	public synchronized GameProfile getGameProfile(UUID uniqueId, String skinName, String displayName) {
		if(gameProfiles.containsKey(skinName)) {
			GameProfile gameProfile = gameProfiles.get(skinName);
			GameProfile localGameProfile = new GameProfile(uniqueId, displayName.length() <= 16 ? displayName : skinName);
			localGameProfile.getProperties().putAll(gameProfile.getProperties());
			return localGameProfile;
		} else {
			try {
				ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
				GameProfileRepository_findProfilesByNames.invoke(MinecraftServer_getGameProfileRepository.invoke(MinecraftServer_getServer.invoke(null)), new String[] {skinName}, Agent.MINECRAFT, callback);
				GameProfile gameProfile = callback.getGameProfile();
				if(gameProfile.getProperties().isEmpty()) {
					MinecraftSessionService_fillProfileProperties.invoke(MinecraftServer_getSessionService.invoke(MinecraftServer_getServer.invoke(null)), gameProfile, true);
				}
				gameProfiles.put(skinName, gameProfile);
				GameProfile localGameProfile = new GameProfile(uniqueId, displayName.length() <= 16 ? displayName : skinName);
				localGameProfile.getProperties().putAll(gameProfile.getProperties());
				return localGameProfile;
			} catch(Exception e) {
				if(VersionHelper.debug()) {
					iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
				}
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
			this.gameProfile = new GameProfile(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"), gameProfile.getName());
		}
		
		public GameProfile getGameProfile() {
			return gameProfile;
		}
		
	}
	
}