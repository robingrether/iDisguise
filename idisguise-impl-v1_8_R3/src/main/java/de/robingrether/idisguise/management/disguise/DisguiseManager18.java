package de.robingrether.idisguise.management.disguise;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.Reflection;

public class DisguiseManager18 extends DisguiseManager {
	
	private void showPlayerLater(final Player player) {
		Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
			
			public void run() {
				if(player != null) {
					for(Player observer : Bukkit.getOnlinePlayers()) {
						if(observer == player) {
							continue;
						}
						observer.showPlayer(player);
					}
				}
			}
			
		}, 10L);
	}
	
	public synchronized void disguise(final OfflinePlayer offlinePlayer, final Disguise disguise) {
		if(disguise instanceof PlayerDisguise && !PlayerHelper.getInstance().isGameProfileLoaded(((PlayerDisguise)disguise).getSkinName())) {
			Bukkit.getScheduler().runTaskAsynchronously(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					PlayerHelper.getInstance().waitForGameProfile(((PlayerDisguise)disguise).getSkinName());
					Bukkit.getScheduler().runTask(iDisguise.getInstance(), new Runnable() {
						
						public void run() {
							disguise(offlinePlayer, disguise);
						}
						
					});
				}
				
			});
			return;
		}
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise oldDisguise = disguiseMap.getDisguise(player);
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.hidePlayer(player);
			}
			if(oldDisguise instanceof PlayerDisguise) {
				if(oldDisguise.getType().equals(DisguiseType.GHOST)) {
					GhostFactory.getInstance().removeGhost(player);
				}
			}
			disguiseMap.updateDisguise(player, disguise);
			if(disguise instanceof PlayerDisguise) {
				if(((PlayerDisguise)disguise).isGhost()) {
					GhostFactory.getInstance().addPlayer(((PlayerDisguise)disguise).getSkinName());
					GhostFactory.getInstance().addGhost(player);
				}
			}
			showPlayerLater(player);
		} else {
			disguiseMap.updateDisguise(offlinePlayer, disguise);
		}
	}
	
	public synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise disguise = disguiseMap.getDisguise(player);
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
				if(disguise.getType().equals(DisguiseType.GHOST)) {
					GhostFactory.getInstance().removeGhost(player);
				}
			}
			disguiseMap.removeDisguise(player);
			showPlayerLater(player);
			return disguise;
		} else {
			return disguiseMap.removeDisguise(offlinePlayer);
		}
	}
	
	public void resendPackets(Player player) {
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer == player) {
				continue;
			}
			observer.hidePlayer(player);
		}
		showPlayerLater(player);
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
				showPlayerLater(player);
			}
		}
	}
	
}