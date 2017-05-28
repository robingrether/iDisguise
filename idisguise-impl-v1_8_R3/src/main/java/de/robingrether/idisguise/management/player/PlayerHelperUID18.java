package de.robingrether.idisguise.management.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PlayerHelper;

public class PlayerHelperUID18 extends PlayerHelper {
	
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
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", iDisguise.getInstance().getFullName());
			connection.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONObject object = (JSONObject)JSONValue.parse(response);
			UUID uniqueId = UUID.fromString(((String)object.get(API_NAME_ID)).replaceFirst("([0-9a-f]{8})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{12})", "$1-$2-$3-$4-$5"));
			loadGameProfile(uniqueId);
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
	
	private void loadGameProfile(UUID uniqueId) {
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
			URL url = new URL(API_UID_URL + uniqueId.toString().replace("-", "") + "?unsigned=false");
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", iDisguise.getInstance().getFullName());
			connection.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONObject object = (JSONObject)JSONValue.parse(response);
			String name = (String)object.get(API_UID_NAME);
			GameProfile profile = new GameProfile(uniqueId, name);
			JSONArray array = (JSONArray)object.get(API_UID_PROPERTIES);
			for(Object obj : array) {
				JSONObject property = (JSONObject)obj;
				String propertyName = (String)property.get(API_UID_NAME);
				profile.getProperties().put(propertyName, new Property(propertyName, (String)property.get(API_UID_VALUE), (String)property.get(API_UID_SIGNATURE)));
			}
			profilesById.put(uniqueId, profile);
			profilesByName.put(name.toLowerCase(Locale.ENGLISH), profile);
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
	
	public String getName(UUID uniqueId) {
		if(profilesById.containsKey(uniqueId)) {
			return profilesById.get(uniqueId).getName();
		} else {
			loadGameProfile(uniqueId);
			if(profilesById.containsKey(uniqueId)) {
				return profilesById.get(uniqueId).getName();
			} else {
				return null;
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
	
	public void loadGameProfileAsynchronously(final UUID uniqueId) {
		if(profilesById.containsKey(uniqueId) || currentlyLoadingById.containsKey(uniqueId)) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(iDisguise.getInstance(), new Runnable() {
			
			public void run() {
				loadGameProfile(uniqueId);
			}
			
		});
	}
	
	public boolean isGameProfileLoaded(String name) {
		return profilesByName.containsKey(name.toLowerCase(Locale.ENGLISH));
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