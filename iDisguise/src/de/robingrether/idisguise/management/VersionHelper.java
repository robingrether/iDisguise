package de.robingrether.idisguise.management;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.management.channel.ChannelInjectorPC;
import de.robingrether.util.StringUtil;

public class VersionHelper {
	
	private static final String[] versions = {"v1_5_R2", "v1_5_R3", "v1_6_R1", "v1_6_R2", "v1_6_R3", "v1_7_R1", "v1_7_R2", "v1_7_R3", "v1_7_R4", "v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2", "v1_10_R1"};
	private static boolean initialized = false;
	private static String versionCode, orgBukkitCraftbukkit = "org.bukkit.craftbukkit", netMinecraftServer = "net.minecraft.server", orgBukkitCraftbukkitVersioned, netMinecraftServerVersioned;
	private static boolean debug, require1_6, require1_7, require1_8, require1_9, require1_10, useGameProfiles;
	
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
	
	public static boolean useGameProfiles() {
		return useGameProfiles;
	}
	
	public static boolean require1_10() {
		return require1_10;
	}
	
	public static boolean require1_9() {
		return require1_9;
	}
	
	public static boolean require1_8() {
		return require1_8;
	}
	
	public static boolean require1_7() {
		return require1_7;
	}
	
	public static boolean require1_6() {
		return require1_6;
	}
	
	public static boolean init(boolean debug) {
		if(initialized) {
			return false;
		}
		versionCode = Bukkit.getServer().getClass().getPackage().getName().substring(orgBukkitCraftbukkit.length() + 1);
		orgBukkitCraftbukkitVersioned = orgBukkitCraftbukkit + "." + versionCode;
		netMinecraftServerVersioned = netMinecraftServer + "." + versionCode;
		VersionHelper.debug = debug;
		require1_6 = requireVersion("v1_6_R1");
		require1_7 = requireVersion("v1_7_R1");
		require1_8 = requireVersion("v1_8_R1");
		require1_9 = requireVersion("v1_9_R1");
		require1_10 = requireVersion("v1_10_R1");
		useGameProfiles = requireVersion("v1_7_R3");
		switch(versionCode) {
			case "v1_5_R2":
			case "v1_5_R3":
			case "v1_6_R1":
			case "v1_6_R2":
			case "v1_6_R3":
				String localVersionCode = versionCode.replace("v1_5_R3", "v1_5_R2").replace("v1_6_R3", "v1_6_R2");
				Reflection.init("reflection/" + localVersionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new DisguiseManager());
				PlayerHelper.setInstance(new PlayerHelper());
				Sounds.init("sounds/15_16.txt");
				break;
			case "v1_7_R1":
			case "v1_7_R2":
				Reflection.init("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new DisguiseManager());
				PlayerHelper.setInstance(new de.robingrether.idisguise.management.player.PlayerHelper17());
				Sounds.init("sounds/17_18.txt");
				break;
			case "v1_7_R3":
			case "v1_7_R4":
				Reflection.init("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new DisguiseManager());
				PlayerHelper.setInstance(new de.robingrether.idisguise.management.player.PlayerHelperUID17());
				Sounds.init("sounds/17_18.txt");
				break;
			case "v1_8_R1":
			case "v1_8_R2":
			case "v1_8_R3":
				Reflection.init("reflection/" + versionCode + ".txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new de.robingrether.idisguise.management.disguise.DisguiseManager18());
				PlayerHelper.setInstance(new de.robingrether.idisguise.management.player.PlayerHelperUID18());
				Sounds.init("sounds/17_18.txt");
				break;
			case "v1_9_R1":
			case "v1_9_R2":
				Reflection.init("reflection/v1_9_R1.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new DisguiseManager());
				PlayerHelper.setInstance(new de.robingrether.idisguise.management.player.PlayerHelperUID18());
				Sounds.init("sounds/19.txt");
				break;
			case "v1_10_R1":
				Reflection.init("reflection/v1_10_R1.txt", netMinecraftServerVersioned, orgBukkitCraftbukkitVersioned);
				DisguiseManager.setInstance(new DisguiseManager());
				PlayerHelper.setInstance(new de.robingrether.idisguise.management.player.PlayerHelperUID18());
				Sounds.init("sounds/19.txt");
				break;
			default:
				return false;
		}
		ChannelInjector.setInstance(new ChannelInjectorPC());
		GhostFactory.setInstance(new GhostFactory());
		PacketHandler.setInstance(new PacketHandler());
		PacketHelper.setInstance(new PacketHelper());
		initialized = true;
		return true;
	}
	
}