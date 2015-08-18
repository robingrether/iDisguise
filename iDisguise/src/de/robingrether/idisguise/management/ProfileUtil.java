package de.robingrether.idisguise.management;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

public class ProfileUtil {
	
	private static final ConcurrentHashMap<String, GameProfile> gameProfiles = new ConcurrentHashMap<String, GameProfile>();
	
	public static synchronized String getCaseCorrectedName(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		return callback.getGameProfile().getName();
	}
	
	public static synchronized UUID getUniqueId(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		return callback.getGameProfile().getId();
	}
	
	public static synchronized GameProfile getGameProfile(String name) {
		if(gameProfiles.containsKey(name)) {
			return gameProfiles.get(name);
		} else {
			ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
			MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
			GameProfile gameProfile = callback.getGameProfile();
			if(gameProfile.getProperties().isEmpty()) {
				MinecraftServer.getServer().aD().fillProfileProperties(gameProfile, true);
			}
			gameProfiles.put(name, gameProfile);
			return gameProfile;
		}
	}
	
	private static class ProfileLookupCallbackImpl implements ProfileLookupCallback {
		
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