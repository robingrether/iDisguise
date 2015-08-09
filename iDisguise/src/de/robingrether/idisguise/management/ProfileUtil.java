package de.robingrether.idisguise.management;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

public class ProfileUtil {
	
	private static final ConcurrentHashMap<String, GameProfile> gameProfiles = new ConcurrentHashMap<String, GameProfile>();
	
	public static String getCaseCorrectedName(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		return callback.getGameProfile().getName();
	}
	
	public static UUID getUniqueId(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		System.out.println("[iDisguise] GameProfile: " + callback.getGameProfile().getName() + " | " + (callback.getGameProfile() != null ? callback.getGameProfile().getId().toString() : "null"));
		return callback.getGameProfile().getId();
	}
	
	public static GameProfile getGameProfile(String name) {
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
			exception.printStackTrace();
			try {
				Field field = GameProfile.class.getField("id");
				field.setAccessible(true);
				field.set(gameProfile, UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"));
			} catch(Exception e) {
			}
			this.gameProfile = gameProfile;
		}
		
		public GameProfile getGameProfile() {
			return gameProfile;
		}
		
	}
	
}