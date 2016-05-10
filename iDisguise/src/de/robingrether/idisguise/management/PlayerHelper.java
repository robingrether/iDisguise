package de.robingrether.idisguise.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.robingrether.idisguise.iDisguise;

public class PlayerHelper {
	
	private static PlayerHelper instance;
	
	public static PlayerHelper getInstance() {
		return instance;
	}
	
	static void setInstance(PlayerHelper instance) {
		PlayerHelper.instance = instance;
	}
	
	private final String API_URL = "https://api.mojang.com/user/profiles/";
	private Map<Integer, Player> players;
	
	public PlayerHelper() {
		players = new HashMap<Integer, Player>();
		for(Player player : Reflection.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
	}
	
	public synchronized void addPlayer(Player player) {
		players.put(player.getEntityId(), player);
	}
	
	public synchronized void removePlayer(Player player) {
		players.remove(player.getEntityId());
	}
	
	public Player getPlayerByEntityId(int entityId) {
		return players.get(entityId);
	}
	
	public String getCaseCorrectedName(String name) {
		return name;
	}
	
	public UUID getUniqueId(String name) {
		return null;
	}
	
	public String getName(UUID uniqueId) {
		BufferedReader reader = null;
		try {
			URL url = new URL(API_URL + uniqueId.toString().replace("-", "") + "/names");
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", iDisguise.getInstance().getFullName().replace(' ', '/') + " (by RobinGrether)");
			connection.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONArray array = (JSONArray)JSONValue.parse(response);
			JSONObject object = (JSONObject)array.get(0);
			return (String)object.get("name");
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot convert the stored disguise data.", e);
			}
		} catch(NullPointerException e) {
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
				}
			}
		}
		return null;
	}
	
	public Object getGameProfile(String skinName, String displayName) {
		return null;
	}
	
}