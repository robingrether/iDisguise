package de.robingrether.idisguise.management.impl.v1_8_R3;

import de.robingrether.idisguise.management.GhostFactory;

public class GhostFactoryImpl extends GhostFactory {
	
	public void addPlayer(String player) {
		if(enabled) {
			ghostTeam.addEntry(player);
		}
	}
	
}