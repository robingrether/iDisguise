package de.robingrether.idisguise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.ProfileHelper;
import de.robingrether.idisguise.management.channel.ChannelInjector;
import de.robingrether.idisguise.management.hooks.ScoreboardHooks;
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.util.StringUtil;

public class EventListener implements Listener {
	
	private final iDisguise plugin;
	
	EventListener(iDisguise plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(!plugin.enabled()) {
			event.disallow(Result.KICK_OTHER, "Server start/reload has not finished yet");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinLowest(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ChannelInjector.inject(player);
		EntityIdList.addEntity(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinHighest(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ProfileHelper.getInstance().registerGameProfile(player);
		if(DisguiseManager.isDisguised(player)) {
			if(!plugin.getLanguage().JOIN_DISGUISED.isEmpty())
				player.sendMessage(plugin.getLanguage().JOIN_DISGUISED);
		}
		if(plugin.getConfiguration().MODIFY_MESSAGE_JOIN) {
			if(event.getJoinMessage() != null && DisguiseManager.isDisguised(player)) {
				if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName()));
				} else {
					event.setJoinMessage(null);
				}
			}
		}
		if(player.hasPermission("iDisguise.update") && plugin.getConfiguration().UPDATE_CHECK) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new UpdateCheck(plugin, player, plugin.getConfiguration().UPDATE_DOWNLOAD), 20L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.getConfiguration().MODIFY_MESSAGE_LEAVE) {
			if(event.getQuitMessage() != null && DisguiseManager.isDisguised(player)) {
				if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
					event.setQuitMessage(event.getQuitMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName()));
				} else {
					event.setQuitMessage(null);
				}
			}
		}
		if(!plugin.getConfiguration().KEEP_DISGUISE_LEAVE) {
			if(DisguiseManager.isDisguised(player)) {
				DisguiseManager.undisguise(player);
			}
		}
		ChannelInjector.remove(player);
		EntityIdList.removeEntity(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getDeathMessage() != null) {
			Player player = event.getEntity();
			if(plugin.getConfiguration().MODIFY_MESSAGE_DEATH) {
				if(DisguiseManager.isDisguised(player)) {
					if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
						event.setDeathMessage(event.getDeathMessage().replaceAll("(" + player.getDisplayName() + "|" + player.getName() + ")", ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName()));
					} else {
						event.setDeathMessage(null);
						return;
					}
				}
			}
			if(plugin.getConfiguration().MODIFY_MESSAGE_KILL) {
				if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					Entity damager = ((EntityDamageByEntityEvent)player.getLastDamageCause()).getDamager();
					LivingEntity killer = null;
					if(damager instanceof LivingEntity) {
						killer = (LivingEntity)damager;
					} else if(damager instanceof Projectile) {
						killer = (LivingEntity)((Projectile)damager).getShooter();
					}
					if(killer != null && DisguiseManager.isDisguised(killer)) {
						if(killer instanceof Player) {
							Player killer2 = (Player)killer;
							if(DisguiseManager.getDisguise(killer2) instanceof PlayerDisguise) {
								event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer2.getDisplayName() + "|" + killer2.getName() + ")", ((PlayerDisguise)DisguiseManager.getDisguise(killer2)).getDisplayName()));
							} else if(DisguiseManager.getDisguise(killer2) instanceof MobDisguise) {
								event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer2.getDisplayName() + "|" + killer2.getName() + ")", StringUtil.capitalizeFully(DisguiseManager.getDisguise(killer2).getType().name().replace('_', ' '))));
							} else {
								event.setDeathMessage(null);
							}
						} else {
							if(DisguiseManager.getDisguise(killer) instanceof PlayerDisguise) {
								event.setDeathMessage(event.getDeathMessage().replaceAll(killer.getName(), ((PlayerDisguise)DisguiseManager.getDisguise(killer)).getDisplayName()));
							} else if(DisguiseManager.getDisguise(killer) instanceof MobDisguise) {
								event.setDeathMessage(event.getDeathMessage().replaceAll(killer.getName(), StringUtil.capitalizeFully(DisguiseManager.getDisguise(killer).getType().name().replace('_', ' '))));
							} else {
								event.setDeathMessage(null);
							}
						}
					}
				} else {
					Player killer = player.getKiller();
					if(killer != null && DisguiseManager.isDisguised(killer)) {
						if(DisguiseManager.getDisguise(killer) instanceof PlayerDisguise) {
							event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer.getDisplayName() + "|" + killer.getName() + ")", ((PlayerDisguise)DisguiseManager.getDisguise(killer)).getDisplayName()));
						} else if(DisguiseManager.getDisguise(killer) instanceof MobDisguise) {
							event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer.getDisplayName() + "|" + killer.getName() + ")", StringUtil.capitalizeFully(DisguiseManager.getDisguise(killer).getType().name().replace('_', ' '))));
						} else {
							event.setDeathMessage(null);
						}
					}
				}
			}
		}
	}
	
	private Map<UUID, Long> mapLastMessageSent = new ConcurrentHashMap<UUID, Long>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(DisguiseManager.isDisguised(player) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.SHULKER)) {
			event.setCancelled(true);
			long lastSent = mapLastMessageSent.containsKey(player.getUniqueId()) ? mapLastMessageSent.get(player.getUniqueId()) : 0L;
			if(lastSent + 3000L < System.currentTimeMillis()) {
				if(!plugin.getLanguage().MOVE_AS_SHULKER.isEmpty())
					player.sendMessage(plugin.getLanguage().MOVE_AS_SHULKER);
				mapLastMessageSent.put(player.getUniqueId(), System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		final LivingEntity livingEntity = event.getEntity();
		final int entityId = livingEntity.getEntityId();
		
		if(livingEntity instanceof Player) return; // we have a seperate method for players
		
		if(DisguiseManager.isDisguised(livingEntity)) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				
				public void run() {
					DisguiseManager.undisguise(livingEntity);
				}
				
			}, 100L);
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			
			public void run() {
				EntityIdList.removeEntity(entityId);
			}
			
		}, 200L);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		final LivingEntity livingEntity = event.getEntity();
		final int entityId = livingEntity.getEntityId();
		EntityIdList.addEntity(livingEntity);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			
			public void run() {
				if(livingEntity == null || !livingEntity.isValid()) {
					EntityIdList.removeEntity(entityId);
				}
			}
			
		}, 40L);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		for(Entity entity : event.getChunk().getEntities()) {
			if(entity instanceof LivingEntity) {
				EntityIdList.addEntity((LivingEntity)entity);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event) {
		final Chunk chunk = event.getChunk();
		final List<Integer> entityIds = new ArrayList<Integer>();
		for(Entity entity : chunk.getEntities()) {
			if(entity instanceof LivingEntity) {
				entityIds.add(entity.getEntityId());
			}
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			
			public void run() {
				if(!chunk.isLoaded()) {
					for(int entityId : entityIds) {
						EntityIdList.removeEntity(entityId);
					}
				}
			}
			
		}, 40L); // TODO: increase delay?
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if(plugin.getConfiguration().MODIFY_SCOREBOARD_PACKETS && StringUtil.equals(event.getPlugin().getName(), "NametagEdit", "ColoredTags")) ScoreboardHooks.setup();
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if(plugin.getConfiguration().MODIFY_SCOREBOARD_PACKETS && StringUtil.equals(event.getPlugin().getName(), "NametagEdit", "ColoredTags")) ScoreboardHooks.setup();
	}
	
}