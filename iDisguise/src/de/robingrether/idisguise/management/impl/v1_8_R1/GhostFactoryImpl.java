package de.robingrether.idisguise.management.impl.v1_8_R1;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.management.GhostFactory;

public class GhostFactoryImpl extends GhostFactory {
	
	public void addPlayer(String player) {
		if(enabled) {
			ghostTeam.addPlayer(Bukkit.getOfflinePlayer(player));
		}
	}
	
}