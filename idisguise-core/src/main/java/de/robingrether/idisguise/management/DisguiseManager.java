package de.robingrether.idisguise.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.channel.InjectedPlayerConnection;
import de.robingrether.idisguise.management.util.DisguiseMap;

import static de.robingrether.idisguise.management.Reflection.*;

public final class DisguiseManager {
	
	private DisguiseManager() {}
	
	public static boolean modifyScoreboardPackets;
	
	private static DisguiseMap disguiseMap = DisguiseMap.emptyMap();
	private static Set<UUID> seeThroughSet = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
	
	public static synchronized void disguise(final OfflinePlayer offlinePlayer, final Disguise disguise) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
//			Disguise oldDisguise = disguiseMap.getDisguise(player);
			hidePlayer(player);
			disguiseMap.updateDisguise(player, disguise);
			showPlayer(player);
		} else {
			disguiseMap.updateDisguise(offlinePlayer, disguise);
		}
	}
	
	public static synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			Disguise disguise = disguiseMap.getDisguise(player);
			if(disguise == null) {
				return null;
			}
			hidePlayer(player);
			disguiseMap.removeDisguise(player);
			showPlayer(player);
			return disguise;
		} else {
			return disguiseMap.removeDisguise(offlinePlayer);
		}
	}
	
	public static synchronized void undisguiseAll() {
		for(OfflinePlayer offlinePlayer : getDisguisedPlayers()) {
			undisguise(offlinePlayer);
		}
	}
	
	public static boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer);
	}
	
	public static boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer) {
		return disguiseMap.isDisguised(offlinePlayer) && disguiseMap.getDisguise(offlinePlayer).isVisibleTo(observer);
	}
	
	public static Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer);
	}
	
	public static int getNumberOfDisguisedPlayers() {
		int i = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isDisguised(player)) {
				i++;
			}
		}
		return i;
	}
	
	public static Set<OfflinePlayer> getDisguisedPlayers() {
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
	
	public static Map<?, Disguise> getDisguises() {
		return disguiseMap.getMap();
	}
	
	public static void updateDisguises(Map<?, Disguise> map) {
		disguiseMap = DisguiseMap.fromMap(map);
	}
	
	public static boolean canSeeThrough(OfflinePlayer offlinePlayer) {
		return seeThroughSet.contains(offlinePlayer.getUniqueId());
	}
	
	public static void setSeeThrough(OfflinePlayer offlinePlayer, boolean seeThrough) {
		if(seeThroughSet.contains(offlinePlayer.getUniqueId()) != seeThrough) {
			if(offlinePlayer.isOnline()) {
				Player observer = offlinePlayer.getPlayer();
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(observer == player) continue;
					hidePlayer(observer, player);
				}
				if(seeThrough) {
					seeThroughSet.add(offlinePlayer.getUniqueId());
				} else {
					seeThroughSet.remove(offlinePlayer.getUniqueId());
				}
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(observer == player) continue;
					showPlayer(observer, player);
				}
			} else {
				if(seeThrough) {
					seeThroughSet.add(offlinePlayer.getUniqueId());
				} else {
					seeThroughSet.remove(offlinePlayer.getUniqueId());
				}
			}
		}
	}
	
	private static void hidePlayer(Player player) {
		if(modifyScoreboardPackets) {
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			if(team != null) {
				try {
					Object packet = PacketPlayOutScoreboardTeam_new.newInstance();
					PacketPlayOutScoreboardTeam_teamName.set(packet, team.getName());
					PacketPlayOutScoreboardTeam_action.setInt(packet, 4);
					((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(packet)).add(player.getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player);
			for(Score score : scores) {
				try {
					Object packet = PacketPlayOutScoreboardScore_new.newInstance();
					PacketPlayOutScoreboardScore_entry.set(packet, player.getName());
					PacketPlayOutScoreboardScore_action.set(packet, EnumScoreboardAction_REMOVE.get(null));
					PacketPlayOutScoreboardScore_objective.set(packet, score.getObjective().getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) continue;
				observer.hidePlayer(player);
				try {
					for(Object packet : packets) {
						((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
					}
				} catch(Exception e) {
				}
			}
		} else {
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) continue;
				observer.hidePlayer(player);
			}
		}
	}
	
	private static void hidePlayer(Player observer, Player player) {
		if(modifyScoreboardPackets) {
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			if(team != null) {
				try {
					Object packet = PacketPlayOutScoreboardTeam_new.newInstance();
					PacketPlayOutScoreboardTeam_teamName.set(packet, team.getName());
					PacketPlayOutScoreboardTeam_action.setInt(packet, 4);
					((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(packet)).add(player.getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player);
			for(Score score : scores) {
				try {
					Object packet = PacketPlayOutScoreboardScore_new.newInstance();
					PacketPlayOutScoreboardScore_entry.set(packet, player.getName());
					PacketPlayOutScoreboardScore_action.set(packet, EnumScoreboardAction_REMOVE.get(null));
					PacketPlayOutScoreboardScore_objective.set(packet, score.getObjective().getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			observer.hidePlayer(player);
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		} else {
			observer.hidePlayer(player);
		}
	}
	
	private static void showPlayer(final Player player) {
		if(VersionHelper.require1_9()) {
			showPlayer0(player);
		} else {
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					showPlayer0(player);
				}
				
			}, 10L);
		}
	}
	
	private static void showPlayer0(Player player) {
		if(modifyScoreboardPackets) {
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			if(team != null) {
				try {
					Object packet = PacketPlayOutScoreboardTeam_new.newInstance();
					PacketPlayOutScoreboardTeam_teamName.set(packet, team.getName());
					PacketPlayOutScoreboardTeam_action.setInt(packet, 3);
					((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(packet)).add(player.getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player);
			for(Score score : scores) {
				try {
					Object packet = PacketPlayOutScoreboardScore_new.newInstance();
					PacketPlayOutScoreboardScore_entry.set(packet, player.getName());
					PacketPlayOutScoreboardScore_action.set(packet, EnumScoreboardAction_CHANGE.get(null));
					PacketPlayOutScoreboardScore_objective.set(packet, score.getObjective().getName());
					PacketPlayOutScoreboardScore_score.setInt(packet, score.getScore());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) continue;
				observer.showPlayer(player);
				try {
					for(Object packet : packets) {
						((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
					}
				} catch(Exception e) {
				}
			}
		} else {
			for(Player observer : Bukkit.getOnlinePlayers()) {
				if(observer == player) continue;
				observer.showPlayer(player);
			}
		}
	}
	
	private static void showPlayer(final Player observer, final Player player) {
		if(VersionHelper.require1_9()) {
			showPlayer0(observer, player);
		} else {
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					showPlayer0(observer, player);
				}
				
			}, 10L);
		}
	}
	
	private static void showPlayer0(Player observer, Player player) {
		if(modifyScoreboardPackets) {
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
			if(team != null) {
				try {
					Object packet = PacketPlayOutScoreboardTeam_new.newInstance();
					PacketPlayOutScoreboardTeam_teamName.set(packet, team.getName());
					PacketPlayOutScoreboardTeam_action.setInt(packet, 3);
					((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(packet)).add(player.getName());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player);
			for(Score score : scores) {
				try {
					Object packet = PacketPlayOutScoreboardScore_new.newInstance();
					PacketPlayOutScoreboardScore_entry.set(packet, player.getName());
					PacketPlayOutScoreboardScore_action.set(packet, EnumScoreboardAction_CHANGE.get(null));
					PacketPlayOutScoreboardScore_objective.set(packet, score.getObjective().getName());
					PacketPlayOutScoreboardScore_score.setInt(packet, score.getScore());
					packets.add(packet);
				} catch(Exception e) {
				}
			}
			observer.showPlayer(player);
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		} else {
			observer.showPlayer(player);
		}
	}
	
	public static void resendPackets(Player player) {
		hidePlayer(player);
		showPlayer(player);
	}
	
	public static void resendPackets() {
		for(OfflinePlayer offlinePlayer : getDisguisedPlayers()) {
			if(offlinePlayer.isOnline()) {
				Player player = offlinePlayer.getPlayer();
				hidePlayer(player);
				showPlayer(player);
			}
		}
	}
	
}