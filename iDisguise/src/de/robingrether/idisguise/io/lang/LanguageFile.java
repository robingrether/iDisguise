package de.robingrether.idisguise.io.lang;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

@Deprecated
public class LanguageFile {
	
	private HashMap<String, String> lang = new HashMap<String, String>();
	private boolean loaded = false;
	private String localization;
	
	public LanguageFile(String localization) {
		this.localization = localization;
		loadFile();
	}
	
	public void loadFile() {
		YamlConfiguration config = getConfig();
		if(config == null) {
			return;
		}
		loadString(config, "update.available");
		loadString(config, "plugin.enabled");
		loadString(config, "plugin.disabled");
		loadString(config, "cmd.disguise.console");
		loadString(config, "cmd.disguise.badnumber");
		loadString(config, "cmd.disguise.badworld");
		loadString(config, "cmd.disguise.block.badid");
		loadString(config, "cmd.disguise.noperm");
		loadString(config, "cmd.disguise.player.longname");
		loadString(config, "cmd.disguise.reload");
		loadString(config, "cmd.disguise.success.ghost");
		loadString(config, "cmd.disguise.success.mob");
		loadString(config, "cmd.disguise.success.player");
		loadString(config, "cmd.disguise.stats.baby");
		loadString(config, "cmd.disguise.stats.ghost");
		loadString(config, "cmd.disguise.stats.mob");
		loadString(config, "cmd.disguise.stats.player");
		loadString(config, "cmd.disguise.stats.not");
		loadString(config, "cmd.disguise.un.success");
		loadString(config, "cmd.disguise.un.not");
		loadString(config, "cmd.disguise.unall.success");
		loadString(config, "listener.join.disguised");
		loadString(config, "listener.worldchange.un");
		loadString(config, "mob.bat");
		loadString(config, "mob.blaze");
		loadString(config, "mob.block");
		loadString(config, "mob.cave-spider");
		loadString(config, "mob.charged-creeper");
		loadString(config, "mob.chicken");
		loadString(config, "mob.cow");
		loadString(config, "mob.creeper");
		loadString(config, "mob.donkey");
		loadString(config, "mob.ender-crystal");
		loadString(config, "mob.ender-dragon");
		loadString(config, "mob.enderman");
		loadString(config, "mob.ghast");
		loadString(config, "mob.giant");
		loadString(config, "mob.horse");
		loadString(config, "mob.iron-golem");
		loadString(config, "mob.magma-cube");
		loadString(config, "mob.mule");
		loadString(config, "mob.mushroom-cow");
		loadString(config, "mob.ocelot");
		loadString(config, "mob.pig");
		loadString(config, "mob.pig-zombie");
		loadString(config, "mob.primed-tnt");
		loadString(config, "mob.sheep");
		loadString(config, "mob.silverfish");
		loadString(config, "mob.skeleton");
		loadString(config, "mob.skeleton-horse");
		loadString(config, "mob.slime");
		loadString(config, "mob.snowman");
		loadString(config, "mob.spider");
		loadString(config, "mob.squid");
		loadString(config, "mob.undead-horse");
		loadString(config, "mob.villager");
		loadString(config, "mob.witch");
		loadString(config, "mob.wither");
		loadString(config, "mob.wither-skeleton");
		loadString(config, "mob.wolf");
		loadString(config, "mob.zombie");
		loaded = true;
	}
	
	private void loadString(YamlConfiguration config, String name) {
		lang.put(name, config.getString(name));
	}
	
	private YamlConfiguration getConfig() {
		YamlConfiguration config = null;
		if(localization.equalsIgnoreCase("local")) {
			localization = "local";
			File file = new File("plugins/iDisguise/lang.yml");
			if(file.exists()) {
				config = YamlConfiguration.loadConfiguration(file);
			} else {
				config = YamlConfiguration.loadConfiguration(new InputStreamReader(getClass().getResourceAsStream("enUS.yml")));
				try {
					config.save(file);
				} catch(Exception e) {
					System.out.println("[iDisguise] Can't save language file");
				}
			}
		} else {
			InputStreamReader input = getClass().getResourceAsStream(localization + ".yml") == null ? null : new InputStreamReader(getClass().getResourceAsStream(localization + ".yml"));
			if(input == null) {
				localization = "enUS";
				input = new InputStreamReader(getClass().getResourceAsStream(localization + ".yml"));
			}
			config = YamlConfiguration.loadConfiguration(input);
		}
		return config;
	}
	
	public String getLocalization() {
		return localization;
	}
	
	public String getString(String name) {
		if(!loaded) {
			if(name.equals("plugin.enabled")) {
				return "iDisguise v%s enabled!";
			} else if(name.equals("plugin.disabled")) {
				return "iDisguise v%s disabled!";
			} else {
				return " ";
			}
		} else {
			if(lang.get(name) != null) {
				return lang.get(name);
			} else {
				return " ";
			}
		}
	}
	
}