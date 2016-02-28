package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class DisguiseManager {
	
	public static DisguiseManager instance;
	
	public abstract Object getSpawnPacket(Player player);
	
	public abstract void disguise(OfflinePlayer offlinePlayer, Disguise disguise);
	
	public abstract Disguise undisguise(OfflinePlayer offlinePlayer);
	
	public abstract void undisguiseAll();
	
	public abstract boolean isDisguised(OfflinePlayer offlinePlayer);
	
	public abstract Disguise getDisguise(OfflinePlayer offlinePlayer);
	
	public abstract int getOnlineDisguiseCount();
	
	public abstract Set<OfflinePlayer> getDisguisedPlayers();
	
	public abstract Map getDisguises();
	
	public abstract void updateDisguises(Map map);
	
}