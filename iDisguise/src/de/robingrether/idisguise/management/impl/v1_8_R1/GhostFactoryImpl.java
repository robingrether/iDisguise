package de.robingrether.idisguise.management.impl.v1_8_R1;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.robingrether.idisguise.management.GhostFactory;

public class GhostFactoryImpl extends GhostFactory {
	
	private final String GHOST_TEAM_NAME = "Ghosts";
	private boolean enabled = false;
	private Team ghostTeam;
	private HashSet<UUID> ghosts;
	private int taskId;
	
	public void enable(Plugin plugin) {
		if(enabled) {
			return;
		}
		ghosts = new HashSet<UUID>();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		ghostTeam = scoreboard.getTeam(GHOST_TEAM_NAME);
		if(ghostTeam == null) {
			ghostTeam = scoreboard.registerNewTeam(GHOST_TEAM_NAME);
		}
		ghostTeam.setCanSeeFriendlyInvisibles(true);
		for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			addPlayer(player.getName());
		}
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for(UUID uuid : ghosts) {
					Player player = Bukkit.getPlayer(uuid);
					if(player != null) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
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
		ghostTeam.unregister();
		enabled = false;
	}
	
	public void addPlayer(String player) {
		if(enabled) {
			ghostTeam.addPlayer(Bukkit.getOfflinePlayer(player));
		}
	}
	
	public boolean addGhost(Player player) {
		if(enabled) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			return ghosts.add(player.getUniqueId());
		}
		return false;
	}
	
	public boolean removeGhost(Player player) {
		if(enabled) {
			boolean remove = ghosts.remove(player.getUniqueId());
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			return remove;
		}
		return false;
	}
	
}