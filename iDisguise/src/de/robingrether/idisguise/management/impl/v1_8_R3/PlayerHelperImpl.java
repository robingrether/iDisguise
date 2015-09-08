package de.robingrether.idisguise.management.impl.v1_8_R3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

import de.robingrether.idisguise.management.PlayerHelper;

public class PlayerHelperImpl extends PlayerHelper {
	
	private final Map<String, GameProfile> gameProfiles = new ConcurrentHashMap<String, GameProfile>();
	
	public PlayerHelperImpl() {
	}
	
	public synchronized String getCaseCorrectedName(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		return callback.getGameProfile().getName();
	}
	
	public synchronized UUID getUniqueId(String name) {
		ProfileLookupCallbackImpl callback = new ProfileLookupCallbackImpl();
		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] {name}, Agent.MINECRAFT, callback);
		return callback.getGameProfile().getId();
	}
	
	public synchronized GameProfile getGameProfile(String name) {
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