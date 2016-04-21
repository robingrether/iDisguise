package de.robingrether.idisguise;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
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
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(!plugin.enabled()) {
			event.disallow(Result.KICK_OTHER, "Server start/reload has not finished yet");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof Player) {
			Player target = (Player)event.getTarget();
			if(plugin.getConfiguration().getBoolean(Configuration.DISABLE_MOB_TARGET) && DisguiseManager.getInstance().isDisguised(target)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.isCancelled()) {
			return;
		}
		if(event.getEntity() instanceof Player) {
			Player damagee = (Player)event.getEntity();
			Entity damager = event.getDamager();
			if(damager instanceof Player) {
				if(plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_HURT) && DisguiseManager.getInstance().isDisguised(damagee)) {
					DisguiseManager.getInstance().undisguise(damagee);
					damagee.sendMessage(ChatColor.GOLD + "You were undisguised because you were hit by another player.");
				}
				if(plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_ATTACK) && DisguiseManager.getInstance().isDisguised((Player)damager)) {
					DisguiseManager.getInstance().undisguise((Player)damager);
					((Player)damager).sendMessage(ChatColor.GOLD + "You were undisguised because you attacked another player.");
				}
			} else {
				if(!plugin.getConfiguration().getBoolean(Configuration.ALLOW_DAMAGE)) {
					event.setCancelled(true);
				} else if(damager instanceof Projectile && plugin.getConfiguration().getBoolean(Configuration.UNDISGUISE_PROJECTILE) && DisguiseManager.getInstance().isDisguised(damagee)) {
					DisguiseManager.getInstance().undisguise(damagee);
					damagee.sendMessage(ChatColor.GOLD + "You were undisguised because you were hit by a projectile.");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ChannelInjector.getInstance().inject(player);
		PlayerHelper.getInstance().addPlayer(player);
		GhostFactory.getInstance().addPlayer(player.getName());
		if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).isGhost()) {
			if(plugin.getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
				GhostFactory.getInstance().addPlayer(((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getName());
				GhostFactory.getInstance().addGhost(player);
			} else {
				DisguiseManager.getInstance().undisguise(player);
				player.sendMessage(ChatColor.GOLD + "You were undisguised because ghost disguises are disabled.");
			}
		}
		if(DisguiseManager.getInstance().isDisguised(player)) {
			player.sendMessage(ChatColor.GOLD + "You are still disguised. Use " + ChatColor.ITALIC + "/disguise status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.");
		}
		if(plugin.getConfiguration().getBoolean(Configuration.REPLACE_JOIN_MESSAGES)) {
			if(event.getJoinMessage() != null && DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getName()));
				} else {
					event.setJoinMessage(null);
				}
			}
		}
		if(player.hasPermission("iDisguise.update") && plugin.getConfiguration().getBoolean(Configuration.CHECK_FOR_UPDATES)) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new UpdateCheck(plugin, player, ChatColor.GOLD + "An update for iDisguise is available: " + ChatColor.ITALIC + "%s", plugin.getConfiguration().getBoolean(Configuration.AUTO_DOWNLOAD_UPDATES)), 20L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.getConfiguration().getBoolean(Configuration.REPLACE_JOIN_MESSAGES)) {
			if(event.getQuitMessage() != null && DisguiseManager.getInstance().isDisguised(player)) {
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
		Player player = event.getPlayer();
		if(DisguiseManager.getInstance().isDisguised(player)) {
			if(!plugin.isDisguisingPermittedInWorld(player.getWorld()) && !player.hasPermission("iDisguise.everywhere")) {
				UndisguiseEvent undisguiseEvent = new UndisguiseEvent(player, DisguiseManager.getInstance().getDisguise(player).clone(), false);
				plugin.getServer().getPluginManager().callEvent(undisguiseEvent);
				if(!undisguiseEvent.isCancelled()) {
					DisguiseManager.getInstance().undisguise(player);
					player.sendMessage(ChatColor.GOLD + "You were undisguised because disguising is prohibited in this world.");
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