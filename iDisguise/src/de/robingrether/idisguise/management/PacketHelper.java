package de.robingrether.idisguise.management;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class PacketHelper {
	
	public static PacketHelper instance;
	
	protected final boolean[] attributes = new boolean[2];
	/*
	 * attributes[0] -> always show names on mob and object disguises
	 * 
	 * attributes[1] -> modify player list (tab key list)
	 */
	
	public abstract Object[] getPackets(Player player, Disguise disguise);
	
	public abstract Object getPlayerInfo(OfflinePlayer offlinePlayer, Object context, int ping, Object gamemode);
	
	public void setAttribute(int index, boolean value) {
		attributes[index] = value;
	}
	
}