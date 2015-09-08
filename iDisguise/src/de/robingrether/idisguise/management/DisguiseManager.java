package de.robingrether.idisguise.management;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;

public abstract class DisguiseManager {
	
	public static DisguiseManager instance;
	
	protected DisguiseList disguiseList = new DisguiseList();
	
	public abstract Object getSpawnPacket(Player player);
	
	protected abstract Object getPlayerInfoPacket(Player player);
	
	protected abstract Object getDestroyPacket(Player player);
	
	public abstract void sendPacketLater(final Player player, final Object packet, long delay);
	
	public abstract void disguise(Player player, Disguise disguise);
	
	public abstract Disguise undisguise(Player player);
	
	public void undisguiseAll() {
		for(UUID player : disguiseList.getPlayers()) {
			if(Bukkit.getPlayer(player) != null) {
				undisguise(Bukkit.getPlayer(player));
			} else {
				disguiseList.removeDisguise(player);
			}
		}
	}
	
	public abstract void updateAttributes(Player player, Player observer);
	
	protected abstract void updateAttributes(Player player);
	
	public boolean isDisguised(Player player) {
		return disguiseList.isDisguised(player.getUniqueId());
	}
	
	public Disguise getDisguise(Player player) {
		return disguiseList.getDisguise(player.getUniqueId());
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
	
	public DisguiseList getDisguiseList() {
		return disguiseList;
	}
	
	public void setDisguiseList(DisguiseList disguiseList) {
		this.disguiseList = disguiseList;
	}
	
}