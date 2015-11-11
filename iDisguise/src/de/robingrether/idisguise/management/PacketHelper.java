package de.robingrether.idisguise.management;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class PacketHelper {
	
	public static PacketHelper instance;
	
	protected final boolean[] attributes = new boolean[1];
	/*
	 * attributes[0] -> always show names on mob and object disguises
	 */
	
	public abstract Object[] getPackets(Player player, Disguise disguise);
	
	public void setAttribute(int index, boolean value) {
		attributes[index] = value;
	}
	
}