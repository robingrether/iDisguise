package de.robingrether.idisguise.management.impl.v1_5_R2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PlayerHelper;

public class PlayerHelperImpl extends PlayerHelper {
	
	public static final String API_URL = "https://api.mojang.com/user/profiles/";
	
	public PlayerHelperImpl() {
		players = new HashMap<Integer, Player>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			players.put(player.getEntityId(), player);
		}
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
			connection.addRequestProperty("User-Agent", ((iDisguise)Bukkit.getPluginManager().getPlugin("iDisguise")).getFullName().replace(' ', '/') + " (by RobinGrether)");
			connection.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONArray array = (JSONArray)JSONValue.parse(response);
			JSONObject object = (JSONObject)array.get(0);
			return (String)object.get("name");
		} catch(IOException e) {
			Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "An error occured while converting saved disguises.", e);
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
	
	public Object getGameProfile(String name) {
		return null;
	}
	
}