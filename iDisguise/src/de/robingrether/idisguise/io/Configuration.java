package de.robingrether.idisguise.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.util.StringUtil;

public class Configuration {
	
	private ConcurrentHashMap<String, Setting> settings = new ConcurrentHashMap<String, Setting>();
	private iDisguise plugin;
	private File configurationFile;
	private File yamlConfigurationFile;
	
	public Configuration(iDisguise plugin, File directory) {
		this.plugin = plugin;
		configurationFile = new File(directory, "config.txt");
		yamlConfigurationFile = new File(directory, "Config.yml");
		setDefault("save-disguises", true, "When this option is set to true, all the disguises are saved when the server shuts down,\nso all the players are still disguised after a restart.");
		setDefault("prohibited-worlds", Arrays.asList("prohibited1", "prohibited2"), "You can put the worlds, you don't want your players to disguise in, here.\nYou can give admins the 'iDisguise.everywhere' permission so they can bypass this prohibition.");
		setDefault("check-for-updates", true, "Enable this if you want the plugin to check for an update when the server starts.\nIf an update is available a message will be printed out into console,\nand every player who has the 'iDisguise.update' permission will receive a message.");
		setDefault("sound-system", true, "When this option is set to true, the plugin will play realistic hurt/death/idle sounds for disguised players.\nVisit http://dev.bukkit.org/bukkit-plugins/idisguise/pages/sound-system/ for more information.\n(Not every mob type is supported so far)");
		setDefault("show-name-while-disguised", false, "When this option is set to true, all disguised players will have their name above their head.\nThis only works for mob disguises.");
		setDefault("no-target-while-disguised", false, "When this option is set to true, disguised players cannot be targeted by mobs (e.g. skeletons).");
		setDefault("entity-damage-while-disguised", true, "When this option is set to false, disguised players cannot be damaged by mobs (e.g. zombies).");
		setDefault("permission-for-undisguise", false, "When this option is set to true, disguised players need the 'iDisguise.undisguise' permission,\notherwise they cannot undisguise themselves anymore.");
		setDefault("undisguise-on-hit", false, "When this option is set to true, a disguised player will be undisguised as soon as he is hit by another player.\nATTENTION: The player will not get notified about this!");
		setDefault("undisguise-on-projectile-hit", false, "When this option is set to true, a disguised player will be undisguised as soon as he is hit by a projectile (e.g. arrows).\nATTENTION: The player will not get notified about this!");
		setDefault("undisguise-on-hit-other", false, "When this option is set to true, a disguised player will be undisguised as soon as he attacks another player.\nATTENTION: The player will not get notified about this!");
		setDefault("ghost-disguises", true, "Enable or disable ghost disguises.\nYou should disable this if you use any scoreboard plugin(s).");
		setDefault("prohibited-player-disguises", Arrays.asList("player1", "player2"), "You can put the player names, you don't want your players to disguise as, here.\nYou can give admins the 'iDisguise.player.prohibited' permission so they can bypass this prohibition.");
	}
	
