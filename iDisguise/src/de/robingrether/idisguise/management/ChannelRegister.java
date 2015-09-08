package de.robingrether.idisguise.management;

import org.bukkit.entity.Player;

public abstract class ChannelRegister {
	
	public static ChannelRegister instance;
	
	public abstract void registerHandler(Player player);
	
	public abstract void unregisterHandler(Player player);
	
}