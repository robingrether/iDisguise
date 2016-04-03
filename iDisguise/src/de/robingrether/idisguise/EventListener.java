package de.robingrether.idisguise;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.io.Configuration;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.ChannelInjector;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PlayerHelper;

public class EventListener implements Listener {
	
	private iDisguise plugin;
	
	public EventListener(iDisguise plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if(!plugin.enabled()) {
			event.disallow(Result.KICK_OTHER, "Server start/reload has not finished yet");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof Player) {
			Player target = (Player)event.getTarget();
			if(DisguiseManager.getInstance().isDisguised(target) && plugin.getConfiguration().getBoolean(Configuration.DISABLE_MOB_TARGET)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntityType() == EntityType.PLAYER) {
			Player p = (Player)event.getEntity();
			if(!event.isCancelled()) {
				if(DisguiseManager.getInstance().isDisguised(p)) {
					if(event.getCause() == DamageCause.ENTITY_ATTACK) {
						if(!plugin.getConfiguration().getBoolean(Configuration.ALLOW_DAMAGE)) {
							event.setCancelled(true);
						}
						if(plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_HURT)) {
							UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getInstance().getDisguise(p).clone(), false);
							plugin.getServer().getPluginManager().callEvent(undisEvent);
							if(!undisEvent.isCancelled()) {
								DisguiseManager.getInstance().undisguise(p);
							}
						}
					} else if(event.getCause() == DamageCause.PROJECTILE) {
						if(plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_PROJECTILE)) {
							UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getInstance().getDisguise(p).clone(), false);
							plugin.getServer().getPluginManager().callEvent(undisEvent);
							if(!undisEvent.isCancelled()) {
								DisguiseManager.getInstance().undisguise(p);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof Player) {
			Player damager = (Player)event.getDamager();
			if(DisguiseManager.getInstance().isDisguised(damager) && plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_ATTACK)) {
				UndisguiseEvent undisEvent = new UndisguiseEvent(damager, DisguiseManager.getInstance().getDisguise(damager).clone(), false);
				plugin.getServer().getPluginManager().callEvent(undisEvent);
				if(!undisEvent.isCancelled()) {
					DisguiseManager.getInstance().undisguise(damager);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(player.hasPermission("iDisguise.update") && plugin.getConfiguration().getBoolean(Configuration.CHECK_FOR_UPDATES)) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new UpdateCheck(plugin, player, ChatColor.GOLD + "An update for iDisguise is available: " + ChatColor.ITALIC + "%s"), 20L);
		}
		if(DisguiseManager.getInstance().isDisguised(player)) {
			player.sendMessage(ChatColor.GOLD + "You are still disguised. Use " + ChatColor.ITALIC + "/disguise status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.");
		}
		if(plugin.getConfiguration().getBoolean(Configuration.REPLACE_JOIN_MESSAGES)) {
			if(player != null && DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getName()));
				} else {
					event.setJoinMessage(null);
				}
			}
		}
		ChannelInjector.getInstance().inject(player);
		GhostFactory.getInstance().addPlayer(player.getName());
		if(DisguiseManager.getInstance().isDisguised(player) && DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.GHOST)) {
			if(plugin.getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
				GhostFactory.getInstance().addGhost(player);
			} else {
				DisguiseManager.getInstance().undisguise(player);
				player.sendMessage(ChatColor.GOLD + "You were undisguised because ghost disguises are disable.");
			}
		}
		PlayerHelper.getInstance().addPlayer(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.getConfiguration().getBoolean(Configuration.REPLACE_JOIN_MESSAGES)) {
			if(player != null && DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setQuitMessage(event.getQuitMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getName()));
				} else {
					event.setQuitMessage(null);
				}
			}
		}
		ChannelInjector.getInstance().remove(player);
		GhostFactory.getInstance().removeGhost(player);
		PlayerHelper.getInstance().removePlayer(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player p = event.getPlayer();
		if(DisguiseManager.getInstance().isDisguised(p)) {
			if(!plugin.isDisguisingPermittedInWorld(p.getWorld()) && !p.hasPermission("iDisguise.everywhere")) {
				UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getInstance().getDisguise(p).clone(), false);
				plugin.getServer().getPluginManager().callEvent(undisEvent);
				if(!undisEvent.isCancelled()) {
					DisguiseManager.getInstance().undisguise(p);
					p.sendMessage(ChatColor.GOLD + "You were undisguised because disguising is prohibited in this world.");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(plugin.getConfiguration().getBoolean(Configuration.REPLACE_DEATH_MESSAGES)) {
			if(event.getDeathMessage() != null) {
				String[] words = event.getDeathMessage().split(" ");
				for(String word : words) {
					Player player = Bukkit.getPlayer(word);
					if(player != null && DisguiseManager.getInstance().isDisguised(player)) {
						if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
							event.setDeathMessage(event.getDeathMessage().replace(word, ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getName()));
						} else {
							event.setDeathMessage(null);
							break;
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
		if(DisguiseManager.getInstance().isDisguised(player) && DisguiseManager.getInstance().getDisguise(player).getType().equals(DisguiseType.SHULKER)) {
			event.setCancelled(true);
			long lastSent = mapLastMessageSent.containsKey(player.getUniqueId()) ? mapLastMessageSent.get(player.getUniqueId()) : 0L;
			if(lastSent + 3000L < System.currentTimeMillis()) {
				player.sendMessage(ChatColor.RED + "You must not move while you are disguised as a shulker.");
				mapLastMessageSent.put(player.getUniqueId(), System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if(DisguiseManager.getInstance().isDisguised(player) && plugin.getConfiguration().getBoolean(Configuration.DISABLE_ITEM_PICK_UP)) {
			event.setCancelled(true);
		}
	}
	
}