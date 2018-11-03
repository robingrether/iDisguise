package de.robingrether.idisguise;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.EntityDisguiseEvent;
import de.robingrether.idisguise.api.EntityUndisguiseEvent;
import de.robingrether.idisguise.api.OfflinePlayerDisguiseEvent;
import de.robingrether.idisguise.api.OfflinePlayerUndisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.AgeableDisguise;
import de.robingrether.idisguise.disguise.CreeperDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.Disguise.Visibility;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.EndermanDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.SheepDisguise;
import de.robingrether.idisguise.disguise.OcelotDisguise;
import de.robingrether.idisguise.disguise.ParrotDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.RabbitDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.VillagerDisguise;
import de.robingrether.idisguise.disguise.WolfDisguise;
import de.robingrether.idisguise.io.Configuration;
import de.robingrether.idisguise.io.Language;
import de.robingrether.idisguise.io.SLAPI;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.ProfileHelper;
import de.robingrether.idisguise.management.Sounds;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.idisguise.management.channel.ChannelInjector;
import de.robingrether.idisguise.management.hooks.ScoreboardHooks;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.StringUtil;
import de.robingrether.util.Validate;

public class iDisguise extends JavaPlugin {
	
	private static iDisguise instance;
	
	private EventListener listener;
	private Configuration configuration;
	private Language language;
	private CommandExecutor cmdExecutor;
	private Metrics metrics;
	private boolean enabled = false;
	
	public iDisguise() { instance = this; }
	
	public void onEnable() {
		boolean debugMode = checkDirectory();
		if(!VersionHelper.init(debugMode)) {
			getLogger().log(Level.SEVERE, String.format("%s is not compatible with your server version!", getFullName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(debugMode) {
			getLogger().log(Level.INFO, "Debug mode is enabled!");
		}
		listener = new EventListener(this);
		configuration = new Configuration(this);
		language = new Language(this);
		loadConfigFiles();
		cmdExecutor = new CommandExecutor(this);
		
		/* Metrics start */
		metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SingleLineChart("disguisedPlayers", () -> DisguiseManager.getNumberOfDisguisedPlayers()));
		metrics.addCustomChart(new Metrics.SimplePie("storageType", () -> configuration.KEEP_DISGUISE_SHUTDOWN ? "file" : "none"));
		metrics.addCustomChart(new Metrics.SimplePie("updateChecking", () -> configuration.UPDATE_CHECK ? configuration.UPDATE_DOWNLOAD ? "check and download" : "check only" : "disabled"));
		metrics.addCustomChart(new Metrics.SimplePie("realisticSoundEffects", () -> configuration.REPLACE_SOUND_EFFECTS ? "enabled" : "disabled"));
		metrics.addCustomChart(new Metrics.SimplePie("undisguisePermission", () -> configuration.UNDISGUISE_PERMISSION ? "enabled" : "disabled"));
		metrics.addCustomChart(new Metrics.SimplePie("viewableDisguises", () -> configuration.DISGUISE_VIEW_SELF ? "enabled" : "disabled"));
		metrics.addCustomChart(new Metrics.SimplePie("channelInjector", () -> ChannelInjector.getImplementationName()));
		metrics.addCustomChart(new Metrics.SimplePie("ghostDisguise", () -> "unavailable"));
		/* Metrics end */
		
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			loadDisguises();
		}
		getServer().getPluginManager().registerEvents(listener, this);
		for(String command : new String[] {"disguise", "odisguise", "undisguise"}) {
			getServer().getPluginCommand(command).setExecutor(cmdExecutor);
			getServer().getPluginCommand(command).setTabCompleter(cmdExecutor);
		}
		getServer().getServicesManager().register(DisguiseAPI.class, getAPI(), this, ServicePriority.Normal);
		if(configuration.UPDATE_CHECK) {
			getServer().getScheduler().runTaskLaterAsynchronously(this, new UpdateCheck(this, getServer().getConsoleSender(), configuration.UPDATE_DOWNLOAD), 20L);
		}
		Calendar today = Calendar.getInstance();
		if(today.get(Calendar.MONTH) == Calendar.NOVEMBER && today.get(Calendar.DAY_OF_MONTH) == 6) {
			getLogger().log(Level.INFO, String.format("YAAAY!!! Today is my birthday! I'm %s years old now.",  today.get(Calendar.YEAR) - 2012));
		}
		getLogger().log(Level.INFO, String.format("%s enabled!", getFullName()));
		enabled = true;
		for(Player player : Bukkit.getOnlinePlayers()) {
			ProfileHelper.getInstance().registerGameProfile(player);
		}
		for(DisguiseType type : DisguiseType.values()) {
			if(type.getMHFSkin() != null) ProfileHelper.getInstance().loadGameProfileAsynchronously(type.getMHFSkin());
		}
		ChannelInjector.injectOnlinePlayers();
		DisguiseManager.resendPackets();
		if(getServer().getPluginManager().getPlugin("iDisguiseAdditions") != null) {
			int version = Integer.parseInt(getServer().getPluginManager().getPlugin("iDisguiseAdditions").getDescription().getVersion().replace("-SNAPSHOT", "").replace(".", ""));
			if(version < 13) {
				getLogger().log(Level.SEVERE, "You use an outdated version of iDisguiseAdditions! Please update to the latest version otherwise the plugin won't work properly.");
			}
		}
		if(getServer().getPluginManager().getPlugin("iDisguiseWG") != null) {
			int version = Integer.parseInt(getServer().getPluginManager().getPlugin("iDisguiseWG").getDescription().getVersion().replace("-SNAPSHOT", "").replace(".", ""));
			if(version < 12) {
				getLogger().log(Level.SEVERE, "You use an outdated version of iDisguiseWG! Please update to the latest version otherwise the plugin won't work properly.");
			}
		}
	}
	
	public void onDisable() {
		if(!enabled) {
			return;
		}
		getServer().getScheduler().cancelTasks(this);
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			saveDisguises();
		}
		ChannelInjector.removeOnlinePlayers();
		ChannelInjector.terminate();
		getLogger().log(Level.INFO, String.format("%s disabled!", getFullName()));
		enabled = false;
	}
	
