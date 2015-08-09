package de.robingrether.idisguise.management;

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

public class GhostFactory {
	
	private static final String GHOST_TEAM_NAME = "Ghosts";
	private static boolean enabled = false;
	private static HashSet<UUID> ghosts;
	private static Team ghostTeam;
	private static int taskId;
	
	public static void enable(Plugin plugin) {
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
			addPlayer(player);
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
	
	public static void disable() {
		if(!enabled) {
			return;
		}
		Bukkit.getScheduler().cancelTask(taskId);
		ghostTeam.unregister();
		enabled = false;
	}
	
	public static void addPlayer(OfflinePlayer player) {
		if(enabled) {
			ghostTeam.addEntry(player.getName());
		}
	}
	
	public static boolean addGhost(Player player) {
		if(enabled) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			return ghosts.add(player.getUniqueId());
		}
		return false;
	}
	
	public static boolean removeGhost(Player player) {
		if(enabled) {
			boolean remove = ghosts.remove(player.getUniqueId());
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			return remove;
		}
		return false;
	}
	
}