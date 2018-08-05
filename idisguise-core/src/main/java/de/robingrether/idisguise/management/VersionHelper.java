package de.robingrether.idisguise.management;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.management.channel.ChannelInjector;
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.idisguise.management.util.VanillaTargetParser;

public class VersionHelper {
	
	public static final List<String> VERSIONS = Arrays.asList("v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1", "v1_13_R1");
	public static final String EARLIEST = VERSIONS.get(0);
	
	private static boolean initialized = false;
	private static String versionCode, orgBukkitCraftbukkit = "org.bukkit.craftbukkit", netMinecraftServer = "net.minecraft.server", orgBukkitCraftbukkitVersioned, netMinecraftServerVersioned;
	private static boolean debug, require1_9, require1_10, require1_11, require1_12, require1_13;
	
	public static String getVersionCode() {
		return versionCode;
	}
	
	public static String getOBCPackage() {
		return orgBukkitCraftbukkitVersioned;
	}
	
	public static String getNMSPackage() {
		return netMinecraftServerVersioned;
	}
	
	public static boolean debug() {
		return debug;
	}
	
	public static boolean requireVersion(String requiredVersion) {
		if(!VERSIONS.contains(requiredVersion)) {
			return false;
		}
		for(String version : VERSIONS) {
			if(version.equals(requiredVersion)) {
				return true;
			} else if(version.equals(versionCode)) {
				return false;
			}
		}
		return false;
	}
	
	public static boolean require1_13() {
		return require1_13;
	}
	
	public static boolean require1_12() {
		return require1_12;
	}
	
	public static boolean require1_11() {
		return require1_11;
	}
	
	public static boolean require1_10() {
		return require1_10;
	}
	
	public static boolean require1_9() {
		return require1_9;
	}
	
	public static boolean init(boolean debug) {
		if(initialized) {
			return false;
		}
		try {
			versionCode = Bukkit.getServer().getClass().getPackage().getName().substring(orgBukkitCraftbukkit.length() + 1);
			if(!VERSIONS.contains(versionCode)) return false;
			
			orgBukkitCraftbukkitVersioned = orgBukkitCraftbukkit + "." + versionCode;
			netMinecraftServerVersioned = netMinecraftServer + "." + versionCode;
			VersionHelper.debug = debug;
			
			require1_9 = requireVersion("v1_9_R1");
			require1_10 = requireVersion("v1_10_R1");
			require1_11 = requireVersion("v1_11_R1");
			require1_12 = requireVersion("v1_12_R1");
			require1_13 = requireVersion("v1_13_R1");
			
			Reflection.load("reflection/common.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
			Reflection.load("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
			Reflection.load("reflection/common2.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
			ChannelInjector.init();
			EntityIdList.init();
			ProfileHelper.setInstance((ProfileHelper)Class.forName("de.robingrether.idisguise.management.profile.ProfileHelperUID").getDeclaredConstructor().newInstance());
			Reflection.EntityHumanNonAbstract = Class.forName("de.robingrether.idisguise.management.reflection.EntityHumanNonAbstract" + versionCode.replaceAll("[^0-9]*", ""));
			Reflection.EntityHumanNonAbstract_new = Reflection.EntityHumanNonAbstract.getConstructor(Reflection.World, Reflection.GameProfile);
			
			if(require1_13) VanillaTargetParser.setInstance((VanillaTargetParser)Class.forName("de.robingrether.idisguise.management.util.VanillaTargetParser113").getDeclaredConstructor().newInstance());
			else VanillaTargetParser.setInstance((VanillaTargetParser)Class.forName("de.robingrether.idisguise.management.util.VanillaTargetParser18").getDeclaredConstructor().newInstance());
			
			if(require1_13) Sounds.init("sounds/113.yml");
			else if(require1_9) Sounds.init("sounds/112.yml");
			else Sounds.init("sounds/18.yml");
			
			initialized = true;
			return true;
		} catch(Exception e) {
			initialized = false;
			return false;
		}
	}
	
}