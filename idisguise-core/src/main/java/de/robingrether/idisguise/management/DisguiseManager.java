package de.robingrether.idisguise.management;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.hooks.ScoreboardHooks;
import de.robingrether.idisguise.management.util.DisguiseMap;

import static de.robingrether.idisguise.management.Reflection.*;

public final class DisguiseManager {
	
	private DisguiseManager() {}
	
	public static boolean disguiseViewSelf;
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
		// do nothing if entity is invalid (dead or despawned)
		if(!livingEntity.isValid()) return;
		
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
	
	public static boolean isDisguised(UUID disguisable) {
		return disguiseMap.isDisguised(disguisable);
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
	
	public static boolean isDisguisedTo(UUID disguisable, Player observer) {
		return disguiseMap.isDisguised(disguisable) && disguiseMap.getDisguise(disguisable).isVisibleTo(observer);
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
	
	public static Disguise getDisguise(UUID disguisable) {
		return disguiseMap.getDisguise(disguisable);
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
		if(VersionHelper.require1_12()) {
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
		} else {
			try {
				Object minecraftServer = MinecraftServer_getServer.invoke(null);
				for(UUID disguisable : origin) {
					Object entity = MinecraftServer_getEntityByUID.invoke(minecraftServer, disguisable);
					if(entity != null) {
						destination.add(Entity_getBukkitEntity.invoke(entity));
					} else {
						OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(disguisable);
						if(offlinePlayer != null) {
							destination.add(offlinePlayer);
						}
					}
				}
			} catch(Exception e) {
				if(VersionHelper.debug()) {
					iDisguise.getInstance().getLogger().log(Level.SEVERE, "An unexpected exception occured.", e);
				}
			}
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
	
	public static synchronized void setSeeThrough(OfflinePlayer offlinePlayer, boolean seeThrough) {
		if(seeThroughSet.contains(offlinePlayer.getUniqueId()) != seeThrough) {
			if(offlinePlayer.isOnline()) {
				Player observer = offlinePlayer.getPlayer();
				for(Object disguisable : getDisguisedEntities()) {
					if(disguisable instanceof LivingEntity) {
						hideEntityFromOne((LivingEntity)disguisable, observer);
					}/* else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						hidePlayerFromOne(observer, ((OfflinePlayer)disguisable).getPlayer());
					}*/
				}
				if(seeThrough) {
					seeThroughSet.add(offlinePlayer.getUniqueId());
				} else {
					seeThroughSet.remove(offlinePlayer.getUniqueId());
				}
				for(Object disguisable : getDisguisedEntities()) {
					if(disguisable instanceof LivingEntity) {
						showEntityToOne((LivingEntity)disguisable, observer);
					}/* else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
						showPlayerToOne(observer, ((OfflinePlayer)disguisable).getPlayer());
					}*/
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
	
	public static synchronized void hideEntityFromAll(LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!(livingEntity instanceof Player) && !livingEntity.isValid()) {
			return;
		}
		
		try {
			Object playerInfoPacket = null;
			boolean isPlayer = livingEntity instanceof Player, disguisedAsPlayer = getDisguise(livingEntity) instanceof PlayerDisguise;
			
			if(isPlayer || disguisedAsPlayer) {
				// construct the packet
				playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
				playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
			}
			
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftLivingEntity_getHandle.invoke(livingEntity)))), livingEntity.getEntityId());
			
			for(Player observer : Bukkit.getOnlinePlayers()) {
				
				if(livingEntity != observer) {
					// hide the entity
					EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
				
				// send the player info removal if needed
				if(isPlayer ? disguiseViewSelf || livingEntity != observer : disguisedAsPlayer && isDisguisedTo(livingEntity, observer)) {
					PacketHandler.sendPacketUnaltered(observer, playerInfoPacket);
				}
				
			}
		} catch(Exception e) {
		}
	}
	
	public static synchronized void hideEntityFromOne(LivingEntity livingEntity, Player observer) {
		// do nothing if entity is invalid (dead or despawned)
		if(!(livingEntity instanceof Player) && !livingEntity.isValid()) {
			return;
		}
		
		try {
			Object playerInfoPacket = null;
			boolean isPlayer = livingEntity instanceof Player, disguisedAsPlayer = getDisguise(livingEntity) instanceof PlayerDisguise;
			
			if(isPlayer || disguisedAsPlayer) {
				// construct the packet
				playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
				List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
				playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, ProfileHelper.getInstance().getGameProfile(livingEntity.getUniqueId(), "", ""), 35, null, null));
			}
			
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftLivingEntity_getHandle.invoke(livingEntity)))), livingEntity.getEntityId());
			
