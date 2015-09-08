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

public abstract class GhostFactory {
	
	public static GhostFactory instance;
	
	protected final String GHOST_TEAM_NAME = "Ghosts";
	protected boolean enabled = false;
	protected HashSet<UUID> ghosts;
	protected Team ghostTeam;
	protected int taskId;
	
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
	
	public abstract void addPlayer(String player);
	
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