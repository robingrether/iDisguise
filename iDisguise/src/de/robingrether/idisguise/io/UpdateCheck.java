package de.robingrether.idisguise.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.robingrether.idisguise.iDisguise;

public class UpdateCheck implements Runnable {
	
	public static final int PROJECT_ID = 46941;
	public static final String API_URL = "https://api.curseforge.com/servermods/files?projectIds=";
	public static final String API_DOWNLOAD_URL = "downloadUrl";
	public static final String API_FILE_NAME = "fileName";
	public static final String API_GAME_VERSION = "gameVersion";
	public static final String API_NAME = "name";
	public static final String API_RELEASE_TYPE = "releaseType";
	
	private iDisguise plugin;
	private String pluginVersion;
	private String latestVersion;
	private CommandSender toBeNotified;
	private String notification;
	
	public UpdateCheck(iDisguise plugin, CommandSender toBeNotified, String notification) {
		this.plugin = plugin;
		this.pluginVersion = plugin.getFullName();
		this.toBeNotified = toBeNotified;
		this.notification = notification;
	}
	
	public void run() {
		checkForUpdate();
		if(isUpdateAvailable()) {
			toBeNotified.sendMessage(String.format(notification, latestVersion));
		}
	}
	
	private boolean isUpdateAvailable() {
		return latestVersion != null && !pluginVersion.equals(latestVersion);
	}
	
	private void checkForUpdate() {
		BufferedReader reader = null;
		try {
			URL url = new URL(API_URL + PROJECT_ID);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", pluginVersion.replace(' ', '/') + " (by RobinGrether)");
			connection.setDoOutput(true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONArray array = (JSONArray)JSONValue.parse(response);
			latestVersion = null;
			JSONObject object = (JSONObject)array.get(array.size() - 1);
			latestVersion = (String)object.get(API_NAME);
		} catch(IOException e) {
			plugin.getLogger().log(Level.WARNING, "An error occured while checking for updates.", e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
				}
			}
		}
	}
	
}