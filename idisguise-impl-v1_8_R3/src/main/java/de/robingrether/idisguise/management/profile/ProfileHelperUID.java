package de.robingrether.idisguise.management.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.ProfileHelper;

import static de.robingrether.idisguise.management.Reflection.*;

public class ProfileHelperUID extends ProfileHelper {
	
	public static final String API_NAME_URL = "https://api.mojang.com/users/profiles/minecraft/";
	public static final String API_NAME_ID = "id";
	
	public static final String API_UID_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public static final String API_UID_ID = "id";
	public static final String API_UID_NAME = "name";
	public static final String API_UID_PROPERTIES = "properties";
	public static final String API_UID_SIGNATURE = "signature";
	public static final String API_UID_VALUE = "value";
	
	private final Map<String, GameProfile> profilesByName = new ConcurrentHashMap<String, GameProfile>();
	private final Map<UUID, GameProfile> profilesById = new ConcurrentHashMap<UUID, GameProfile>();
	
	private final Map<String, Object> currentlyLoadingByName = new ConcurrentHashMap<String, Object>();
	private final Map<UUID, Object> currentlyLoadingById = new ConcurrentHashMap<UUID, Object>();
	
	private final Set<String> nonExistingProfiles = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	
	private void loadGameProfile(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		if(currentlyLoadingByName.containsKey(name)) {
			synchronized(currentlyLoadingByName.get(name)) {
				try {
					currentlyLoadingByName.get(name).wait(10000L);
				} catch (InterruptedException e) {
				}
				return;
			}
		}
		currentlyLoadingByName.put(name, new Object());
		BufferedReader reader = null;
		try {
			URL url = new URL(API_NAME_URL + name);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.addRequestProperty("User-Agent", iDisguise.getInstance().getFullName());
			connection.setDoOutput(true);
			connection.connect();
			if(connection.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String response = reader.readLine();
				JSONObject object = (JSONObject)JSONValue.parse(response);
				UUID uniqueId = UUID.fromString(((String)object.get(API_NAME_ID)).replaceFirst("([0-9a-f]{8})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{12})", "$1-$2-$3-$4-$5"));
				loadGameProfile(uniqueId, name);
			} else if(connection.getResponseCode() == 204) {
				nonExistingProfiles.add(name);
			}
			synchronized(currentlyLoadingByName.get(name)) {
				currentlyLoadingByName.remove(name).notifyAll();
			}
		} catch(Exception e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
				}
			}
		}
	}
	
	private void loadGameProfile(UUID uniqueId, String name) {
		if(currentlyLoadingById.containsKey(uniqueId)) {
			synchronized(currentlyLoadingById.get(uniqueId)) {
				try {
					currentlyLoadingById.get(uniqueId).wait(10000L);
				} catch (InterruptedException e) {
				}
				return;
			}
		}
		currentlyLoadingById.put(uniqueId, new Object());
		BufferedReader reader = null;
		try {
			GameProfile profile = (GameProfile)UserCache_getProfileById.invoke(MinecraftServer_getUserCache.invoke(MinecraftServer_getServer.invoke(null)), uniqueId);
			if(profile == null) {
				profile = new GameProfile(uniqueId, name);
			}
			if(!profile.getProperties().containsKey("textures")) {
				URL url = new URL(API_UID_URL + uniqueId.toString().replace("-", "") + "?unsigned=false");
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.addRequestProperty("User-Agent", iDisguise.getInstance().getFullName());
				connection.setDoOutput(true);
				connection.connect();
				if(connection.getResponseCode() == 200) {
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String response = reader.readLine();
					JSONObject object = (JSONObject)JSONValue.parse(response);
					name = (String)object.get(API_UID_NAME);
					profile = new GameProfile(uniqueId, name);
					JSONArray array = (JSONArray)object.get(API_UID_PROPERTIES);
					for(Object obj : array) {
						JSONObject property = (JSONObject)obj;
						String propertyName = (String)property.get(API_UID_NAME);
						profile.getProperties().put(propertyName, new Property(propertyName, (String)property.get(API_UID_VALUE), (String)property.get(API_UID_SIGNATURE)));
					}
					profilesById.put(uniqueId, profile);
					profilesByName.put(profile.getName().toLowerCase(Locale.ENGLISH), profile);
					UserCache_putProfile.invoke(MinecraftServer_getUserCache.invoke(MinecraftServer_getServer.invoke(null)), profile);
					final String skinName = name;
					Bukkit.getScheduler().runTask(iDisguise.getInstance(), new Runnable() {
						
						public void run() {
							for(Object disguisable : DisguiseManager.getDisguisedEntities()) {
								if(disguisable instanceof OfflinePlayer) {
									OfflinePlayer player = (OfflinePlayer)disguisable;
									if(player.isOnline() && DisguiseManager.getDisguise(player) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getDisguise(player)).getSkinName().equalsIgnoreCase(skinName)) {
										DisguiseManager.resendPackets(player.getPlayer());
									}
								} else {
									LivingEntity livingEntity = (LivingEntity)disguisable;
									if(DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getDisguise(livingEntity)).getSkinName().equalsIgnoreCase(skinName)) {
										DisguiseManager.resendPackets(livingEntity);
									}
								}
							}
						}
						
					});
				} else if(connection.getResponseCode() == 429 && name != null) {
					profilesById.put(uniqueId, profile);
					profilesByName.put(profile.getName().toLowerCase(Locale.ENGLISH), profile);
					final GameProfile emptyProfile = profile;
					Bukkit.getScheduler().runTaskLaterAsynchronously(iDisguise.getInstance(), new Runnable() {
						
						public void run() {
							loadGameProfile(emptyProfile.getId(), emptyProfile.getName());
						}
						
					}, 1200L);
				}
			}
			synchronized(currentlyLoadingById.get(uniqueId)) {
				currentlyLoadingById.remove(uniqueId).notifyAll();
			}
		} catch(Exception e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required profile information.", e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
				}
			}
		}
	}
	
	public String getCaseCorrectedName(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		if(profilesByName.containsKey(name)) {
			return profilesByName.get(name).getName();
		} else {
			loadGameProfile(name);
			if(profilesByName.containsKey(name)) {
				return profilesByName.get(name).getName();
			} else {
				return name;
			}
		}
	}
	
	public UUID getUniqueId(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		if(profilesByName.containsKey(name)) {
			return profilesByName.get(name).getId();
		} else {
			loadGameProfile(name);
			if(profilesByName.containsKey(name)) {
				return profilesByName.get(name).getId();
			} else {
				return UUID.randomUUID();
			}
		}
	}
	
	public GameProfile getGameProfile(UUID uniqueId, String skinName, String displayName) {
		GameProfile localProfile = new GameProfile(uniqueId, displayName.length() <= 16 ? displayName : skinName);
		if(profilesByName.containsKey(skinName.toLowerCase(Locale.ENGLISH))) {
			localProfile.getProperties().putAll(profilesByName.get(skinName.toLowerCase(Locale.ENGLISH)).getProperties());
		}
		return localProfile;
	}
	
	public void loadGameProfileAsynchronously(String skinName) {
		final String name = skinName.toLowerCase(Locale.ENGLISH);
		if(profilesByName.containsKey(name) || currentlyLoadingByName.containsKey(name)) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(iDisguise.getInstance(), new Runnable() {
			
			public void run() {
				loadGameProfile(name);
			}
			
		});
	}
	
	public void registerGameProfile(Player player) {
		if(player.getUniqueId().version() == 4) {
			try {
				GameProfile profile = (GameProfile)CraftPlayer_getProfile.invoke(player);
				profilesById.put(profile.getId(), profile);
				profilesByName.put(profile.getName().toLowerCase(Locale.ENGLISH), profile);
			} catch(Exception e) {
			}
		} else {
			loadGameProfileAsynchronously(player.getName());
		}
	}
	
	public boolean isGameProfileLoaded(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		return profilesByName.containsKey(name) || nonExistingProfiles.contains(name);
	}
	
	public void waitForGameProfile(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		if(currentlyLoadingByName.containsKey(name)) {
			synchronized(currentlyLoadingByName.get(name)) {
				try {
					currentlyLoadingByName.get(name).wait(10000L);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
}