package de.robingrether.idisguise.management;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.management.channel.ChannelInjector;
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.util.StringUtil;

public class VersionHelper {
	
	public static final String[] VERSIONS = {"v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1"};
	public static final String EARLIEST = VERSIONS[0];
	
	private static boolean initialized = false;
	private static String versionCode, orgBukkitCraftbukkit = "org.bukkit.craftbukkit", netMinecraftServer = "net.minecraft.server", orgBukkitCraftbukkitVersioned, netMinecraftServerVersioned;
	private static boolean debug, require1_9, require1_10, require1_11, require1_12;
	
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
		if(!StringUtil.equals(requiredVersion, VERSIONS)) {
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
			orgBukkitCraftbukkitVersioned = orgBukkitCraftbukkit + "." + versionCode;
			netMinecraftServerVersioned = netMinecraftServer + "." + versionCode;
			VersionHelper.debug = debug;
			require1_9 = requireVersion("v1_9_R1");
			require1_10 = requireVersion("v1_10_R1");
			require1_11 = requireVersion("v1_11_R1");
			require1_12 = requireVersion("v1_12_R1");
			Reflection.load("reflection/common.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
			switch(versionCode) {
				case "v1_8_R1":
				case "v1_8_R2":
				case "v1_8_R3":
					Reflection.load("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
					Sounds.init("sounds/18.yml");
					break;
				case "v1_9_R1":
				case "v1_9_R2":
				case "v1_10_R1":
				case "v1_11_R1":
				case "v1_12_R1":
					Reflection.load("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
					Sounds.init("sounds/112.yml");
					break;
				default:
					return false;
			}
			ChannelInjector.init();
			EntityIdList.init();
			ProfileHelper.setInstance((ProfileHelper)Class.forName("de.robingrether.idisguise.management.profile.ProfileHelperUID").newInstance());
			Reflection.EntityHumanNonAbstract = Class.forName("de.robingrether.idisguise.management.reflection.EntityHumanNonAbstract" + versionCode.replaceAll("[^0-9]*", ""));
			Reflection.EntityHumanNonAbstract_new = Reflection.EntityHumanNonAbstract.getConstructor(Reflection.World, Reflection.GameProfile);
			initialized = true;
			return true;
		} catch(Exception e) {
			initialized = false;
			return false;
		}
	}
	
}