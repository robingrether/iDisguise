package de.robingrether.idisguise.management.impl.v1_7_R2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.DisguiseMap;
import de.robingrether.idisguise.management.DisguiseMapLegacy;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PacketHelper;

public class DisguiseManagerImpl extends DisguiseManager {
	
	private DisguiseMapLegacy disguiseMap = new DisguiseMapLegacy();
	
	public Packet[] getSpawnPacket(Player player) {
		Packet[] packetSpawn;
		Disguise disguise = getDisguise(player);
		if(disguise == null) {
			packetSpawn = new Packet[1];
			packetSpawn[0] = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		} else {
			packetSpawn = (Packet[])PacketHelper.instance.getPackets(player, disguise);
		}
		return packetSpawn;
	}
	
	public synchronized void disguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise oldDisguise = disguiseMap.getDisguise(player.getName());
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
			disguiseMap.putDisguise(player.getName(), disguise);
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
			disguiseMap.putDisguise(offlinePlayer.getName(), disguise);
		}
	}
	
	public synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise disguise = disguiseMap.getDisguise(player.getName());
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
			disguiseMap.removeDisguise(player.getName());
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) {
					continue;
				}
				observer.showPlayer(player);
			}
			return disguise;
		} else {
			return disguiseMap.removeDisguise(offlinePlayer.getName());
		}
	}
	
	public synchronized void undisguiseAll() {
		for(String player : disguiseMap.getPlayers()) {
			undisguise(Bukkit.getOfflinePlayer(player));
		}
	}
	
	public boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer.getName());
	}
	
	public Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer.getName());
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
		for(String player : disguiseMap.getMap().keySet()) {
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
				disguiseMap = new DisguiseMapLegacy(new DisguiseMap(map));
			} else if(map.keySet().iterator().next() instanceof String) {
				disguiseMap = new DisguiseMapLegacy(map);
			}
		}
	}
	
}