			if(livingEntity != observer) {
				// hide the entity
				EntityTrackerEntry_clear.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
			
			// send the player info removal if needed
			if(isPlayer ? disguiseViewSelf || livingEntity != observer : disguisedAsPlayer && isDisguisedTo(livingEntity, observer)) {
				PacketHandler.sendPacketUnaltered(observer, playerInfoPacket);
			}
			
		} catch(Exception e) {
		}
	}
	
	public static void showEntityToAll(final LivingEntity livingEntity) {
		// do nothing if entity is invalid (dead or despawned)
		if(!(livingEntity instanceof Player) && !livingEntity.isValid()) {
			return;
		}
		
		if(VersionHelper.require1_9()) {
			showEntityToAll0(livingEntity);
		} else {
			// we have to delay the reappearance for 1.8.0 clients
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), () -> showEntityToAll0(livingEntity), 10L);
		}
	}
	
	private static synchronized void showEntityToAll0(LivingEntity livingEntity) {
		try {
			Object playerInfoPacket = null;
			boolean isPlayer = livingEntity instanceof Player;
			
			if(isPlayer) {
				// construct the packet
				Object players = Array.newInstance(EntityPlayer, 1);
				Array.set(players, 0, CraftPlayer_getHandle.invoke(livingEntity));
				playerInfoPacket = PacketPlayOutPlayerInfo_new_full.newInstance(EnumPlayerInfoAction_ADD_PLAYER.get(null), players);
			}
			
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftLivingEntity_getHandle.invoke(livingEntity)))), livingEntity.getEntityId());
			
			for(Player observer : Bukkit.getOnlinePlayers()) {
				
				// send the player info
				if(isPlayer && (disguiseViewSelf || livingEntity != observer)) {
					PacketHandler.sendPacket(observer, playerInfoPacket);
				}
				
				if(livingEntity != observer) {
					// show the entity
					EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
				}
				
			}
			
			if(isPlayer && disguiseViewSelf) {
				respawnPlayerToSelf((Player)livingEntity);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(modifyScoreboardPackets && livingEntity instanceof Player) {
			// update scoreboard hooks
			ScoreboardHooks.updatePlayer((Player)livingEntity);
		}
	}
	
	public static void showEntityToOne(final LivingEntity livingEntity, final Player observer) {
		// do nothing if entity is invalid (dead or despawned)
		if(!(livingEntity instanceof Player) && !livingEntity.isValid()) {
			return;
		}
		
		if(VersionHelper.require1_9()) {
			showEntityToOne0(livingEntity, observer);
		} else {
			// we have to delay the reappearance for 1.8.0 clients
			Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), () -> showEntityToOne0(livingEntity, observer), 10L);
		}
	}
	
	private static synchronized void showEntityToOne0(LivingEntity livingEntity, Player observer) {
		try {
			Object playerInfoPacket = null;
			boolean isPlayer = livingEntity instanceof Player;
			
			if(isPlayer) {
				// construct the packet
				Object players = Array.newInstance(EntityPlayer, 1);
				Array.set(players, 0, CraftPlayer_getHandle.invoke(livingEntity));
				playerInfoPacket = PacketPlayOutPlayerInfo_new_full.newInstance(EnumPlayerInfoAction_ADD_PLAYER.get(null), players);
			}
			
			Object entityTrackerEntry = IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftLivingEntity_getHandle.invoke(livingEntity)))), livingEntity.getEntityId());
			
			// send the player info
			if(isPlayer && (disguiseViewSelf || livingEntity != observer)) {
				PacketHandler.sendPacket(observer, playerInfoPacket);
			}
			
			if(livingEntity != observer) {
				// show the entity
				EntityTrackerEntry_updatePlayer.invoke(entityTrackerEntry, CraftPlayer_getHandle.invoke(observer));
			}
			
			if(isPlayer && disguiseViewSelf && livingEntity == observer) {
				respawnPlayerToSelf((Player)livingEntity);
			}
		} catch(Exception e) {
		}
		
		if(modifyScoreboardPackets && livingEntity instanceof Player) {
			// update scoreboard hooks
			ScoreboardHooks.updatePlayer((Player)livingEntity);
		}
	}
	
	private static void respawnPlayerToSelf(Player player) throws Exception {
		Object entityPlayer = CraftPlayer_getHandle.invoke(player);
		Object world = Entity_world.get(entityPlayer);
		Location location = player.getLocation();
		
		EntityTracker_untrackPlayer.invoke(WorldServer_entityTracker.get(world), entityPlayer);
		
		PlayerChunkMap_removePlayer.invoke(WorldServer_getPlayerChunkMap.invoke(world), entityPlayer);
		
		Object actualDimension;
		Object tempDimension;
		if(VersionHelper.require1_13()) {
			actualDimension = WorldServer_dimension.get(world);
			tempDimension = (int)DimensionManager_getDimensionID.invoke(actualDimension) >= 0 ? DimensionManager_NETHER.get(null) : DimensionManager_OVERWORLD.get(null);
		} else {
			actualDimension = location.getWorld().getEnvironment().getId();
			tempDimension = (int)actualDimension == 1 ? 0 : (int)actualDimension + 1;
		}
		PacketHandler.sendPacketUnaltered(player, PacketPlayOutRespawn_new.newInstance(tempDimension, World_getDifficulty.invoke(world), World_getType.invoke(world), PlayerInteractManager_getGameMode.invoke(EntityPlayer_playerInteractManager.get(entityPlayer))));
		PacketHandler.sendPacketUnaltered(player, PacketPlayOutRespawn_new.newInstance(actualDimension, World_getDifficulty.invoke(world), World_getType.invoke(world), PlayerInteractManager_getGameMode.invoke(EntityPlayer_playerInteractManager.get(entityPlayer))));
		
		if(VersionHelper.require1_9()) {
			PacketHandler.sendPacketUnaltered(player, PacketPlayOutPosition_new.newInstance(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), Collections.emptySet(), 27));
		} else {
			PacketHandler.sendPacketUnaltered(player, PacketPlayOutPosition_new.newInstance(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), Collections.emptySet()));
		}
		PacketHandler.sendPacketUnaltered(player, PacketPlayOutSpawnPosition_new.newInstance(World_getSpawn.invoke(world)));
		PacketHandler.sendPacketUnaltered(player, PacketPlayOutExperience_new.newInstance(EntityHuman_exp.get(entityPlayer), EntityHuman_expTotal.get(entityPlayer), EntityHuman_expLevel.get(entityPlayer)));
		
		PlayerList_sendWorldInfo.invoke(MinecraftServer_getPlayerList.invoke(MinecraftServer_getServer.invoke(null)), entityPlayer, world);
		
		PlayerChunkMap_addPlayer.invoke(WorldServer_getPlayerChunkMap.invoke(world), entityPlayer);
		
		PlayerList_updateClient.invoke(MinecraftServer_getPlayerList.invoke(MinecraftServer_getServer.invoke(null)), entityPlayer);
		
		EntityPlayer_updateAbilities.invoke(entityPlayer);
		
		for(Object effect : (Collection)EntityLiving_getEffects.invoke(entityPlayer)) {
			PacketHandler.sendPacketUnaltered(player, PacketPlayOutEntityEffect_new.newInstance(player.getEntityId(), effect));
		}
	}
	
	public static void resendPackets(Player player) {
		/*hidePlayerFromAll(player);
		showPlayerToAll(player);*/ resendPackets((LivingEntity)player);
	}
	
	public static synchronized void resendPackets(LivingEntity livingEntity) {
		hideEntityFromAll(livingEntity);
		showEntityToAll(livingEntity);
	}
	
	public static synchronized void resendPackets() {
		for(Object disguisable : getDisguisedEntities()) {
			if(disguisable instanceof LivingEntity) {
				hideEntityFromAll((LivingEntity)disguisable);
				showEntityToAll((LivingEntity)disguisable);
			}/* else if(disguisable instanceof OfflinePlayer && ((OfflinePlayer)disguisable).isOnline()) {
				hidePlayerFromAll(((OfflinePlayer)disguisable).getPlayer());
				showPlayerToAll(((OfflinePlayer)disguisable).getPlayer());
			}*/
		}
	}
	
}