	public void onReload() {
		if(!enabled) {
			return;
		}
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			saveDisguises();
		}
		enabled = false;
		loadConfigFiles();
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			loadDisguises();
		}
		enabled = true;
		DisguiseManager.resendPackets();
	}
	
	boolean disguise(Object target, Disguise disguise, boolean fireEvent) {
		Validate.notNull(disguise);
		if(target instanceof OfflinePlayer) {
			OfflinePlayer offlinePlayer = (OfflinePlayer)target;
			if(offlinePlayer.isOnline()) {
				Player player = offlinePlayer.getPlayer();
				if(fireEvent) {
					DisguiseEvent event = new DisguiseEvent(player, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						return false;
					} else {
						DisguiseManager.disguise(player, disguise);
						return true;
					}
				} else {
					DisguiseManager.disguise(player, disguise);
					return true;
				}
			} else {
				if(fireEvent) {
					OfflinePlayerDisguiseEvent event = new OfflinePlayerDisguiseEvent(offlinePlayer, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						return false;
					} else {
						DisguiseManager.disguise(offlinePlayer, disguise);
						return true;
					}
				} else {
					DisguiseManager.disguise(offlinePlayer, disguise);
					return true;
				}
			}
		} else if(target instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)target;
			if(fireEvent) {
				EntityDisguiseEvent event = new EntityDisguiseEvent(livingEntity, disguise);
				getServer().getPluginManager().callEvent(event);
				if(event.isCancelled()) {
					return false;
				} else {
					DisguiseManager.disguise(livingEntity, disguise);
					return true;
				}
			} else {
				DisguiseManager.disguise(livingEntity, disguise);
				return true;
			}
		}
		return false;
	}
	
	boolean undisguise(Object target, boolean fireEvent) {
		if(target instanceof OfflinePlayer) {
			OfflinePlayer offlinePlayer = (OfflinePlayer)target;
			if(offlinePlayer.isOnline()) {
				Player player = offlinePlayer.getPlayer();
				if(!DisguiseManager.isDisguised(player)) return false;
				
				if(fireEvent) {
					UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.getDisguise(player), false);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						return false;
					} else {
						DisguiseManager.undisguise(player);
						return true;
					}
				} else {
					DisguiseManager.undisguise(player);
					return true;
				}
			} else {
				if(!DisguiseManager.isDisguised(offlinePlayer)) return false;
				
				if(fireEvent) {
					OfflinePlayerUndisguiseEvent event = new OfflinePlayerUndisguiseEvent(offlinePlayer, DisguiseManager.getDisguise(offlinePlayer), false);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						return false;
					} else {
						DisguiseManager.undisguise(offlinePlayer);
						return true;
					}
				} else {
					DisguiseManager.undisguise(offlinePlayer);
					return true;
				}
			}
		} else if(target instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)target;
			if(!DisguiseManager.isDisguised(livingEntity)) return false;
			
			if(fireEvent) {
				EntityUndisguiseEvent event = new EntityUndisguiseEvent(livingEntity, DisguiseManager.getDisguise(livingEntity), false);
				getServer().getPluginManager().callEvent(event);
				if(event.isCancelled()) {
					return false;
				} else {
					DisguiseManager.undisguise(livingEntity);
					return true;
				}
			} else {
				DisguiseManager.undisguise(livingEntity);
				return true;
			}
		}
		return false;
	}
	
	boolean hasPermission(CommandSender sender, DisguiseType type) {
		if(type.isMob()) return sender.hasPermission("iDisguise.mob." + type.name().toLowerCase(Locale.ENGLISH));
		if(type.isObject()) return sender.hasPermission("iDisguise.object." + type.name().toLowerCase(Locale.ENGLISH));
		return false;
	}
	
	boolean hasPermission(CommandSender sender, Disguise disguise) {
		if(ObjectUtil.equals(disguise.getVisibility(), Visibility.ONLY_LIST, Visibility.NOT_LIST) && !sender.hasPermission("iDisguise.visibility.list")) return false;
		if(ObjectUtil.equals(disguise.getVisibility(), Visibility.ONLY_PERMISSION, Visibility.NOT_PERMISSION) && !sender.hasPermission("iDisguise.visibility.permission")) return false;
		if(disguise instanceof PlayerDisguise) {
			PlayerDisguise playerDisguise = (PlayerDisguise)disguise;
			return (sender.hasPermission("iDisguise.player.name.*") || sender.hasPermission("iDisguise.player.name." + playerDisguise.getSkinName())) && (isPlayerDisguisePermitted(playerDisguise.getSkinName()) || sender.hasPermission("iDisguise.player.prohibited")) && (playerDisguise.getSkinName().equalsIgnoreCase(playerDisguise.getDisplayName()) || sender.hasPermission("iDisguise.player.display-name"));
		} else if(hasPermission(sender, disguise.getType())) {
			if(disguise instanceof MobDisguise) {
				MobDisguise mobDisguise = (MobDisguise)disguise;
				if(mobDisguise.getCustomName() != null && !mobDisguise.getCustomName().isEmpty() && !sender.hasPermission("iDisguise.mob.custom-name")) return false;
				if(disguise instanceof AgeableDisguise) {
					if(((AgeableDisguise)disguise).isAdult() || sender.hasPermission("iDisguise.mob.baby")) {
						switch(disguise.getType()) {
							case OCELOT:
								return sender.hasPermission("iDisguise.mob.ocelot.type." + ((OcelotDisguise)disguise).getCatType().name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""));
							case PIG:
								return !((PigDisguise)disguise).isSaddled() || sender.hasPermission("iDisguise.mob.pig.saddled");
							case RABBIT:
								return sender.hasPermission("iDisguise.mob.rabbit.type." + ((RabbitDisguise)disguise).getRabbitType().name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer"));
							case SHEEP:
								return sender.hasPermission("iDisguise.mob.sheep.color." + ((SheepDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
							case VILLAGER: 
								return sender.hasPermission("iDisguise.mob.villager.profession." + ((VillagerDisguise)disguise).getProfession().name().toLowerCase(Locale.ENGLISH));
							case WOLF:
								return sender.hasPermission("iDisguise.mob.wolf.collar." + ((WolfDisguise)disguise).getCollarColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-')) && (!((WolfDisguise)disguise).isTamed() || sender.hasPermission("iDisguise.mob.wolf.tamed")) && (!((WolfDisguise)disguise).isAngry() || sender.hasPermission("iDisguise.mob.wolf.angry"));
							default:
								return true;
						}
					}
				} else {
					switch(disguise.getType()) {
						case CREEPER:
							return (!((CreeperDisguise)disguise).isPowered() || sender.hasPermission("iDisguise.mob.creeper.powered"));
						case ENDERMAN:
							return (((EndermanDisguise)disguise).getCarriedBlock().equals(Material.AIR) || sender.hasPermission("iDisguise.mob.enderman.block"));
						case MAGMA_CUBE:
							return (((SizedDisguise)disguise).getSize() < 5 || sender.hasPermission("iDisguise.mob.magma_cube.giant"));
						case PARROT:
							return sender.hasPermission("iDisguise.mob.parrot.variant." + ((ParrotDisguise)disguise).getVariant().name().toLowerCase(Locale.ENGLISH));
						case PHANTOM:
							return (((SizedDisguise)disguise).getSize() < 1 || sender.hasPermission("iDisguise.mob.phantom.giant"));
						case SLIME:
							return (((SizedDisguise)disguise).getSize() < 5 || sender.hasPermission("iDisguise.mob.slime.giant"));
						default:
							return true;
					}
				}
			} else if(disguise instanceof ObjectDisguise) {
				ObjectDisguise objectDisguise = (ObjectDisguise)disguise;
				if(objectDisguise.getCustomName() != null && !objectDisguise.getCustomName().isEmpty() && !sender.hasPermission("iDisguise.object.custom-name")) return false;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the plugin directory (= data folder) exists and creates such a directory if not.
	 * 
	 * @return <code>true</code> if a file named 'debug' exists in that directory (which means that debug mode shall be enabled), <code>false</code> otherwise
	 */
	private boolean checkDirectory() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		return new File(getDataFolder(), "debug").isFile();
	}
	
	private void loadConfigFiles() {
		// reload config
		configuration.loadData();
		configuration.saveData();
		
		// reload language file
		language.loadData();
		language.saveData();
		
		// reset config values
		PacketHandler.showOriginalPlayerName = configuration.NAME_TAG_SHOWN;
		PacketHandler.modifyPlayerListEntry = configuration.MODIFY_PLAYER_LIST_ENTRY;
		DisguiseManager.modifyScoreboardPackets = PacketHandler.modifyScoreboardPackets = configuration.MODIFY_SCOREBOARD_PACKETS;
		PacketHandler.replaceSoundEffects = configuration.REPLACE_SOUND_EFFECTS;
		PacketHandler.bungeeCord = configuration.BUNGEE_CORD;
		DisguiseManager.disguiseViewSelf = PacketHandler.disguiseViewSelf = configuration.DISGUISE_VIEW_SELF && !configuration.BUNGEE_CORD; // view-self does not work via Bungee
		
		// setup hooks
		if(configuration.MODIFY_SCOREBOARD_PACKETS) {
			ScoreboardHooks.setup();
		}
		
		// load disguise aliases
		try {
			for(DisguiseType type : DisguiseType.values()) {
				if(!type.isPlayer()) {
					String value = (String)Language.class.getDeclaredField("DISGUISE_ALIAS_" + type.name()).get(language);
					if(StringUtil.isNotBlank(value)) {
						String[] aliases = value.split("\\s*,\\s*");
						for(String alias : aliases) {
							if(StringUtil.isNotBlank(alias) && alias.matches("!?[A-Za-z0-9-_]+")) {
								if(alias.startsWith("!"))
									type.setCustomCommandArgument(alias.substring(1));
								else
									type.addCustomCommandArgument(alias);
							}
						}
					}
				}
			}
		} catch(Exception e) { // fail silently
		}
	}
	
	private void loadDisguises() {
		File dataFile = new File(getDataFolder(), "disguises.dat");
		if(dataFile.exists()) {
			DisguiseManager.updateDisguises(SLAPI.loadMap(dataFile));
		}
	}
	
	private void saveDisguises() {
		File dataFile = new File(getDataFolder(), "disguises.dat");
		SLAPI.saveMap(DisguiseManager.getDisguises(), dataFile);
	}
	
	public DisguiseAPI getAPI() {
		return new DisguiseAPI() {
			
			public boolean disguise(OfflinePlayer offlinePlayer, Disguise disguise) {
				return disguise(offlinePlayer, disguise, true);
			}
			
			public boolean disguise(Player player, Disguise disguise) {
				return disguise(player, disguise, true);
			}
			
			public boolean disguise(LivingEntity livingEntity, Disguise disguise) {
				return disguise(livingEntity, disguise, true);
			}
			
			public boolean disguise(OfflinePlayer offlinePlayer, Disguise disguise, boolean fireEvent) {
				return iDisguise.this.disguise(offlinePlayer, disguise, fireEvent);
			}
			
			public boolean disguise(Player player, Disguise disguise, boolean fireEvent) {
				return iDisguise.this.disguise(player, disguise, fireEvent);
			}
			
			public boolean disguise(LivingEntity livingEntity, Disguise disguise, boolean fireEvent) {
				return iDisguise.this.disguise(livingEntity, disguise, fireEvent);
			}
			
			public boolean undisguise(OfflinePlayer offlinePlayer) {
				return undisguise(offlinePlayer, true);
			}
			
			public boolean undisguise(Player player) {
				return undisguise(player, true);
			}
			
			public boolean undisguise(LivingEntity livingEntity) {
				return undisguise(livingEntity, true);
			}
			
			public boolean undisguise(OfflinePlayer offlinePlayer, boolean fireEvent) {
				return iDisguise.this.undisguise(offlinePlayer, fireEvent);
			}
			
			public boolean undisguise(Player player, boolean fireEvent) {
				return iDisguise.this.undisguise(player, fireEvent);
			}
			
			public boolean undisguise(LivingEntity livingEntity, boolean fireEvent) {
				return iDisguise.this.undisguise(livingEntity, fireEvent);
			}
			
			public void undisguiseAll() {
				DisguiseManager.undisguiseAll();
			}
			
			public boolean isDisguised(OfflinePlayer offlinePlayer) {
				return DisguiseManager.isDisguised(offlinePlayer);
			}
			
			public boolean isDisguised(Player player) {
				return DisguiseManager.isDisguised(player);
			}
			
			public boolean isDisguised(LivingEntity livingEntity) {
				return DisguiseManager.isDisguised(livingEntity);
			}
			
			public boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer) {
				return DisguiseManager.isDisguisedTo(offlinePlayer, observer);
			}
			
			public boolean isDisguisedTo(Player player, Player observer) {
				return DisguiseManager.isDisguisedTo(player, observer);
			}
			
			public boolean isDisguisedTo(LivingEntity livingEntity, Player observer) {
				return DisguiseManager.isDisguisedTo(livingEntity, observer);
			}
			
			public Disguise getDisguise(OfflinePlayer offlinePlayer) {
				return DisguiseManager.isDisguised(offlinePlayer) ? DisguiseManager.getDisguise(offlinePlayer).clone() : null;
			}
			
			public Disguise getDisguise(Player player) {
				return DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
			}
			
			public Disguise getDisguise(LivingEntity livingEntity) {
				return DisguiseManager.isDisguised(livingEntity) ? DisguiseManager.getDisguise(livingEntity).clone() : null;
			}
			
			public int getNumberOfDisguisedPlayers() {
				return DisguiseManager.getNumberOfDisguisedPlayers();
			}
			
			public Sounds getSoundsForEntity(DisguiseType type) {
				return Sounds.getSoundsForEntity(type);
			}
			
			public boolean setSoundsForEntity(DisguiseType type, Sounds sounds) {
				return Sounds.setSoundsForEntity(type, sounds);
			}
			
			public boolean isSoundsEnabled() {
				return PacketHandler.replaceSoundEffects;
			}
			
			public void setSoundsEnabled(boolean enabled) {
				PacketHandler.replaceSoundEffects = enabled;
			}
			
			public boolean hasPermission(Player player, DisguiseType type) {
				return iDisguise.this.hasPermission(player, type);
			}
			
			public boolean hasPermission(Player player, Disguise disguise) {
				return iDisguise.this.hasPermission(player, disguise);
			}
			
			public boolean canSeeThrough(OfflinePlayer player) {
				return DisguiseManager.canSeeThrough(player);
			}
			
			public void setSeeThrough(OfflinePlayer player, boolean seeThrough) {
				DisguiseManager.setSeeThrough(player, seeThrough);
			}
			
			public Set<Object> getDisguisedEntities() {
				return DisguiseManager.getDisguisedEntities();
			}
			
		};
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "iDisguise " + getVersion();
	}
	
	public File getPluginFile() {
		return getFile();
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public boolean isPlayerDisguisePermitted(String name) {
		return !getConfiguration().RESTRICTED_PLAYER_NAMES.contains(name);
	}
	
	public boolean enabled() {
		return enabled;
	}
	
	public static iDisguise getInstance() {
		return instance;
	}
	
}