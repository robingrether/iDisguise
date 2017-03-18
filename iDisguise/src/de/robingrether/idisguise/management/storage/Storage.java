package de.robingrether.idisguise.management.storage;

import org.bukkit.OfflinePlayer;

import de.robingrether.idisguise.disguise.Disguise;

public class Storage {
	
	private static Storage instance = new Storage();
	
	public static Storage getInstance() {
		return instance;
	}
	
	public static void setInstance(Storage instance) {
		Storage.instance = instance;
	}
	
	public void enable() {}
	
	public void disable() {}
	
	public void update(OfflinePlayer player, Disguise disguise) {}
	
}