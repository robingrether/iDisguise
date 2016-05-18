package de.robingrether.idisguise.io;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import de.robingrether.idisguise.iDisguise;

public class Configuration {
	
	public static final String ENABLE_GHOST_DISGUISE_PATH = "disguise.enable-ghost-disguise";
	public static final String KEEP_DISGUISE_LEAVE_PATH = "disguise.keep-disguise-leave";
	public static final String KEEP_DISGUISE_SHUTDOWN_PATH = "disguise.keep-disguise-shutdown";
	public static final String MODIFY_MESSAGE_DEATH_PATH = "disguise.modify-message-death";
	public static final String MODIFY_MESSAGE_JOIN_PATH = "disguise.modify-message-join";
	public static final String MODIFY_MESSAGE_KILL_PATH = "disguise.modify-message-kill";
	public static final String MODIFY_MESSAGE_LEAVE_PATH = "disguise.modify-message-leave";
	public static final String MODIFY_PLAYER_LIST_ENTRY_PATH = "disguise.modify-player-list-entry";
	public static final String NAME_TAG_SHOWN_PATH = "disguise.name-tag-shown";
	public static final String NAME_TAG_ALWAYS_VISIBLE_PATH ="disguise.name-tag-always-visible";
	public static final String REPLACE_SOUND_EFFECTS_PATH = "disguise.replace-sound-effects";
	public static final String RESTRICTED_PLAYER_NAMES_PATH = "commands.restricted-player-names";
	public static final String RESTRICTED_WORLDS_PATH = "commands.restricted-worlds";
	public static final String UNDISGUISE_PERMISSION_PATH = "commands.undisguise-permission";
	public static final String UPDATE_CHECK_PATH = "updates.check";
	public static final String UPDATE_DOWNLOAD_PATH = "updates.download";
	
	public boolean ENABLE_GHOST_DISGUISE = false;
	public boolean KEEP_DISGUISE_LEAVE = true;
	public boolean KEEP_DISGUISE_SHUTDOWN = true;
	public boolean MODIFY_MESSAGE_DEATH = false;
	public boolean MODIFY_MESSAGE_JOIN = false;
	public boolean MODIFY_MESSAGE_KILL = false;
	public boolean MODIFY_MESSAGE_LEAVE = false;
	public boolean MODIFY_PLAYER_LIST_ENTRY = false;
	public boolean NAME_TAG_SHOWN = false;
	public boolean NAME_TAG_ALWAYS_VISIBLE = false;
	public boolean REPLACE_SOUND_EFFECTS = true;
	public List<String> RESTRICTED_PLAYER_NAMES = Arrays.asList("player1", "player2");
	public List<String> RESTRICTED_WORLDS = Arrays.asList("world1", "world2");
	public boolean UNDISGUISE_PERMISSION = false;
	public boolean UPDATE_CHECK = true;
	public boolean UPDATE_DOWNLOAD = false;
	
	private iDisguise plugin;
	
	public Configuration(iDisguise plugin) {
		this.plugin = plugin;
	}
	
	public void loadData() {
		File configurationFile = new File(plugin.getDataFolder(), "config.yml");
		if(!configurationFile.exists()) {
			plugin.saveDefaultConfig();
		}
		plugin.reloadConfig();
		FileConfiguration fileConfiguration = plugin.getConfig();
		try {
			for(Field pathField : getClass().getDeclaredFields()) {
				if(pathField.getName().endsWith("_PATH")) {
					Field valueField = getClass().getDeclaredField(pathField.getName().substring(0, pathField.getName().length() - 5));
					if(fileConfiguration.isSet((String)pathField.get(null))) {
						if(fileConfiguration.isBoolean((String)pathField.get(null))) {
							valueField.setBoolean(this, fileConfiguration.getBoolean((String)pathField.get(null), valueField.getBoolean(this)));
						} else if(fileConfiguration.isList((String)pathField.get(null))) {
							valueField.set(this, fileConfiguration.getList((String)pathField.get(null), (List<String>)valueField.get(this)));
						}
					}
				}
			}
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while loading the config file.", e);
		}
	}
	
	public void saveData() {
		FileConfiguration fileConfiguration = plugin.getConfig();
		try {
			for(Field pathField : getClass().getDeclaredFields()) {
				if(pathField.getName().endsWith("_PATH")) {
					Field valueField = getClass().getDeclaredField(pathField.getName().substring(0, pathField.getName().length() - 5));
					if(valueField.getType() == boolean.class) {
						fileConfiguration.set((String)pathField.get(null), valueField.getBoolean(this));
					} else {
						fileConfiguration.set((String)pathField.get(null), valueField.get(this));
					}
				}
			}
			plugin.saveConfig();
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while saving the config file.", e);
		}
	}
	
}