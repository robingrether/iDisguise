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
import org.bukkit.configuration.file.YamlConfiguration;
import de.robingrether.util.StringUtil;

public class Configuration {
	
	private ConcurrentHashMap<String, Setting> settings = new ConcurrentHashMap<String, Setting>();
	private File configurationFile;
	private File yamlConfigurationFile;
	
	public Configuration(File directory) {
		configurationFile = new File(directory, "config.txt");
		yamlConfigurationFile = new File(directory, "Config.yml");
		setDefault("save-disguises", true, "If set to true the disguises will be saved when the server is stopped");
		setDefault("prohibited-worlds", Arrays.asList("prohibited1", "prohibited2"), "Disguising is prohibited in the following worlds");
		setDefault("check-for-updates", true, "If set to true the plugin automatically checks if an update is available");
		setDefault("sound-system", true, "If set to true the sound system is enabled, for more information visit http://dev.bukkit.org/bukkit-plugins/idisguise/pages/sound-system/");
		setDefault("show-name-while-disguised", false, "If set to true every disguised player has his original name above his head");
		setDefault("no-target-while-disguised", false, "If set to true mobs cannot target disguised players");
		setDefault("entity-damage-while-disguised", true, "If set to false disguised players cannot be damaged");
		setDefault("permission-for-undisguise", false, "If set to true players must have \"iDisguise.undisguise\" permission to undisguise");
		setDefault("undisguise-on-hit", false, "If set to true players are undisguised when they are hit by another player or mob");
		setDefault("undisguise-on-projectile-hit", false, "If set to true players are undisguised when they are hit by a projectile (e.g. arrow, snowball)");
		setDefault("undisguise-on-hit-other", false, "If set to true players are undisguised when they hit another player");
		setDefault("ghost-disguises", true, "Enable/disable ghost disguises, you should disable this if you are using any scoreboard plugin");
	}
	
	public void loadData() {
		if(configurationFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configurationFile)));
				String line, lastLine = null;
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
						settings.put(split[0], new Setting(split[0], value, lastLine.substring(2)));
					}
					lastLine = line;
				}
				reader.close();
			} catch(Exception e) {
				System.err.println("[iDisguise] An error occured while loading the configuration.");
				e.printStackTrace();
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
				writer.write("# " + setting.description() + "\n" + setting.key() + ": ");
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
				writer.write("\n");
			}
			writer.close();
		} catch(Exception e) {
			System.err.println("[iDisguise] An error occured while saving the configuration.");
			e.printStackTrace();
		}
	}
	
	public String getString(String key) {
		return settings.get(key) != null ? settings.get(key).stringValue() : null;
	}
	
	public List<String> getStringList(String key) {
		return settings.get(key) != null ? settings.get(key).listValue() : null;
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