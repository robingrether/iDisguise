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
		hideEntityFromAll(livingEntity);
		disguiseMap.updateDisguise(livingEntity.getUniqueId(), disguise);
		showEntityToAll(livingEntity);
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
		hideEntityFromAll(livingEntity);
		disguiseMap.removeDisguise(livingEntity.getUniqueId());
		showEntityToAll(livingEntity);
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
						hideEntityFromOne(observer, (LivingEntity)disguisable);
					} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						hidePlayerFromOne(observer, ((OfflinePlayer)disguisable).getPlayer());
					}
				}
				if(seeThrough) {
					seeThroughSet.add(offlinePlayer.getUniqueId());
				} else {
					seeThroughSet.remove(offlinePlayer.getUniqueId());
				}
				for(Object disguisable : getDisguisedEntities()) {
					if(disguisable instanceof LivingEntity) {
						showEntityToOne(observer, (LivingEntity)disguisable);
					} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						showPlayerToOne(observer, ((OfflinePlayer)disguisable).getPlayer());
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
	
	private static void hideEntityFromAll(LivingEntity livingEntity) {
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			hidePlayerFromAll((Player)livingEntity);
			return;
		}
			
		// construct the player info packet (in case the entity is disguised as a player)
		Object playerInfoPacket = null;
		if(getDisguise(livingEntity) instanceof PlayerDisguise) {
			try {	
				playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List<Object> playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
				playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
			} catch(Exception e) {
			}
		}
		
		// do the actual sending and stuff
		for(Player observer : Bukkit.getOnlinePlayers()) {
			try {
				
				// clear the entity tracker entry
				Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
				if(entityTrackerEntry != null) {
					EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
				
				// send the player info packet
				((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacketDirectly(playerInfoPacket);
				
			} catch(Exception e) {
			}
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void hideEntityFromOne(Player observer, LivingEntity livingEntity) {
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			hidePlayerFromOne(observer, (Player)livingEntity);
			return;
		}
		
		try {
			
			// clear the entity tracker entry
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
			
		} catch(Exception e) {
		}
		
		if(getDisguise(livingEntity) instanceof PlayerDisguise) {
			try {
				
				// construct the player info packet
				Object playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List<Object> playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
				playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
				
				// send the player info packet
				((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacketDirectly(playerInfoPacket);
			} catch(Exception e) {
			}
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void hidePlayerFromAll(Player player) {
		List<Object> packets = new ArrayList<Object>();
		
		// do we care about scoreboard packets?
		if(modifyScoreboardPackets) {
			
			// construct the scoreboard packets
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
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
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
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
		}
		
		// do the actual sending and stuff
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer == player) continue;
			
			// hide the player
			observer.hidePlayer(player);
			
			// send the scoreboard packets
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		}
	}
	
	private static void hidePlayerFromOne(Player observer, Player player) {
		// hide the player
		observer.hidePlayer(player);
		
		// do we care about scoreboard packets?
		if(modifyScoreboardPackets) {
			
			// construct the scoreboard packets
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
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
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
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
			
			// send the scoreboard packets
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		}
	}
	
	private static void showEntityToAll(LivingEntity livingEntity) {
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			showPlayerToAll((Player)livingEntity);
			return;
		}
		
		// do the actual sending and stuff
		for(Player observer : Bukkit.getOnlinePlayers()) {
			try {
				
				// update the entity tracker entry
				Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
				if(entityTrackerEntry != null) {
					EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
				
			} catch(Exception e) {
			}
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void showEntityToOne(Player observer, LivingEntity livingEntity) {
		// use other function if the entity is a player
		if(livingEntity instanceof Player) {
			showPlayerToOne(observer, (Player)livingEntity);
			return;
		}
		
		// update the entity tracker entry
		try {
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftPlayer_getHandle.invoke(observer)))), livingEntity.getEntityId());
			if(entityTrackerEntry != null) {
				EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
		} catch(Exception e) {
		}
		
		// we don't care about scoreboard packets for entities
	}
	
	private static void showPlayerToAll(final Player player) {
		if(VersionHelper.require1_9()) {
			showPlayerToAll0(player);
		} else {
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					showPlayerToAll0(player);
				}
				
			}, 10L);
		}
	}
	
	private static void showPlayerToAll0(Player player) {
		List<Object> packets = new ArrayList<Object>();
		
		// do we care about scoreboard packets?
		if(modifyScoreboardPackets) {
			
			// construct the scoreboard packets
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
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
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
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
		}
		
		// do all the sending and stuff
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer == player) continue;
			
			// show the player
			observer.showPlayer(player);
			
			// send the scoreboard packets
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		}
	}
	
	private static void showPlayerToOne(final Player observer, final Player player) {
		if(VersionHelper.require1_9()) {
			showPlayerToOne0(observer, player);
		} else {
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
				
				public void run() {
					showPlayerToOne0(observer, player);
				}
				
			}, 10L);
		}
	}
	
	private static void showPlayerToOne0(Player observer, Player player) {
		// show the player
		observer.showPlayer(player);
		
		// do we care about scoreboard packets?
		if(modifyScoreboardPackets) {
			
			// construct the scoreboard packets
			List<Object> packets = new ArrayList<Object>();
			Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
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
			Set<Score> scores = Bukkit.getScoreboardManager().getMainScoreboard().getScores(player.getName());
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
			
			// send the scoreboard packets
			try {
				for(Object packet : packets) {
					((InjectedPlayerConnection)EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))).sendPacket(packet);
				}
			} catch(Exception e) {
			}
		}
	}
	
	public static void resendPackets(Player player) {
		hidePlayerFromAll(player);
		showPlayerToAll(player);
	}
	
	public static void resendPackets(LivingEntity livingEntity) {
		hideEntityFromAll(livingEntity);
		showEntityToAll(livingEntity);
	}
	
	public static void resendPackets() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				hideEntityFromAll((LivingEntity)disguisable);
				showEntityToAll((LivingEntity)disguisable);
			} else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
				hidePlayerFromAll(((OfflinePlayer)disguisable).getPlayer());
				showPlayerToAll(((OfflinePlayer)disguisable).getPlayer());
			}
		}
	}
	
}