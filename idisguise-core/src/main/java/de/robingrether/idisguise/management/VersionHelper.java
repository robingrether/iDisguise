package de.robingrether.idisguise.management;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.management.channel.ChannelInjector;
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.util.StringUtil;

public class VersionHelper {
	
	private static final String[] versions = {"v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1"};
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
		if(!StringUtil.equals(requiredVersion, versions)) {
			return false;
		}
		for(String version : versions) {
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
			switch(versionCode) {
				case "v1_8_R1":
				case "v1_8_R2":
				case "v1_8_R3":
					Reflection.init("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
					Sounds.init("sounds/17_18.txt");
					break;
				case "v1_9_R1":
				case "v1_9_R2":
					Reflection.init("reflection/v1_9_R1.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
					Sounds.init("sounds/111.txt");
					break;
				case "v1_10_R1":
				case "v1_11_R1":
				case "v1_12_R1":
					Reflection.init("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
					Sounds.init("sounds/111.txt");
					break;
				default:
					return false;
			}
			ChannelInjector.init();
			EntityIdList.init();
			ProfileHelper.setInstance((ProfileHelper)Class.forName("de.robingrether.idisguise.management.profile.ProfileHelperUID").newInstance());
			initialized = true;
			return true;
		} catch(Exception e) {
			initialized = false;
			return false;
		}
	}
	
}