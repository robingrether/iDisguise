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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.channel.InjectedPlayerConnection;
import de.robingrether.idisguise.management.util.DisguiseMap;

import static de.robingrether.idisguise.management.Reflection.*;

public final class DisguiseManager {
	
	private DisguiseManager() {}
	
	public static boolean modifyScoreboardPackets;
	
	private static DisguiseMap disguiseMap = DisguiseMap.emptyMap();
	private static Set<UUID> seeThroughSet = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
	
	public static synchronized void disguise(OfflinePlayer offlinePlayer, Disguise disguise) {
		if(offlinePlayer.isOnline()) {
			disguise((LivingEntity)offlinePlayer.getPlayer(), disguise);
		} else {
			disguiseMap.updateDisguise(offlinePlayer.getUniqueId(), disguise);
		}
	}
	
	public static synchronized void disguise(Player player, Disguise disguise) {
		disguise((LivingEntity)player, disguise);
	}
	
	public static synchronized void disguise(LivingEntity livingEntity, Disguise disguise) {
		hideEntity(livingEntity);
		disguiseMap.updateDisguise(livingEntity.getUniqueId(), disguise);
		showEntity(livingEntity);
	}
	
	public static synchronized Disguise undisguise(OfflinePlayer offlinePlayer) {
		if(offlinePlayer.isOnline()) {
			return undisguise((LivingEntity)offlinePlayer.getPlayer());
		} else {
			return disguiseMap.removeDisguise(offlinePlayer.getUniqueId());
		}
	}
	
	public static synchronized Disguise undisguise(Player player) {
		return undisguise((LivingEntity)player);
	}
	
	public static synchronized Disguise undisguise(LivingEntity livingEntity) {
		Disguise disguise = disguiseMap.getDisguise(livingEntity.getUniqueId());
		if(disguise == null) {
			return null;
		}
		hideEntity(livingEntity);
		disguiseMap.removeDisguise(livingEntity.getUniqueId());
		showEntity(livingEntity);
		return disguise;
	}
	
	public static synchronized void undisguiseAll() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				undisguise((LivingEntity)disguisable);
			} else if(disguisable instanceof OfflinePlayer) {
				undisguise((OfflinePlayer)disguisable);
			}
		}
	}
	
	public static boolean isDisguised(OfflinePlayer offlinePlayer) {
		return disguiseMap.isDisguised(offlinePlayer.getUniqueId());
	}
	
	public static boolean isDisguised(Player player) {
		return disguiseMap.isDisguised(player.getUniqueId());
	}
	
	public static boolean isDisguised(LivingEntity livingEntity) {
		return disguiseMap.isDisguised(livingEntity.getUniqueId());
	}
	
	public static boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer) {
		return disguiseMap.isDisguised(offlinePlayer.getUniqueId()) && disguiseMap.getDisguise(offlinePlayer.getUniqueId()).isVisibleTo(observer);
	}
	
	public static boolean isDisguisedTo(Player player, Player observer) {
		return disguiseMap.isDisguised(player.getUniqueId()) && disguiseMap.getDisguise(player.getUniqueId()).isVisibleTo(observer);
	}
	
	public static boolean isDisguisedTo(LivingEntity livingEntity, Player observer) {
		return disguiseMap.isDisguised(livingEntity.getUniqueId()) && disguiseMap.getDisguise(livingEntity.getUniqueId()).isVisibleTo(observer);
	}
	
	public static Disguise getDisguise(OfflinePlayer offlinePlayer) {
		return disguiseMap.getDisguise(offlinePlayer.getUniqueId());
	}
	
	public static Disguise getDisguise(Player player) {
		return disguiseMap.getDisguise(player.getUniqueId());
	}
	
	public static Disguise getDisguise(LivingEntity livingEntity) {
		return disguiseMap.getDisguise(livingEntity.getUniqueId());
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
	
	public static Set<Object> getDisguisedEntities() {
		Set<UUID> origin = disguiseMap.getDisguisedEntities();
		Set<Object> destination = new HashSet<Object>();
		try {
			for(UUID disguisable : origin) {
				Entity entity = Bukkit.getEntity(disguisable);
				if(entity != null) {
					destination.add(entity);
				} else {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(disguisable);
					if(offlinePlayer != null) {
						destination.add(offlinePlayer);
					}
				}
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
				for(Object disguisable : getDisguisedEntities()) {
					if(disguisable instanceof LivingEntity) {
						hideEntity(observer, (LivingEntity)disguisable);
					} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						hidePlayer(observer, ((OfflinePlayer)disguisable).getPlayer());
					}
				}
				if(seeThrough) {
					seeThroughSet.add(offlinePlayer.getUniqueId());
				} else {
					seeThroughSet.remove(offlinePlayer.getUniqueId());
				}
				for(Object disguisable : getDisguisedEntities()) {
					if(disguisable instanceof LivingEntity) {
						showEntity(observer, (LivingEntity)disguisable);
					} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						showPlayer(observer, ((OfflinePlayer)disguisable).getPlayer());
					}
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
	
	private static void hideEntity(LivingEntity livingEntity) {
		if(livingEntity instanceof Player) {
			hidePlayer((Player)livingEntity);
			return;
		}
		Object packet = null;
		if(getDisguise(livingEntity) instanceof PlayerDisguise) {
			try {	
				packet = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(packet, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List<Object> playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(packet);
				playerInfoList.add(PlayerInfoData_new.newInstance(packet, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
			} catch(Exception e) {
			}
		}
		for(Player observer : Bukkit.getOnlinePlayers()) {
			try {
				Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
				if(entityTrackerEntry != null) {
					EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
				((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
			} catch(Exception e) {
			}
		}
	}
	
	private static void hideEntity(Player observer, LivingEntity livingEntity) {
		if(livingEntity instanceof Player) {
			hidePlayer(observer, (Player)livingEntity);
			return;
		}
		Object packet = null;
		if(getDisguise(livingEntity) instanceof PlayerDisguise) {
			try {	
				packet = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(packet, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List<Object> playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(packet);
				playerInfoList.add(PlayerInfoData_new.newInstance(packet, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
			} catch(Exception e) {
			}
		}
		try {
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
			((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
		} catch(Exception e) {
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
	
	private static void showEntity(LivingEntity livingEntity) {
		if(livingEntity instanceof Player) {
			showPlayer((Player)livingEntity);
			return;
		}
		for(Player observer : Bukkit.getOnlinePlayers()) {
			try {
				Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
				if(entityTrackerEntry != null) {
					EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
			} catch(Exception e) {
			}
		}
	}
	
	private static void showEntity(Player observer, LivingEntity livingEntity) {
		if(livingEntity instanceof Player) {
			showPlayer(observer, (Player)livingEntity);
			return;
		}
		try {
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
		} catch(Exception e) {
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
	
	public static void resendPackets(LivingEntity livingEntity) {
		hideEntity(livingEntity);
		showEntity(livingEntity);
	}
	
	public static void resendPackets() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				hideEntity((LivingEntity)disguisable);
				showEntity((LivingEntity)disguisable);
			} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
				hidePlayer(((OfflinePlayer)disguisable).getPlayer());
				showPlayer(((OfflinePlayer)disguisable).getPlayer());
			}
		}
	}
	
}