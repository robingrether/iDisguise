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
	public String CANNOT_FIND_ENTITY = ChatColor.RED + "Cannot find valid living entity with EID %id%.";
	public String WRONG_USAGE_NO_NAME = ChatColor.RED + "Wrong usage: account name required";
	public String WRONG_USAGE_TWO_DISGUISE_TYPES = ChatColor.RED + "Wrong usage: two disguise types given";
	public String WRONG_USAGE_UNKNOWN_ARGUMENTS = ChatColor.RED + "Wrong usage: unknown arguments " + ChatColor.ITALIC + "%arguments%";
	public String WRONG_USAGE_SEE_THROUGH = ChatColor.RED + "Wrong usage: unknown argument " + ChatColor.ITALIC + "%argument%";
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
	public String UNDISGUISE_SUCCESS_ALL_ONLINE = ChatColor.GOLD + "%share% out of %total% online disguised players undisguised.";
	public String UNDISGUISE_SUCCESS_ALL_OFFLINE = ChatColor.GOLD + "%share% out of %total% offline disguised players undisguised.";
	public String UNDISGUISE_SUCCESS_ALL_ENTITIES = ChatColor.GOLD + "%share% out of %total% disguised entities undisguised.";
	public String HELP_INFO = ChatColor.GREEN + "%name% %version% - Help";
	public String HELP_BASE = ChatColor.GOLD + (ChatColor.ITALIC + " %command% " + ChatColor.GOLD + "- %description%");
	public String HELP_TYPES = ChatColor.GRAY + "Types: %types%";
	public String HELP_TYPES_AVAILABLE = "%type%";
	public String HELP_TYPES_NOT_SUPPORTED = ChatColor.STRIKETHROUGH + "%type%";
	public String HELP_TYPES_NO_PERMISSION = ChatColor.STRIKETHROUGH + "%type%";
	public String HELP_HELP = "Shows this message";
	public String HELP_PLAYER_SELF = "Disguise yourself as a player";
	public String HELP_PLAYER_OTHER = "Disguise a player as a player";
	public String HELP_RANDOM_SELF = "Disguise yourself as a randomly chosen mob";
	public String HELP_RANDOM_OTHER = "Disguise a player as a randomly chosen mob";
	public String HELP_RELOAD = "Reload config and language file";
	public String HELP_SEE_THROUGH_SELF = "Indicate or toggle see-through mode for yourself";
	public String HELP_SEE_THROUGH_OTHER = "Indicate or toggle see-through mode for a player";
	public String HELP_STATUS_SELF = "Shows your disguise status";
	public String HELP_STATUS_OTHER = "Shows a player's disguise status";
	public String HELP_UNDISGUISE_SELF = "Undisguise yourself";
	public String HELP_UNDISGUISE_ALL_NEW = "Undisguise everyone (*), all online players (*o), all players (*p) or all entities (*e)";
	public String HELP_UNDISGUISE_OTHER = "Undisguise a player";
	public String HELP_DISGUISE_SELF = "Disguise yourself as a mob with optional subtypes";
	public String HELP_DISGUISE_OTHER = "Disguise a player as a mob with optional subtypes";
	public String HELP_SUBTYPE = "Apply one (or multiple) subtypes";
	public String JOIN_DISGUISED = ChatColor.GOLD + "You are still disguised. Use " + ChatColor.ITALIC + "/disguise status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.";
	public String MOVE_AS_SHULKER = ChatColor.RED + "You must not move while you are disguised as a shulker.";
	public String UPDATE_AVAILABLE = ChatColor.GOLD + "[iDisguise] An update is available: %version%";
	public String UPDATE_ALREADY_DOWNLOADED = ChatColor.GOLD + "[iDisguise] Update already downloaded. (Restart server to apply update)";
	public String UPDATE_DOWNLOADING = ChatColor.GOLD + "[iDisguise] Downloading update...";
	public String UPDATE_DOWNLOAD_SUCCEEDED = ChatColor.GOLD + "[iDisguise] Download succeeded. (Restart server to apply update)";
	public String UPDATE_DOWNLOAD_FAILED = ChatColor.RED + "[iDisguise] Download failed.";
	public String UPDATE_OPTION = ChatColor.GOLD + "[iDisguise] You can enable automatic updates in the config file.";
	public String EASTER_EGG_BIRTHDAY = ChatColor.YELLOW + "YAAAY!!! Today is my birthday! I'm %age% years old now.";
	public String SEE_THROUGH_STATUS_ON_SELF = ChatColor.GOLD + "See-through mode is currently " + ChatColor.GREEN + "enabled " + ChatColor.GOLD + "for you.";
	public String SEE_THROUGH_STATUS_ON_OTHER = ChatColor.GOLD + "See-through mode is currently " + ChatColor.GREEN + "enabled " + ChatColor.GOLD + "for %player%.";
	public String SEE_THROUGH_STATUS_OFF_SELF = ChatColor.GOLD + "See-through mode is currently " + ChatColor.RED + "disabled " + ChatColor.GOLD + "for you.";
	public String SEE_THROUGH_STATUS_OFF_OTHER = ChatColor.GOLD + "See-through mode is currently " + ChatColor.RED + "disabled " + ChatColor.GOLD + "for %player%.";
	public String SEE_THROUGH_ENABLE_SELF = ChatColor.GOLD + "See-through mode is now " + ChatColor.GREEN + "enabled " + ChatColor.GOLD + "for you.";
	public String SEE_THROUGH_ENABLE_OTHER = ChatColor.GOLD + "See-through mode is now " + ChatColor.GREEN + "enabled " + ChatColor.GOLD + "for %player%.";
	public String SEE_THROUGH_DISABLE_SELF = ChatColor.GOLD + "See-through mode is now " + ChatColor.RED + "disabled " + ChatColor.GOLD + "for you.";
	public String SEE_THROUGH_DISABLE_OTHER = ChatColor.GOLD + "See-through mode is now " + ChatColor.RED + "disabled " + ChatColor.GOLD + "for %player%.";
	public String SEE_THROUGH_ENTITY = ChatColor.RED + "See-through mode is only for players.";
	public String HELP_TARGET_TITLE = ChatColor.GRAY + "Target:";
	public String HELP_TARGET_UID = ChatColor.GRAY + " <xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx> - Match player by UID";
	public String HELP_TARGET_EID = ChatColor.GRAY + " [entity-id] - Match entity/player by entity id";
	public String HELP_TARGET_NAME_EXACT = ChatColor.GRAY + " {player-name} - Match player by EXACT name";
	public String HELP_TARGET_NAME_MATCH = ChatColor.GRAY + " player-name - Match online player";
	
	public String DISGUISE_ALIAS_BAT = "";
	public String DISGUISE_ALIAS_BLAZE = "";
	public String DISGUISE_ALIAS_CAVE_SPIDER = "cavespider, bluespider, blue-spider";
	public String DISGUISE_ALIAS_CHICKEN = "chick";
	public String DISGUISE_ALIAS_COW = "cattle, ox";
	public String DISGUISE_ALIAS_CREEPER = "";
	public String DISGUISE_ALIAS_DONKEY = "";
	public String DISGUISE_ALIAS_ELDER_GUARDIAN = "";
	public String DISGUISE_ALIAS_ENDER_DRAGON = "";
	public String DISGUISE_ALIAS_ENDERMAN = "endermen";
	public String DISGUISE_ALIAS_ENDERMITE = "";
	public String DISGUISE_ALIAS_EVOKER = "";
	public String DISGUISE_ALIAS_GHAST = "";
	public String DISGUISE_ALIAS_GIANT = "";
	public String DISGUISE_ALIAS_GUARDIAN = "";
	public String DISGUISE_ALIAS_HORSE = "";
	public String DISGUISE_ALIAS_HUSK = "";
	public String DISGUISE_ALIAS_ILLUSIONER = "";
	public String DISGUISE_ALIAS_IRON_GOLEM = "golem";
	public String DISGUISE_ALIAS_LLAMA = "";
	public String DISGUISE_ALIAS_MAGMA_CUBE = "magmacube, magma-slime, magmaslime, lava-slime, lavaslime, lava-cube, lavacube";
	public String DISGUISE_ALIAS_MULE = "";
	public String DISGUISE_ALIAS_MUSHROOM_COW = "mushroom-cow, mooshroom";
	public String DISGUISE_ALIAS_OCELOT = "cat";
	public String DISGUISE_ALIAS_PARROT = "";
	public String DISGUISE_ALIAS_PIG = "";
	public String DISGUISE_ALIAS_PIG_ZOMBIE = "pigzombie, pigman, pigmen";
	public String DISGUISE_ALIAS_POLAR_BEAR = "polarbear, bear";
	public String DISGUISE_ALIAS_RABBIT = "bunny";
	public String DISGUISE_ALIAS_SHEEP = "";
	public String DISGUISE_ALIAS_SHULKER = "";
	public String DISGUISE_ALIAS_SILVERFISH = "";
	public String DISGUISE_ALIAS_SKELETAL_HORSE = "";
	public String DISGUISE_ALIAS_SKELETON = "";
	public String DISGUISE_ALIAS_SLIME = "";
	public String DISGUISE_ALIAS_SNOWMAN = "";
	public String DISGUISE_ALIAS_SPIDER = "";
	public String DISGUISE_ALIAS_SQUID = "";
	public String DISGUISE_ALIAS_STRAY = "";
	public String DISGUISE_ALIAS_UNDEAD_HORSE = "undeadhorse, zombie-horse, zombiehorse";
	public String DISGUISE_ALIAS_VEX = "";
	public String DISGUISE_ALIAS_VILLAGER = "";
	public String DISGUISE_ALIAS_VINDICATOR = "";
	public String DISGUISE_ALIAS_WITCH = "";
	public String DISGUISE_ALIAS_WITHER = "witherboss";
	public String DISGUISE_ALIAS_WITHER_SKELETON = "";
	public String DISGUISE_ALIAS_WOLF = "dog";
	public String DISGUISE_ALIAS_ZOMBIE = "";
	public String DISGUISE_ALIAS_ZOMBIE_VILLAGER = "zombievillager, infected-villager, infectedvillager";
	public String DISGUISE_ALIAS_AREA_EFFECT_CLOUD = "areaeffectcloud, effect-cloud, effectcloud";
	public String DISGUISE_ALIAS_ARMOR_STAND = "armorstand";
	public String DISGUISE_ALIAS_BOAT = "";
	public String DISGUISE_ALIAS_ENDER_CRYSTAL = "endercrystal, crystal";
	public String DISGUISE_ALIAS_FALLING_BLOCK = "fallingblock, block";
	public String DISGUISE_ALIAS_ITEM = "item-stack, itemstack";
	public String DISGUISE_ALIAS_MINECART = "cart";
	
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