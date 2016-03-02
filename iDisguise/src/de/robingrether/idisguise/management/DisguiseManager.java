package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
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
	
	public void resendPackets() {
		for(OfflinePlayer offlinePlayer : getDisguisedPlayers()) {
			if(offlinePlayer.isOnline()) {
				Player player = offlinePlayer.getPlayer();
				for(Player observer : Bukkit.getOnlinePlayers()) {
					if(observer == player) {
						continue;
					}
					observer.hidePlayer(player);
				}
				for(Player observer : Bukkit.getOnlinePlayers()) {
					if(observer == player) {
						continue;
					}
					observer.showPlayer(player);
				}
			}
		}
	}
	
}