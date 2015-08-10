package de.robingrether.idisguise;

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
//import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.ChannelHandler;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PlayerUtil;
import de.robingrether.idisguise.sound.SoundSystem;

public class iDisguiseListener implements Listener {
	
	private iDisguise plugin;
	
	public iDisguiseListener(iDisguise plugin) {
		this.plugin = plugin;
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onDisguise(DisguiseEvent event) {
		Player player = event.getPlayer();
		Disguise disguise = event.getDisguise();
		boolean cancel = false;
		event.setCancelled(cancel);
	}*/
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof Player) {
			Player target = (Player)event.getTarget();
			if(DisguiseManager.isDisguised(target) && !plugin.canMobsTargetDisguisedPlayers()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntityType() == EntityType.PLAYER) {
			Player p = (Player)event.getEntity();
			if(!event.isCancelled()) {
				if(DisguiseManager.isDisguised(p)) {
					if(event.getCause() == DamageCause.ENTITY_ATTACK) {
						if(!plugin.canDisguisedPlayersBeDamaged()) {
							event.setCancelled(true);
						}
						if(plugin.undisguisePlayerWhenHitByLiving()) {
							UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getDisguise(p).clone(), false);
							plugin.getServer().getPluginManager().callEvent(undisEvent);
							if(!undisEvent.isCancelled()) {
								DisguiseManager.undisguiseToAll(p);
							}
						}
					} else if(event.getCause() == DamageCause.PROJECTILE) {
						if(plugin.undisguisePlayerWhenHitByProjectile()) {
							UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getDisguise(p).clone(), false);
							plugin.getServer().getPluginManager().callEvent(undisEvent);
							if(!undisEvent.isCancelled()) {
								DisguiseManager.undisguiseToAll(p);
							}
						}
					}
					if(!event.isCancelled() && DisguiseManager.isDisguised(p)) {
						SoundSystem.playHurtSound(p, DisguiseManager.getDisguise(p).getType());
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntityType() == EntityType.PLAYER && event.getDamager() instanceof Player) {
			Player damager = (Player)event.getDamager();
			if(DisguiseManager.isDisguised(damager) && plugin.undisguisePlayerWhenHitsOtherPlayer()) {
				UndisguiseEvent undisEvent = new UndisguiseEvent(damager, DisguiseManager.getDisguise(damager).clone(), false);
				plugin.getServer().getPluginManager().callEvent(undisEvent);
				if(!undisEvent.isCancelled()) {
					DisguiseManager.undisguiseToAll(damager);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(p.hasPermission("iDisguise.admin") && plugin.checkForUpdates()) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new UpdateCheck("iDisguise v" + plugin.getVersion(), p, ChatColor.GREEN + plugin.lang.getString("update.available")), 20L);
		}
		if(DisguiseManager.isDisguised(p)) {
			p.sendMessage(ChatColor.GREEN + plugin.lang.getString("listener.join.disguised"));
		}
		ChannelHandler.addHandler(p);
		GhostFactory.addPlayer(p);
		PlayerUtil.addPlayer(p);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ChannelHandler.removeHandler(player);
		PlayerUtil.removePlayer(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player p = event.getPlayer();
		if(DisguiseManager.isDisguised(p)) {
			if(!plugin.isDisguisingPermittedInWorld(p.getWorld()) && !p.hasPermission("iDisguise.admin")) {
				UndisguiseEvent undisEvent = new UndisguiseEvent(p, DisguiseManager.getDisguise(p).clone(), false);
				plugin.getServer().getPluginManager().callEvent(undisEvent);
				if(!undisEvent.isCancelled()) {
					DisguiseManager.undisguiseToAll(p);
					p.sendMessage(ChatColor.GREEN + plugin.lang.getString("listener.worldchange.un"));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if(DisguiseManager.isDisguised(p)) {
			if(DisguiseManager.getDisguise(p) instanceof PlayerDisguise) {
				PlayerDisguise pd = (PlayerDisguise)DisguiseManager.getDisguise(p);
				event.setDeathMessage(event.getDeathMessage().replace(p.getName(), pd.getName()));
			} else {
				Disguise d = DisguiseManager.getDisguise(p);
				event.setDeathMessage(null);
				SoundSystem.playDeathSound(p, d.getType());
			}
		}
	}
	
	/*@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		Player p = event.getPlayer();
		if(DisguiseManager.getDisguise(p) instanceof MobDisguise) {
			event.setCancelled(true);
		}
	}*/
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if(!event.isCancelled()) {
			if(DisguiseManager.getDisguise(p) instanceof MobDisguise) {
				SoundSystem.playIdleSound(p, DisguiseManager.getDisguise(p).getType());
			}
		}
	}
	
}