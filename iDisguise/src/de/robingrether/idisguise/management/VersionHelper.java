package de.robingrether.idisguise.management;

import org.bukkit.Bukkit;

import de.robingrether.util.StringUtil;

public class VersionHelper {
	
	private static final String[] versions = {"v1_5_R2", "v1_5_R3", "v1_6_R1", "v1_6_R2", "v1_6_R3", "v1_7_R1", "v1_7_R2", "v1_7_R3", "v1_7_R4", "v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1"};
	private static boolean initialized = false;
	private static String versionCode, orgBukkitCraftbukkit = "org.bukkit.craftbukkit", netMinecraftServer = "net.minecraft.server", orgBukkitCraftbukkitVersioned, netMinecraftServerVersioned;
	
	public static String getVersionCode() {
		return versionCode;
	}
	
	public static String getOBCPackage() {
		return orgBukkitCraftbukkitVersioned;
	}
	
	public static String getNMSPackage() {
		return netMinecraftServerVersioned;
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
	
	public static boolean require1_9() {
		return requireVersion("v1_9_R1");
	}
	
	public static boolean require1_8() {
		return requireVersion("v1_8_R1");
	}
	
	public static boolean require1_6() {
		return requireVersion("v1_6_R1");
	}
	
	public static boolean init() {
		if(initialized) {
			return false;
		}
		versionCode = Bukkit.getServer().getClass().getPackage().getName().substring(orgBukkitCraftbukkit.length() + 1);
		orgBukkitCraftbukkitVersioned = orgBukkitCraftbukkit + "." + versionCode;
		netMinecraftServerVersioned = netMinecraftServer + "." + versionCode;
		switch(versionCode) {
			case "v1_9_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_9_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_9_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_9_R1.PlayerHelperImpl();
				break;
			case "v1_8_R3":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.PlayerHelperImpl();
				break;
			case "v1_8_R2":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.PlayerHelperImpl();
				break;
			case "v1_8_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.PlayerHelperImpl();
				break;
			case "v1_7_R4":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.PlayerHelperImpl();
				break;
			case "v1_7_R3":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R3.PlayerHelperImpl();
				break;
			case "v1_7_R2":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.PlayerHelperImpl();
				break;
			case "v1_7_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.PlayerHelperImpl();
				break;
			case "v1_6_R3":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_6_R3.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_6_R3.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PlayerHelperImpl();
				break;
			case "v1_6_R2":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_6_R2.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_6_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PlayerHelperImpl();
				break;
			case "v1_6_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_6_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_6_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PlayerHelperImpl();
				break;
			case "v1_5_R3":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_5_R3.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R3.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PlayerHelperImpl();
				break;
			case "v1_5_R2":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_5_R2.PlayerHelperImpl();
				break;
			default:
				return false;
		}
		initialized = true;
		return true;
	}
	
}