package de.robingrether.idisguise.management.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;

public class VanillaTargetParser18 extends VanillaTargetParser {
	
	private static Class<?> EntityLiving;
	private static Method PlayerSelector_getPlayers;
	private static Method CraftServer_getCommandMap;
	private static Class<?> VanillaCommandWrapper;
	private static Method VanillaCommandWrapper_getListener;
	private static Object vanillaCommand = null;
	
	public Collection<? extends Object> parseTargets(String argument, CommandSender sender) throws Exception {
		if(vanillaCommand == null) {
			Command command = ((SimpleCommandMap)CraftServer_getCommandMap.invoke(Bukkit.getServer())).getCommand("minecraft:entitydata");
			if(VanillaCommandWrapper.isInstance(command)) vanillaCommand = command;
		}
		if(vanillaCommand != null) {
			return (Collection<? extends Object>)PlayerSelector_getPlayers.invoke(null, getListener(sender), argument, EntityLiving);
		}
		
		return Collections.emptySet();
	}
	
	static {
		try {
			EntityLiving = Class.forName(VersionHelper.getNMSPackage() + ".EntityLiving");
			PlayerSelector_getPlayers = Class.forName(VersionHelper.getNMSPackage() + ".PlayerSelector").getDeclaredMethod("getPlayers", Class.forName(VersionHelper.getNMSPackage() + ".ICommandListener"), String.class, Class.class);
			CraftServer_getCommandMap = Class.forName(VersionHelper.getOBCPackage() + ".CraftServer").getDeclaredMethod("getCommandMap");
			VanillaCommandWrapper = Class.forName(VersionHelper.getOBCPackage() + ".command.VanillaCommandWrapper");
			VanillaCommandWrapper_getListener = VanillaCommandWrapper.getDeclaredMethod("getListener", CommandSender.class);
			VanillaCommandWrapper_getListener.setAccessible(true);
		} catch(ClassNotFoundException|NoSuchMethodException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required reflection configuration.", e);
			}
		}
	}
	
	private static Object getListener(CommandSender sender) throws Exception {
		if(vanillaCommand != null) return VanillaCommandWrapper_getListener.invoke(vanillaCommand, sender);
		
		return null;
	}
	
}