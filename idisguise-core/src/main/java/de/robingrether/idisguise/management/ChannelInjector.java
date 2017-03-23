package de.robingrether.idisguise.management;

import org.bukkit.entity.Player;

public abstract class ChannelInjector {
	
	private static ChannelInjector instance;
	
	public static ChannelInjector getInstance() {
		return instance;
	}
	
	static void setInstance(ChannelInjector instance) {
		ChannelInjector.instance = instance;
	}
	
	public abstract void inject(Player player);
	
	public abstract void remove(Player player);
	
	public void injectOnlinePlayers() {
		for(Player player : Reflection.getOnlinePlayers()) {
			inject(player);
		}
	}
	
	public void removeOnlinePlayers() {
		for(Player player : Reflection.getOnlinePlayers()) {
			remove(player);
		}
	}
	
}