	public void loadData() {
		if(configurationFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configurationFile)));
				String line;
				while((line = reader.readLine()) != null) {
					if(!line.startsWith("#") && line.contains(":")) {
						String[] split = line.split("\\s*:\\s*", 2);
						Object value = null;
						if(StringUtil.equals(split[1], "true", "false")) {
							value = Boolean.parseBoolean(split[1]);
						} else if(split[1].matches("[+|-]?\\d+(\\.\\d+)?")) {
							value = split[1].contains(".") ? Double.parseDouble(split[1]) : Integer.parseInt(split[1]);
						} else if(split[1].startsWith("{") && split[1].trim().endsWith("}")) {
							value = Arrays.asList(split[1].replaceAll("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)", "").replaceAll("[\"|{|}]", "").split(","));
						} else {
							value = split[1].replaceAll("(^\"|\"$)", "");
						}
						Setting setting = settings.get(split[0]);
						if(setting != null && value.getClass().isAssignableFrom(setting.value().getClass())) {
							settings.put(split[0], new Setting(split[0], value, setting.description()));
						}
					}
				}
				reader.close();
			} catch(Exception e) {
				plugin.getLogger().log(Level.SEVERE, "An error occured while loading the configuration.", e);
			}
		} else if(yamlConfigurationFile.exists()) {
			YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(yamlConfigurationFile);
			for(Setting setting : settings.values()) {
				if(yamlConfiguration.contains(setting.key())) {
					settings.put(setting.key(), new Setting(setting.key(), yamlConfiguration.get(setting.key()), setting.description()));
				}
			}
			yamlConfigurationFile.delete();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveData() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configurationFile)));
			for(Setting setting : new TreeMap<String, Setting>(settings).values()) {
				writer.write("# " + setting.description().replace("\n", "\n# ") + "\n" + setting.key() + ": ");
				Object value = setting.value();
				if(value instanceof String) {
					writer.write((String)value);
				} else if(value instanceof List) {
					writer.write("{");
					List<String> list = (List<String>)value;
					writer.write(list.size() > 0 ? "\"" + list.get(0) + "\"" : "");
					for(int i = 1; i < list.size(); i++) {
						writer.write(",\"" + list.get(i) + "\"");
					}
					writer.write("}");
				} else if(value instanceof Boolean) {
					writer.write(Boolean.toString((Boolean)value));
				} else if(value instanceof Double) {
					writer.write(Double.toString((Double)value));
				} else if(value instanceof Integer) {
					writer.write(Integer.toString((Integer)value));
				} else {
					writer.write("\"" + value.toString() + "\"");
				}
				writer.write("\n\n");
			}
			writer.close();
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured while saving the configuration.", e);
		}
	}
	
	public String getString(String key) {
		return settings.get(key) != null ? settings.get(key).stringValue() : null;
	}
	
	public List<String> getStringList(String key) {
		return settings.get(key) != null ? settings.get(key).listValue() : Arrays.asList(new String[0]);
	}
	
	public boolean getBoolean(String key) {
		return settings.get(key) != null ? settings.get(key).booleanValue() : false;
	}
	
	public double getDouble(String key) {
		return settings.get(key) != null ? settings.get(key).doubleValue() : 0.0;
	}
	
	public int getInt(String key) {
		return settings.get(key) != null ? settings.get(key).intValue() : 0;
	}
	
	public void setDefault(String key, Object defaultValue, String description) {
		if(!settings.containsKey(key)) {
			settings.put(key, new Setting(key, defaultValue, description));
		}
	}
	
	public void setString(String key, String value, String description) {
		settings.put(key, new Setting(key, value, description));
	}
	
	public void setStringList(String key, List<String> value, String description) {
		settings.put(key, new Setting(key, value, description));
	}
	
	public void setBoolean(String key, boolean value, String description) {
		settings.put(key, new Setting(key, value, description));
	}
	
	public void setDouble(String key, double value, String description) {
		settings.put(key, new Setting(key, value, description));
	}
	
	public void setInt(String key, int value, String description) {
		settings.put(key, new Setting(key, value, description));
	}
	
	public static class Setting {
		
		private final String key;
		private final Object value;
		private final String description;
		
		public Setting(String key, Object value, String description) {
			this.key = key;
			this.value = value;
			this.description = description;
		}
		
		public String key() {
			return key;
		}
		
		public String description() {
			return description;
		}
		
		public Object value() {
			return value;
		}
		
		public String stringValue() {
			return value instanceof String ? (String)value : null;
		}
		
		public List<String> listValue() {
			return value instanceof List ? (List<String>)value : null;
		}
		
		public boolean booleanValue() {
			return value instanceof Boolean ? (Boolean)value : false;
		}
		
		public double doubleValue() {
			return value instanceof Double ? (Double)value : 0.0;
		}
		
		public int intValue() {
			return value instanceof Integer ? (Integer)value : 0;
		}
		
	}
	
}