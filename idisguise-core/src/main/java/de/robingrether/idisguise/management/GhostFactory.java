package de.robingrether.idisguise.management;

import java.util.HashSet;
import java.util.Set;

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
	private Set<OfflinePlayer> ghosts;
	private int taskId;
	
	public void enable(Plugin plugin) {
		if(enabled) {
			return;
		}
		enabled = true;
		ghosts = new HashSet<OfflinePlayer>();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		ghostTeam = scoreboard.getTeam(GHOST_TEAM_NAME);
		if(ghostTeam == null) {
			ghostTeam = scoreboard.registerNewTeam(GHOST_TEAM_NAME);
		}
		ghostTeam.setCanSeeFriendlyInvisibles(true);
		for(Player player : Bukkit.getOnlinePlayers()) {
			addPlayer(player.getName());
		}
		for(OfflinePlayer offlinePlayer : DisguiseManager.getInstance().getDisguisedPlayers()) {
			if(DisguiseManager.getInstance().getDisguise(offlinePlayer) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(offlinePlayer)).isGhost()) {
				addPlayer(((PlayerDisguise)DisguiseManager.getInstance().getDisguise(offlinePlayer)).getSkinName());
				if(offlinePlayer.isOnline()) {
					addGhost(offlinePlayer.getPlayer());
				}
			}
		}
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			public void run() {
				for(OfflinePlayer offlinePlayer : ghosts) {
					if(offlinePlayer.isOnline()) {
						offlinePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
					}
				}
			}
			
		}, 1200L, 1200L);
	}
	
	public void disable() {
		if(!enabled) {
			return;
		}
		enabled = false;
		Bukkit.getScheduler().cancelTask(taskId);
		for(OfflinePlayer offlinePlayer : ghosts) {
			if(offlinePlayer.isOnline()) {
				offlinePlayer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}
		ghostTeam.unregister();
	}
	
	public void addPlayer(String name) {
		if(enabled) {
			if(VersionHelper.requireVersion("v1_8_R3")) {
				ghostTeam.addEntry(name);
			} else {
				ghostTeam.addPlayer(Bukkit.getOfflinePlayer(name));
			}
		}
	}
	
	public boolean addGhost(Player player) {
		if(enabled) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			return ghosts.add(player);
		}
		return false;
	}
	
	public boolean removeGhost(Player player) {
		if(enabled) {
			boolean remove = ghosts.remove(player);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			return remove;
		}
		return false;
	}
	
}