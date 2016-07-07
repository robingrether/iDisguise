package de.robingrether.idisguise.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
	private String downloadUrl;
	private boolean autoDownload;
	
	public UpdateCheck(iDisguise plugin, CommandSender toBeNotified, boolean autoDownload) {
		this.plugin = plugin;
		this.pluginVersion = plugin.getFullName();
		this.toBeNotified = toBeNotified;
		this.autoDownload = autoDownload;
	}
	
	public void run() {
		checkForUpdate();
		if(isUpdateAvailable()) {
			toBeNotified.sendMessage(plugin.getLanguage().UPDATE_AVAILABLE.replace("%version%", latestVersion));
			if(autoDownload) {
				downloadUpdate();
			}
		}
	}
	
	private boolean isUpdateAvailable() {
		if(latestVersion != null && !pluginVersion.equals(latestVersion)) {
			try {
				int current = Integer.parseInt(pluginVersion.split(" ")[1].replace(".", ""));
				int latest = Integer.parseInt(latestVersion.split(" ")[1].replace(".", ""));
				return latest > current;
			} catch(NumberFormatException e) {
			} catch(ArrayIndexOutOfBoundsException e) {
			}
		}
		return false;
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
			downloadUrl = (String)object.get(API_DOWNLOAD_URL);
		} catch(Exception e) {
			plugin.getLogger().log(Level.WARNING, "Update checking failed: " + e.getClass().getSimpleName());
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
				}
			}
		}
	}
	
	private void downloadUpdate() {
		File oldFile = plugin.getPluginFile();
		File newFile = new File(plugin.getServer().getUpdateFolderFile(), oldFile.getName());
		if(newFile.exists()) {
			toBeNotified.sendMessage(plugin.getLanguage().UPDATE_ALREADY_DOWNLOADED);
		} else {
			InputStream input = null;
			OutputStream output = null;
			try {
				toBeNotified.sendMessage(plugin.getLanguage().UPDATE_DOWNLOADING);
				URL url = new URL(downloadUrl);
				URLConnection connection = url.openConnection();
				connection.addRequestProperty("User-Agent", pluginVersion.replace(' ', '/') + " (by RobinGrether)");
				connection.setDoOutput(true);
				input = connection.getInputStream();
				plugin.getServer().getUpdateFolderFile().mkdir();
				output = new FileOutputStream(newFile);
				int fetched;
				byte[] data = new byte[4096];
				while((fetched = input.read(data)) > 0) {
					output.write(data, 0, fetched);
				}
				input.close();
				output.close();
				toBeNotified.sendMessage(plugin.getLanguage().UPDATE_DOWNLOAD_SUCCEEDED);
			} catch(IOException e) {
				toBeNotified.sendMessage(plugin.getLanguage().UPDATE_DOWNLOAD_FAILED);
				plugin.getLogger().log(Level.WARNING, "Update download failed: " + e.getClass().getSimpleName());
			} finally {
				if(input != null) {
					try {
						input.close();
					} catch(IOException e) {
					}
				}
				if(output != null) {
					try {
						output.close();
					} catch(IOException e) {
					}
				}
			}
		}
	}
	
}