package de.robingrether.idisguise;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.ChannelInjector;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.util.StringUtil;

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
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ChannelInjector.getInstance().inject(player);
		PlayerHelper.getInstance().addPlayer(player);
		GhostFactory.getInstance().addPlayer(player.getName());
		if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise && ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).isGhost()) {
			if(plugin.getConfiguration().ENABLE_GHOST_DISGUISE) {
				GhostFactory.getInstance().addPlayer(((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getSkinName());
				GhostFactory.getInstance().addGhost(player);
			} else {
				DisguiseManager.getInstance().undisguise(player);
				player.sendMessage(ChatColor.GOLD + "You were undisguised because ghost disguises are disabled.");
			}
		}
		if(DisguiseManager.getInstance().isDisguised(player)) {
			player.sendMessage(ChatColor.GOLD + "You are still disguised. Use " + ChatColor.ITALIC + "/disguise status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.");
		}
		if(plugin.getConfiguration().MODIFY_MESSAGE_JOIN) {
			if(event.getJoinMessage() != null && DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setJoinMessage(event.getJoinMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getDisplayName()));
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
			if(event.getQuitMessage() != null && DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setQuitMessage(event.getQuitMessage().replace(player.getName(), ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getDisplayName()));
				} else {
					event.setQuitMessage(null);
				}
			}
		}
		if(!plugin.getConfiguration().KEEP_DISGUISE_LEAVE) {
			if(DisguiseManager.getInstance().isDisguised(player)) {
				DisguiseManager.getInstance().undisguise(player);
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
		if(event.getDeathMessage() != null) {
			Player player = event.getEntity();
			if(DisguiseManager.getInstance().isDisguised(player)) {
				if(DisguiseManager.getInstance().getDisguise(player) instanceof PlayerDisguise) {
					event.setDeathMessage(event.getDeathMessage().replaceAll("(" + player.getDisplayName() + "|" + player.getName() + ")", ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(player)).getDisplayName()));
				} else {
					event.setDeathMessage(null);
					return;
				}
			}
			if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)player.getLastDamageCause()).getDamager() instanceof Player) {
				Player killer = (Player)((EntityDamageByEntityEvent)player.getLastDamageCause()).getDamager();
				if(DisguiseManager.getInstance().isDisguised(killer)) {
					if(DisguiseManager.getInstance().getDisguise(killer) instanceof PlayerDisguise) {
						event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer.getDisplayName() + "|" + killer.getName() + ")", ((PlayerDisguise)DisguiseManager.getInstance().getDisguise(killer)).getDisplayName()));
					} else if(DisguiseManager.getInstance().getDisguise(killer) instanceof MobDisguise) {
						event.setDeathMessage(event.getDeathMessage().replaceAll("(" + killer.getDisplayName() + "|" + killer.getName() + ")", StringUtil.capitalizeFully(DisguiseManager.getInstance().getDisguise(killer).getType().name().replace('_', ' '))));
					} else {
						event.setDeathMessage(null);
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
	
}