package de.robingrether.idisguise;

import java.beans.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.util.ObjectUtil;

import static de.robingrether.idisguise.Reflection.*;

public class iDisguise extends JavaPlugin implements Listener {
	
	public static final Pattern INT_VAL = Pattern.compile("[+-]?[0-9]+");
	public static final Pattern DOUBLE_VAL = Pattern.compile("[+-]?[0-9]*\\.[0-9]+");
	public static final Pattern ENUM_VAL = Pattern.compile("([A-Za-z0-9]+)\\.([A-Za-z0-9_]+)");
	public static final Pattern STRING_VAL = Pattern.compile("\".*\"");
	
	private static iDisguise INSTANCE;
	
	private boolean debugMode = false;
	private Map<UUID, Entity> disguiseMap = new HashMap<>();
	
	public iDisguise() { INSTANCE = this; }
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		for(Entity entity : disguiseMap.values()) {
			entity.remove();
		}
		disguiseMap.clear();
	}
	
	/*
	 * My own function to reload language and configuration stuff.
	 */
	public void onReload() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("I'm sorry. This command is for players only.");
			return true;
		}
		if(command.getName().equalsIgnoreCase("disguise")) {
			if(args.length == 0) {
				sender.sendMessage("Please feed me a disguise type. I'm bad at guessing.");
			} else if(args[0].equalsIgnoreCase("player")) {
				if(args.length == 1) {
					sender.sendMessage("Please feed me a player name as well.");
				} else {
					try {
						sender.sendMessage("Currently not supported!");
					} catch(IllegalArgumentException e) {
						sender.sendMessage("I'm sorry. This player name is invalid.");
					}
				}
			} else {
				try {
					EntityType type = EntityType.valueOf(args[0].toUpperCase(Locale.ENGLISH).replace('-', '_'));
					Entity entity = disguise((Player)sender, type);
					for(int i = 1; i < args.length; i++) {
						String codeLine = args[i];
						String[] codeFrags = codeLine.split("[()]", -1);
						Statement statement = null;
						if(codeFrags[1].length() == 0) {
							statement = new Statement(entity, codeFrags[0], new Object[0]);
						} else if(codeFrags[1].equals("true")) {
							statement = new Statement(entity, codeFrags[0], new Object[] {true});
						} else if(codeFrags[1].equals("false")) {
							statement = new Statement(entity, codeFrags[0], new Object[] {false});
						} else if(INT_VAL.matcher(codeFrags[1]).matches()) {
							statement = new Statement(entity, codeFrags[0], new Object[] {Integer.valueOf(codeFrags[1])});
						} else if(DOUBLE_VAL.matcher(codeFrags[1]).matches()) {
							statement = new Statement(entity, codeFrags[0], new Object[] {Double.valueOf(codeFrags[1])});
						} else if(ENUM_VAL.matcher(codeFrags[1]).matches()) {
							Matcher m = ENUM_VAL.matcher(codeFrags[1]);
							// TODO
							statement = new Statement(entity, codeFrags[0], new Object[] {null});
						} else if(STRING_VAL.matcher(codeFrags[1]).matches()) {
							statement = new Statement(entity, codeFrags[0], new Object[] {codeFrags[1].substring(1, codeFrags[1].length() - 1)});
						}
						if(statement != null) {
							try {
								statement.execute();
							} catch (Exception e) {
								sender.sendMessage("Something went wrong with your additional statement!");
								e.printStackTrace();
							}
						}
					}
					sender.sendMessage("Disguised successfully!");
				} catch(IllegalArgumentException e) {
					sender.sendMessage("I'm sorry. I do not know this disguise type.");
				}
			}
		} else if(command.getName().equalsIgnoreCase("undisguise")) {
			if(isDisguised((Player)sender)) {
				undisguise((Player)sender);
				sender.sendMessage("Undisguised successfully!");
			} else {
				sender.sendMessage("I'm sorry. You are not even disguised.");
			}
		}
		return true;
	}
	
	public synchronized EntityType getDisguise(Player player) {
		return disguiseMap.get(player.getUniqueId()).getType();
	}
	
	public synchronized boolean isDisguised(Player player) {
		return disguiseMap.containsKey(player.getUniqueId());
	}
	
	public synchronized Entity disguise(Player player, EntityType entityType, String... args) {
		if(isDisguised(player)) {
			undisguise(player);
		}
		Entity entity = player.getWorld().spawnEntity(player.getLocation(), entityType);
		entity.setMetadata("iDisguise", new FixedMetadataValue(this, player.getUniqueId()));
		if(entity instanceof LivingEntity) {
			((LivingEntity)entity).setAI(false);
			Bukkit.getScheduler().runTaskLater(this, () -> {
				try {
					EntityTrackerEntry_clear.invoke(IntHashMap_get.invoke(EntityTracker_trackedEntities.get(WorldServer_entityTracker.get(Entity_world.get(CraftLivingEntity_getHandle.invoke(entity)))), entity.getEntityId()), CraftPlayer_getHandle.invoke(player));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}, 10L);
			//((LivingEntity)entity).setCollidable(false);
			//player.setCollidable(false);
		}
		disguiseMap.put(player.getUniqueId(), entity);
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer != player) {
				observer.hidePlayer(this, player);
			}
		}
		return entity;
	}
	
	public synchronized EntityType undisguise(Player player) {
		if(!isDisguised(player)) return null;
		
		Entity entity = disguiseMap.remove(player.getUniqueId());
		entity.remove();
		for(Player observer : Bukkit.getOnlinePlayers()) {
			if(observer != player) {
				observer.showPlayer(this, player);
			}
		}
		return entity.getType();
	}
	
	@EventHandler
	public void handlePlayerJoin(PlayerJoinEvent event) {
		for(UUID uid : disguiseMap.keySet()) {
			event.getPlayer().hidePlayer(this, Bukkit.getPlayer(uid));
		}
	}
	
	@EventHandler
	public void handlePlayerQuit(PlayerQuitEvent event) {
		if(isDisguised(event.getPlayer())) {
			undisguise(event.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void handlePlayerMove(PlayerMoveEvent event) {
		if(!event.isCancelled() && isDisguised(event.getPlayer())) {
			disguiseMap.get(event.getPlayer().getUniqueId()).teleport(event.getTo());
		}
	}
	
	@EventHandler
	public void handleEntityDamage(EntityDamageEvent event) {
		if(event.getEntity().hasMetadata("iDisguise")) {
			if(ObjectUtil.equals(event.getCause(), DamageCause.FLY_INTO_WALL, DamageCause.SUFFOCATION)) {
				event.setCancelled(true);
			} else {
				Bukkit.getPlayer((UUID)event.getEntity().getMetadata("iDisguise").get(0).value()).damage(event.getDamage());
				event.setDamage(Double.MIN_VALUE);
			}
		}
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getNameAndVersion() {
		return getName() + " " + getVersion();
	}
	
	public boolean debugMode() {
		return debugMode;
	}
	
	public static iDisguise getInstance() {
		return INSTANCE;
	}
	
}
