package de.robingrether.idisguise.io;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.robingrether.idisguise.iDisguise;

public class Language {
	
	public String RELOAD_COMPLETE = ChatColor.GOLD + "[iDisguise] Reload complete.";
	public String NO_PERMISSION = ChatColor.RED + "You are not allowed to do this.";
	public String CONSOLE_USE_OTHER_COMMAND = ChatColor.RED + "Use /odisguise from the server console.";
	public String CANNOT_FIND_PLAYER = ChatColor.RED + "Cannot find player %player%.";
	public String WRONG_USAGE_NO_NAME = ChatColor.RED + "Wrong usage: account name required";
	public String INVALID_NAME = ChatColor.RED + "The given account name is invalid.";
	public String EVENT_CANCELLED = ChatColor.RED + "Another plugin prohibits you to do that.";
	public String DISGUISE_PLAYER_SUCCESS_SELF = ChatColor.GOLD + "You disguised as a %type% called %name%.";
	public String DISGUISE_PLAYER_SUCCESS_OTHER = ChatColor.GOLD + "%player% disguised as a %type% called %name%.";
	public String DISGUISE_SUCCESS_SELF = ChatColor.GOLD + "You disguised as a %type%.";
	public String DISGUISE_SUCCESS_OTHER = ChatColor.GOLD + "%player% disguised as a %type%.";
	public String STATUS_PLAYER_SELF = ChatColor.GOLD + "You are disguised as a %type% called %name%.";
	public String STATUS_PLAYER_OTHER = ChatColor.GOLD + "%player% is disguised as a %type% called %name%.";
	public String STATUS_SELF = ChatColor.GOLD + "You are disguised as a %type%.";
	public String STATUS_OTHER = ChatColor.GOLD + "%player% is disguised as a %type%.";
	public String STATUS_SUBTYPES = ChatColor.GRAY + "(%subtypes%)";
	public String STATUS_NOT_DISGUISED_SELF = ChatColor.GOLD + "You are not disguised.";
	public String STATUS_NOT_DISGUISED_OTHER = ChatColor.GOLD + "%player% is not disguised.";
	public String OUTDATED_SERVER = ChatColor.RED + "Your Minecraft version does not support the given disguise type.";
	public String UNDISGUISE_CONSOLE = ChatColor.RED + "You are not a player so you cannot undisguise.";
	public String UNDISGUISE_NOT_DISGUISED_SELF = ChatColor.RED + "You are not disguised.";
	public String UNDISGUISE_NOT_DISGUISED_OTHER = ChatColor.RED + "%player% is not disguised.";
	public String UNDISGUISE_SUCCESS_SELF = ChatColor.GOLD + "You undisguised.";
	public String UNDISGUISE_SUCCESS_OTHER = ChatColor.GOLD + "%player% undisguised.";
	public String UNDISGUISE_SUCCESS_ALL = ChatColor.GOLD + "%share% out of %total% disguised players undisguised.";
	public String UNDISGUISE_SUCCESS_ALL_IGNORE = ChatColor.GOLD + "Undisguised every disguised player ignoring other plugins.";
	public String HELP_INFO = ChatColor.GREEN + "%name% %version% - Help";
	public String HELP_BASE = ChatColor.GOLD + (ChatColor.ITALIC + " %command% " + ChatColor.GOLD + "- %description%");
	public String HELP_TYPES = ChatColor.GRAY + "Types: %types%";
	public String HELP_HELP = "Shows this message";
	public String HELP_PLAYER_SELF = "Disguise yourself as a player";
	public String HELP_PLAYER_OTHER = "Disguise a player as a player";
	public String HELP_GHOST_SELF = "Disguise yourself as a ghost";
	public String HELP_GHOST_OTHER = "Disguise a player as a ghost";
	public String HELP_RANDOM_SELF = "Disguise yourself as a randomly chosen mob";
	public String HELP_RANDOM_OTHER = "Disguise a player as a randomly chosen mob";
	public String HELP_RELOAD = "Reload config and language file";
	public String HELP_STATUS_SELF = "Shows your disguise status";
	public String HELP_STATUS_OTHER = "Shows a player's disguise status";
	public String HELP_UNDISGUISE_SELF = "Undisguise yourself";
	public String HELP_UNDISGUISE_ALL = "Undisguise everyone";
	public String HELP_UNDISGUISE_OTHER = "Undisguise a player";
	public String HELP_DISGUISE_SELF = "Disguise yourself as a mob with optional subtypes";
	public String HELP_DISGUISE_OTHER = "Disguise a player as a mob with optional subtypes";
	public String HELP_SUBTYPE = "Apply one (or multiple) subtypes";
	public String UNDISGUISE_GHOST_DISABLED = ChatColor.GOLD + "You were undisguised because ghost disguises are disabled.";
	public String JOIN_DISGUISED = ChatColor.GOLD + "You are still disguised. Use " + ChatColor.ITALIC + "/disguise status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.";
	public String MOVE_AS_SHULKER = ChatColor.RED + "You must not move while you are disguised as a shulker.";
	public String UPDATE_AVAILABLE = ChatColor.GOLD + "[iDisguise] An update is available: %version%";
	public String UPDATE_ALREADY_DOWNLOADED = ChatColor.GOLD + "[iDisguise] Update already downloaded. (Restart server to apply update)";
	public String UPDATE_DOWNLOADING = ChatColor.GOLD + "[iDisguise] Downloading update...";
	public String UPDATE_DOWNLOAD_SUCCEEDED = ChatColor.GOLD + "[iDisguise] Download succeeded. (Restart server to apply update)";
	public String UPDATE_DOWNLOAD_FAILED = ChatColor.RED + "[iDisguise] Download failed.";
	public String UPDATE_OPTION = ChatColor.GOLD + "[iDisguise] You can enable automatic updates in the config file.";
	public String EASTER_EGG_BIRTHDAY = ChatColor.YELLOW + "YAAAY!!! Today is my birthday! I'm %age% years old now.";
	
	private iDisguise plugin;
	
	public Language(iDisguise plugin) {
		this.plugin = plugin;
	}
	
	public void loadData() {
		File languageFile = new File(plugin.getDataFolder(), "language.yml");
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(languageFile);
		try {
			for(Field field : getClass().getDeclaredFields()) {
				if(field.getType().equals(String.class)) {
					if(fileConfiguration.isString(field.getName().toLowerCase(Locale.ENGLISH).replace('_', '-'))) {
						field.set(this, fileConfiguration.getString(field.getName().toLowerCase(Locale.ENGLISH).replace('_', '-')));
					}
				}
			}
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while loading the language file.", e);
		}
	}
	
	public void saveData() {
		File languageFile = new File(plugin.getDataFolder(), "language.yml");
		FileConfiguration fileConfiguration = new YamlConfiguration();
		try {
			for(Field field : getClass().getDeclaredFields()) {
				if(field.getType().equals(String.class)) {
					fileConfiguration.set(field.getName().toLowerCase(Locale.ENGLISH).replace('_', '-'), field.get(this));
				}
			}
			fileConfiguration.save(languageFile);
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while saving the language file.", e);
		}
	}
	
}