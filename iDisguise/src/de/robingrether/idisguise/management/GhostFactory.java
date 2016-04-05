package de.robingrether.idisguise.management;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.robingrether.idisguise.disguise.PlayerDisguise;

public class GhostFactory {
	
	private static GhostFactory instance;
	
	public static GhostFactory getInstance() {
		return instance;
	}
	
	static void setInstance(GhostFactory instance) {
		GhostFactory.instance = instance;
	}
	
	private final String GHOST_TEAM_NAME = "Ghosts";
	private boolean enabled = false;
	private Team ghostTeam;
	private Set<?> ghosts;
	private int taskId;
	
	public void enable(Plugin plugin) {
		if(enabled) {
			return;
		}
		if(VersionHelper.useGameProfiles()) {
			ghosts = new HashSet<UUID>();
		} else {
			ghosts = new HashSet<String>();
		}
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		ghostTeam = scoreboard.getTeam(GHOST_TEAM_NAME);
		if(ghostTeam == null) {
			ghostTeam = scoreboard.registerNewTeam(GHOST_TEAM_NAME);
		}
		ghostTeam.setCanSeeFriendlyInvisibles(true);
		for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
			addPlayer(offlinePlayer.getName());
		}
		for(Player player : Reflection.getOnlinePlayers()) {
			if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).isGhost()) {
				addGhost(player);
			}
		}
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			public void run() {
				if(VersionHelper.useGameProfiles()) {
					for(UUID uid : ((Set<UUID>)ghosts)) {
						Player player = Bukkit.getPlayer(uid);
						if(player != null) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
						}
					}
				} else {
					for(String name : ((Set<String>)ghosts)) {
						Player player = Bukkit.getPlayer(name);
						if(player != null) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
						}
					}
				}
			}
			
		}, 1200L, 1200L);
		enabled = true;
	}
	
	public void disable() {
		if(!enabled) {
			return;
		}
		Bukkit.getScheduler().cancelTask(taskId);
		if(VersionHelper.useGameProfiles()) {
			for(UUID uid : ((Set<UUID>)ghosts)) {
				Player player = Bukkit.getPlayer(uid);
				if(player != null) {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		} else {
			for(String name : ((Set<String>)ghosts)) {
				Player player = Bukkit.getPlayer(name);
				if(player != null) {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		}
		ghostTeam.setCanSeeFriendlyInvisibles(false);
		ghostTeam.unregister();
		enabled = false;
	}
	
	public void addPlayer(String player) {
		if(enabled) {
			if(VersionHelper.requireVersion("v1_8_R3")) {
				ghostTeam.addEntry(player);
			} else {
				ghostTeam.addPlayer(Bukkit.getOfflinePlayer(player));
			}
		}
	}
	
	public boolean addGhost(Player player) {
		if(enabled) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			if(VersionHelper.useGameProfiles()) {
				return ((Set<UUID>)ghosts).add(player.getUniqueId());
			} else {
				return ((Set<String>)ghosts).add(player.getName().toLowerCase(Locale.ENGLISH));
			}
		}
		return false;
	}
	
	public boolean removeGhost(Player player) {
		if(enabled) {
			boolean remove;
			if(VersionHelper.useGameProfiles()) {
				remove = ghosts.remove(player.getUniqueId());
			} else {
				remove = ghosts.remove(player.getName().toLowerCase(Locale.ENGLISH));
			}
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			return remove;
		}
		return false;
	}
}