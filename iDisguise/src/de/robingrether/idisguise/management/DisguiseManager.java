package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class DisguiseManager {
	
	public static DisguiseManager instance;
	
	public abstract Object getSpawnPacket(Player player);
	
	protected abstract Object getPlayerInfoPacket(Player player);
	
	protected abstract Object getDestroyPacket(Player player);
	
	public abstract void sendPacketLater(final Player player, final Object packet, long delay);
	
	public abstract void disguise(Player player, Disguise disguise);
	
	public abstract Disguise undisguise(Player player);
	
	public abstract void undisguiseAll();
	
	public abstract void updateAttributes(Player player, Player observer);
	
	protected abstract void updateAttributes(Player player);
	
	public abstract boolean isDisguised(Player player);
	
	public abstract Disguise getDisguise(Player player);
	
	public abstract int getOnlineDisguiseCount();
	
	public abstract Set<OfflinePlayer> getDisguisedPlayers();
	
	public abstract Disguise removeDisguise(OfflinePlayer offlinePlayer);
	
	public abstract Map getDisguises();
	
	public abstract void updateDisguises(Map map);
	
}