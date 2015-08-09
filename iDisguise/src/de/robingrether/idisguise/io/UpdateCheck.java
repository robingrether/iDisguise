package de.robingrether.idisguise.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateCheck implements Runnable {
	
	public static final int PROJECT_ID = 46941;
	public static final String API_URL = "https://api.curseforge.com/servermods/files?projectIds=";
	public static final String API_DOWNLOAD_URL = "downloadUrl";
	public static final String API_FILE_NAME = "fileName";
	public static final String API_GAME_VERSION = "gameVersion";
	public static final String API_NAME = "name";
	public static final String API_RELEASE_TYPE = "releaseType";
	
	private String pluginVersion;
	private String latestVersion;
	private CommandSender toBeNotified;
	private String notification;
	
	public UpdateCheck(String pluginVersion, CommandSender toBeNotified, String notification) {
		this.pluginVersion = pluginVersion;
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
		try {
			URL url = new URL(API_URL + PROJECT_ID);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", pluginVersion.replace(' ', '/') + " (by Robingrether)");
			connection.setDoOutput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String response = reader.readLine();
			JSONArray array = (JSONArray)JSONValue.parse(response);
			latestVersion = null;
			for(int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject)array.get(i);
				Pattern pattern = Pattern.compile("\\D*([0-9\\.]*).*");
				Matcher matcher = pattern.matcher((String)object.get(API_GAME_VERSION));
				if(matcher.matches() && matcher.group(1).equals(getMCVersion())) {
					latestVersion = (String)object.get(API_NAME);
				}
			}
		} catch(Exception e) {
			System.err.println("[iDisguise] An error occured while checking for updates.");
		}
	}
	
	private static String getMCVersion() {
		Pattern pattern = Pattern.compile("[^\\(]*\\(MC:\\s*([0-9\\.]*).*");
		Matcher matcher = pattern.matcher(Bukkit.getVersion());
		if(matcher.matches() && matcher.group(1) != null) {
			return matcher.group(1);
		} else {
			return "";
		}
	}
	
}