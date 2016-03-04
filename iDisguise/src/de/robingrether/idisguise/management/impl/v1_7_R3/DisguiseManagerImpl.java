package de.robingrether.idisguise.management.impl.v1_7_R3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.DisguiseMapLegacy;
import de.robingrether.idisguise.management.GhostFactory;

public class DisguiseManagerImpl extends DisguiseManager {
	
	private DisguiseMap disguiseMap = new DisguiseMap();
	
	public synchronized void disguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise oldDisguise = disguiseMap.getDisguise(player.getUniqueId());
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.hidePlayer(player);
			}
			if(oldDisguise instanceof PlayerDisguise) {
				player.setDisplayName(player.getName());
				if(oldDisguise.getType().equals(DisguiseType.GHOST)) {
					GhostFactory.instance.removeGhost(player);
				}
			}
			disguiseMap.putDisguise(player.getUniqueId(), disguise);
			if(disguise instanceof PlayerDisguise) {
				player.setDisplayName(((PlayerDisguise)disguise).getName());
				if(((PlayerDisguise)disguise).isGhost()) {
					GhostFactory.instance.addPlayer(((PlayerDisguise)disguise).getName());
					GhostFactory.instance.addGhost(player);
				}
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.showPlayer(player);
			}
		} else {
			disguiseMap.putDisguise(offlinePlayer.getUniqueId(), disguise);
		}
	}
	
	public synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise disguise = disguiseMap.getDisguise(player.getUniqueId());
			if(disguise == null) {
				return null;
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.hidePlayer(player);
			}
			if(disguise instanceof PlayerDisguise) {
				player.setDisplayName(player.getName());
				if(disguise.getType().equals(DisguiseType.GHOST)) {
					GhostFactory.instance.removeGhost(player);
				}
			}
			disguiseMap.removeDisguise(player.getUniqueId());
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.showPlayer(player);
			}
			return disguise;
		} else {
			return disguiseMap.removeDisguise(offlinePlayer.getUniqueId());
		}
	}
	
	public synchronized void undisguiseAll() {
		for(UUID uuid : disguiseMap.getPlayers()) {
			undisguise(Bukkit.getOfflinePlayer(uuid));
		}
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer.getUniqueId());
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer.getUniqueId());
	}
	
	public int getOnlineDisguiseCount() {
		int count = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isDisguised(player)) {
				count++;
			}
		}
		return count;
	}
	
	public Set<OfflinePlayer> getDisguisedPlayers() {
		Set<OfflinePlayer> set = new HashSet<OfflinePlayer>();
		for(UUID player : disguiseMap.getMap().keySet()) {
			set.add(Bukkit.getOfflinePlayer(player));
		}
		return set;
	}
	
	public Map getDisguises() {
		return disguiseMap.getMap();
	}
	
	public void updateDisguises(Map map) {
		if(!map.keySet().isEmpty()) {
			if(map.keySet().iterator().next() instanceof UUID) {
				disguiseMap = new DisguiseMap(map);
			} else if(map.keySet().iterator().next() instanceof String) {
				disguiseMap = new DisguiseMap(new DisguiseMapLegacy(map));
			}
		}
	}
	
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