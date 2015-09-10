package de.robingrether.idisguise.management;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class GhostFactory {
	
	public static GhostFactory instance;
	
	public abstract void enable(Plugin plugin);
	
	public abstract void disable();
	
	public abstract void addPlayer(String player);
	
	public abstract boolean addGhost(Player player);
	
	public abstract boolean removeGhost(Player player);
	
}