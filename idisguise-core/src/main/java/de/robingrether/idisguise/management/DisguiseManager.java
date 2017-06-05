package de.robingrether.idisguise.management;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;

import static de.robingrether.idisguise.management.Reflection.*;

public class DisguiseManager {
	
	private static DisguiseManager instance;
	
	public static DisguiseManager getInstance() {
		return instance;
	}
	
	static void setInstance(DisguiseManager instance) {
		DisguiseManager.instance = instance;
	}
	
	protected DisguiseMap disguiseMap = DisguiseMap.emptyMap();
	
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
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.showPlayer(player);
			}
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
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.showPlayer(player);
			}
			return disguise;
		} else {
			return disguiseMap.removeDisguise(offlinePlayer);
		}
	}
	
	public synchronized void undisguiseAll() {
		for(OfflinePlayer offlinePlayer : getDisguisedPlayers()) {
			undisguise(offlinePlayer);
		}
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer);
	}
	
	public boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer) {
		return disguiseMap.isDisguised(offlinePlayer) && disguiseMap.getDisguise(offlinePlayer).isVisibleTo(observer);
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer);
	}
	
	public int getNumberOfDisguisedPlayers() {
		int i = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isDisguised(player)) {
				i++;
			}
		}
		return i;
	}
	
	public Set<OfflinePlayer> getDisguisedPlayers() {
		Set<?> origin = disguiseMap.getDisguisedPlayers();
		Set<OfflinePlayer> destination = new HashSet<OfflinePlayer>();
		try {
			for(Object offlinePlayer : origin) {
				destination.add(offlinePlayer instanceof UUID ? Bukkit.getOfflinePlayer((UUID)offlinePlayer) : Bukkit.getOfflinePlayer((String)offlinePlayer));
			}
		} catch(Exception e) {
		}
		return destination;
	}
	
	public Map<?, Disguise> getDisguises() {
		return disguiseMap.getMap();
	}
	
	public void updateDisguises(Map<?, Disguise> map) {
		disguiseMap = DisguiseMap.fromMap(map);
	}
	
	public void resendPackets(Player player) {
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