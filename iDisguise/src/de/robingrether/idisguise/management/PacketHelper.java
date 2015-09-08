package de.robingrether.idisguise.management;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class PacketHelper {
	
	public static PacketHelper instance;
	
	protected final boolean[] attributes = new boolean[1];
	
	public abstract Object getPacket(Player player, Disguise disguise);
	
	public void setAttribute(int index, boolean value) {
		attributes[index] = value;
	}
	
}