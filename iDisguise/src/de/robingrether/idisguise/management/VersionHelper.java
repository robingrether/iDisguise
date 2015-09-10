package de.robingrether.idisguise.management;

import org.bukkit.Bukkit;

public class VersionHelper {
	
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
	
	public static boolean init() {
		if(initialized) {
			return false;
		}
		versionCode = Bukkit.getServer().getClass().getPackage().getName().substring(orgBukkitCraftbukkit.length() + 1);
		orgBukkitCraftbukkitVersioned = orgBukkitCraftbukkit + "." + versionCode;
		netMinecraftServerVersioned = netMinecraftServer + "." + versionCode;
		switch(versionCode) {
			case "v1_8_R3":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R3.PlayerHelperImpl();
				break;
			case "v1_8_R2":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R2.PlayerHelperImpl();
				break;
			case "v1_8_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_8_R1.PlayerHelperImpl();
				break;
			case "v1_7_R4":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R4.GhostFactoryImpl();
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
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R2.PlayerHelperImpl();
				break;
			case "v1_7_R1":
				ChannelRegister.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.ChannelRegisterImpl();
				DisguiseManager.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.DisguiseManagerImpl();
				GhostFactory.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.GhostFactoryImpl();
				PacketHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.PacketHelperImpl();
				PlayerHelper.instance = new de.robingrether.idisguise.management.impl.v1_7_R1.PlayerHelperImpl();
				break;
			default:
				return false;
		}
		initialized = true;
		return true;
	}
	
}