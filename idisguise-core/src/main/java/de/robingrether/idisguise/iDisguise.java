package de.robingrether.idisguise;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;

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
import de.robingrether.idisguise.disguise.DisguiseType.Type;
import de.robingrether.idisguise.disguise.EndermanDisguise;
import de.robingrether.idisguise.disguise.FallingBlockDisguise;
import de.robingrether.idisguise.disguise.ItemDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.SheepDisguise;
import de.robingrether.idisguise.disguise.OcelotDisguise;
import de.robingrether.idisguise.disguise.OutdatedServerException;
import de.robingrether.idisguise.disguise.ParrotDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.RabbitDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.Subtypes;
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
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.idisguise.management.util.VanillaTargetParser;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.RandomUtil;
import de.robingrether.util.StringUtil;
import de.robingrether.util.Validate;

public class iDisguise extends JavaPlugin {
	
	private static iDisguise instance;
	
	private EventListener listener;
	private Configuration configuration;
	private Language language;
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
		metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SingleLineChart("disguisedPlayers") {
			
			public int getValue() {
				return DisguiseManager.getNumberOfDisguisedPlayers();
			}
			
		});
		metrics.addCustomChart(new Metrics.SimplePie("storageType") {
			
			public String getValue() {
				return configuration.KEEP_DISGUISE_SHUTDOWN ? "file" : "none";
			}
			
		});
		metrics.addCustomChart(new Metrics.SimplePie("updateChecking") {
			
			public String getValue() {
				return configuration.UPDATE_CHECK ? configuration.UPDATE_DOWNLOAD ? "check and download" : "check only" : "disabled";
			}
			
		});
		metrics.addCustomChart(new Metrics.SimplePie("realisticSoundEffects") {
			
			public String getValue() {
				return configuration.REPLACE_SOUND_EFFECTS ? "enabled" : "disabled";
			}
			
		});
		metrics.addCustomChart(new Metrics.SimplePie("undisguisePermission") {
			
			public String getValue() {
				return configuration.UNDISGUISE_PERMISSION ? "enabled" : "disabled";
			}
			
		});
		metrics.addCustomChart(new Metrics.SimplePie("ghostDisguise") {
			
			public String getValue() {
				return "unavailable";
			}
			
		});
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			loadDisguises();
		}
		getServer().getPluginManager().registerEvents(listener, this);
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
//			EntityIdList.addPlayer(player);
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
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(StringUtil.equalsIgnoreCase(command.getName(), "disguise", "odisguise")) {
			if(args.length == 0) {
				sendHelpMessage(sender, command, alias, 1);
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("iDisguise.reload")) {
					onReload();
					sender.sendMessage(language.RELOAD_COMPLETE);
				} else {
					sender.sendMessage(language.NO_PERMISSION);
				}
			} else {
				Collection<? extends Object> targets = null;
				boolean disguiseSelf;
				if(command.getName().equalsIgnoreCase("disguise")) {
					if(sender instanceof Player) {
						targets = Arrays.asList((Player)sender);
						disguiseSelf = true;
					} else {
						sender.sendMessage(language.CONSOLE_USE_OTHER_COMMAND);
						return true;
					}
				} else if(args.length > 1) {
					if(sender.hasPermission("iDisguise.others")) {
						targets = parseTargets(args[0], sender);
						if(targets.size() == 0) {
							sender.sendMessage(language.CANNOT_FIND_TARGETS);
							return true;
						}
						disguiseSelf = false;
						args = Arrays.copyOfRange(args, 1, args.length);
					} else {
						sender.sendMessage(language.NO_PERMISSION);
						return true;
					}
				} else {
					sendHelpMessage(sender, command, alias, 1);
					return true;
				}
				if(args[0].equalsIgnoreCase("help")) {
					int pageNumber = 1;
					if(args.length > 1) {
						try {
							pageNumber = Integer.parseInt(args[1]);
						} catch(NumberFormatException e) { // fail silently
						}
					}
					sendHelpMessage(sender, command, alias, pageNumber);
				} else if(StringUtil.equalsIgnoreCase(args[0], "status", "state", "stats")) {
					for(Object target : targets) {
						if(target instanceof OfflinePlayer) {
							OfflinePlayer player = (OfflinePlayer)target;
							if(DisguiseManager.isDisguised(player)) {
								if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
									PlayerDisguise disguise = (PlayerDisguise)DisguiseManager.getDisguise(player);
									sender.sendMessage((disguiseSelf ? language.STATUS_PLAYER_SELF : language.STATUS_PLAYER_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().getCustomCommandArgument()).replace("%name%", disguise.getDisplayName()));
									sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								} else {
									Disguise disguise = DisguiseManager.getDisguise(player);
									sender.sendMessage((disguiseSelf ? language.STATUS_SELF : language.STATUS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().getCustomCommandArgument()));
									sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								}
							} else {
								sender.sendMessage((disguiseSelf ? language.STATUS_NOT_DISGUISED_SELF : language.STATUS_NOT_DISGUISED_OTHER).replace("%player%", player.getName()));
							}
						} else {
							LivingEntity livingEntity = (LivingEntity)target;
							if(DisguiseManager.isDisguised(livingEntity)) {
								if(DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise) {
									PlayerDisguise disguise = (PlayerDisguise)DisguiseManager.getDisguise(livingEntity);
									sender.sendMessage(language.STATUS_PLAYER_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()).replace("%name%", disguise.getDisplayName()));
									sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								} else {
									Disguise disguise = DisguiseManager.getDisguise(livingEntity);
									sender.sendMessage(language.STATUS_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()));
									sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								}
							} else {
								sender.sendMessage(language.STATUS_NOT_DISGUISED_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]"));
							}
						}
					}
				} else if(StringUtil.equalsIgnoreCase(args[0], "seethrough", "see-through")) {
					for(Object target : targets) {
						if(sender.hasPermission("iDisguise.see-through")) {
							if(target instanceof OfflinePlayer) {
								OfflinePlayer player = (OfflinePlayer)target;
								if(args.length < 2) {
									sender.sendMessage((DisguiseManager.canSeeThrough(player) ? disguiseSelf ? language.SEE_THROUGH_STATUS_ON_SELF : language.SEE_THROUGH_STATUS_ON_OTHER : disguiseSelf ? language.SEE_THROUGH_STATUS_OFF_SELF : language.SEE_THROUGH_STATUS_OFF_OTHER).replace("%player%", player.getName()));
								} else if(StringUtil.equalsIgnoreCase(args[1], "on", "off")) {
									boolean seeThrough = args[1].equalsIgnoreCase("on");
									DisguiseManager.setSeeThrough(player, seeThrough);
									sender.sendMessage((seeThrough ? disguiseSelf ? language.SEE_THROUGH_ENABLE_SELF : language.SEE_THROUGH_ENABLE_OTHER : disguiseSelf ? language.SEE_THROUGH_DISABLE_SELF : language.SEE_THROUGH_DISABLE_OTHER).replace("%player%", player.getName()));
								} else {
									sender.sendMessage(language.WRONG_USAGE_SEE_THROUGH.replace("%argument%", args[1]));
								}
							} else {
								sender.sendMessage(language.SEE_THROUGH_ENTITY);
							}
						} else {
							sender.sendMessage(language.NO_PERMISSION);
						}
					}
				} else {
					Disguise disguise = null;
					if(StringUtil.equalsIgnoreCase(args[0], "player", "p")) {
						if(args.length < 2) {
							sender.sendMessage(language.WRONG_USAGE_NO_NAME);
							return true;
						} else {
							String skinName = args.length == 2 ? args[1].replaceAll("&[0-9a-fk-or]", "") : args[1];
							String displayName = args.length == 2 ? ChatColor.translateAlternateColorCodes('&', args[1]) : ChatColor.translateAlternateColorCodes('&', args[2].replace("\\s", " "));
							if(!Validate.minecraftUsername(skinName)) {
								sender.sendMessage(language.INVALID_NAME);
								return true;
							} else {
								disguise = new PlayerDisguise(skinName, displayName);
							}
						}
					} else if(StringUtil.equalsIgnoreCase(args[0], "random")) {
						if(sender.hasPermission("iDisguise.random")) {
							disguise = DisguiseType.random(RandomUtil.nextBoolean() ? Type.MOB : Type.OBJECT).newInstance();
						} else {
							sender.sendMessage(language.NO_PERMISSION);
							return true;
						}
					} else {
						if(targets.size() == 1) {
							Object target = targets.iterator().next();
							if(target instanceof OfflinePlayer) {
								if(DisguiseManager.isDisguised((OfflinePlayer)target))
									disguise = DisguiseManager.getDisguise((OfflinePlayer)target).clone();
							} else if(target instanceof LivingEntity) {
								if(DisguiseManager.isDisguised((LivingEntity)target))
									disguise = DisguiseManager.getDisguise((LivingEntity)target).clone();
							}
						}
						boolean match = false;
						List<String> unknown_args = new ArrayList<String>(Arrays.asList(args));
						for(Iterator<String> iterator = unknown_args.iterator(); iterator.hasNext(); ) {
							DisguiseType type = DisguiseType.fromString(iterator.next());
							if(type != null) {
								if(match) {
									sender.sendMessage(language.WRONG_USAGE_TWO_DISGUISE_TYPES);
									return true;
								}
								try {
									disguise = type.newInstance();
									match = true;
									iterator.remove();
								} catch(OutdatedServerException e) {
									sender.sendMessage(language.OUTDATED_SERVER);
									return true;
	//							} catch(UnsupportedOperationException e) {
	//								sendHelpMessage(sender, command, alias);
	//								return true;
								}
							}
						}
						if(disguise != null) {
							for(Iterator<String> iterator = unknown_args.iterator(); iterator.hasNext(); ) {
								if(Subtypes.applySubtype(disguise, iterator.next())) {
									match = true;
									iterator.remove();
								}
							}
						}
						if(!unknown_args.isEmpty()) {
							sender.sendMessage(language.WRONG_USAGE_UNKNOWN_ARGUMENTS.replace("%arguments%", StringUtil.join(", ", unknown_args.toArray(new String[0]))));
						}
						if(!match) return true;
					}
					if(hasPermission(sender, disguise)) {
						int successes = 0;
						for(Object target : targets) {
							if(disguise(target, disguise, true)) successes++;
						}
						if(targets.size() == 1) {
							Object target = targets.iterator().next();
							if(successes == 1) {
								sender.sendMessage((disguiseSelf ? language.DISGUISE_SUCCESS_SELF : language.DISGUISE_SUCCESS_OTHER)
										.replace("%player%", target instanceof OfflinePlayer ? ((OfflinePlayer)target).getName() : ((LivingEntity)target).getType().name() + " [" + ((LivingEntity)target).getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()));
							} else {
								sender.sendMessage(language.EVENT_CANCELLED);
							}
						} else {
							sender.sendMessage(language.DISGUISE_SUCCESS_MULTIPLE.replace("%share%", Integer.toString(successes)).replace("%total%", Integer.toString(targets.size())).replace("%type%", disguise.getType().getCustomCommandArgument()));
						}
					} else {
						sender.sendMessage(language.NO_PERMISSION);
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("undisguise")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					if(DisguiseManager.isDisguised((Player)sender)) {
						if(!configuration.UNDISGUISE_PERMISSION || sender.hasPermission("iDisguise.undisguise")) {
							UndisguiseEvent event = new UndisguiseEvent((Player)sender, DisguiseManager.getDisguise((Player)sender), false);
							getServer().getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								sender.sendMessage(language.EVENT_CANCELLED);
							} else {
								DisguiseManager.undisguise((Player)sender);
								sender.sendMessage(language.UNDISGUISE_SUCCESS_SELF);
							}
						} else {
							sender.sendMessage(language.NO_PERMISSION);
						}
					} else {
						sender.sendMessage(language.UNDISGUISE_NOT_DISGUISED_SELF);
					}
				} else {
					sender.sendMessage(language.UNDISGUISE_CONSOLE);
				}
			} else {
				Collection<? extends Object> targets = null;
				if(args[0].startsWith("*")) {
					if(sender.hasPermission("iDisguise.undisguise.all")) {
						args[0] = args[0].toLowerCase(Locale.ENGLISH);
						if(args[0].matches("\\*[eop]?")) {
							boolean entities = true, online = true, offline = true;
							if(args[0].length() == 2) {
								switch(args[0].charAt(1)) {
									case 'e':
										online = offline = false;
										break;
									case 'o':
										offline = entities = false;
										break;
									case 'p':
										entities = false;
										break;
								}
							}
							final boolean ent = entities, on = online, off = offline;
							Set<Object> disguisedEntities = DisguiseManager.getDisguisedEntities();
							disguisedEntities.removeIf(new Predicate<Object>() {
								
								public boolean test(Object target) {
									if(target instanceof Player) {
										return !on;
									} else if(target instanceof OfflinePlayer) {
										return !off;
									} else if(target instanceof LivingEntity) {
										return !ent;
									}
									return true;
								}
								
							});
							targets = disguisedEntities;
						} else {
							sender.sendMessage(language.WRONG_USAGE_UNKNOWN_ARGUMENTS.replace("%arguments%", args[0]));
							return true;
						}
					} else {
						sender.sendMessage(language.NO_PERMISSION);
						return true;
					}
				} else {
					if(sender.hasPermission("iDisguise.undisguise.others")) {
						targets = parseTargets(args[0], sender);
						if(targets.size() == 0) {
							sender.sendMessage(language.CANNOT_FIND_TARGETS);
							return true;
						}
					} else {
						sender.sendMessage(language.NO_PERMISSION);
						return true;
					}
				}
				boolean fireEvent = args.length > 1 ? !args[1].equalsIgnoreCase("ignore") : true;
				int successes = 0;
				for(Object target : targets) {
					if(undisguise(target, fireEvent)) successes++;
				}
				sender.sendMessage(language.UNDISGUISE_SUCCESS_MULTIPLE.replace("%share%", Integer.toString(successes)).replace("%total%", Integer.toString(targets.size())));
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<String>();
		if(command.getName().equalsIgnoreCase("disguise")) {
			if(sender instanceof Player) {
				Player player = (Player)sender;
				if(args.length < 2) {
					completions.addAll(Arrays.asList("help", "player", "status"));
					if(sender.hasPermission("iDisguise.random")) {
						completions.add("random");
					}
					if(sender.hasPermission("iDisguise.reload")) {
						completions.add("reload");
					}
					if(sender.hasPermission("iDisguise.see-through")) {
						completions.add("see-through");
					}
					for(DisguiseType type : DisguiseType.values()) {
						if(type.isAvailable() && !type.isPlayer()) {
							completions.add(type.getCustomCommandArgument());
						}
					}
				}
				Disguise disguise = DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
				for(String argument : args) {
					DisguiseType type = DisguiseType.fromString(argument);
					if(type != null) {
						try {
							disguise = type.newInstance();
							break;
						} catch(OutdatedServerException e) {
						} catch(UnsupportedOperationException e) {
						}
					}
				}
				if(disguise != null) {
					completions.addAll(Subtypes.listSubtypeArguments(disguise, args.length > 0 ? args[args.length - 1].contains("=") : false));
				}
			} else {
				completions.add("reload");
			}
		} else if(command.getName().equalsIgnoreCase("odisguise")) {
			if(args.length < 2) {
				if(sender.hasPermission("iDisguise.reload")) {
					completions.add("reload");
				}
				if(sender.hasPermission("iDisguise.others")) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						completions.add("{" + player.getName() + "}");
					}
					if(sender instanceof Player) { // TODO: target suggestions
						for(Entity entity : ((Player)sender).getNearbyEntities(5.0, 5.0, 5.0)) {
							if(entity instanceof LivingEntity) {
								completions.add("[" + entity.getEntityId() + "]");
							}
						}
					}
				}
			} else if(sender.hasPermission("iDisguise.others")) {
				Collection<? extends Object> targets = parseTargets(args[0], sender);
				if(!targets.isEmpty()) {
					if(args.length < 3) {
						completions.addAll(Arrays.asList("help", "player", "status"));
						if(sender.hasPermission("iDisguise.random")) {
							completions.add("random");
						}
						if(sender.hasPermission("iDisguise.see-through") && ObjectUtil.instanceOf(OfflinePlayer.class, targets)) {
							completions.add("see-through");
						}
						for(DisguiseType type : DisguiseType.values()) {
							if(!type.isPlayer() && type.isAvailable() && hasPermission(sender, type.newInstance())) {
								completions.add(type.getCustomCommandArgument());
							}
						}
					}
					Disguise disguise = null;
					if(targets.size() == 1) {
						Object target = targets.iterator().next();
						if(target instanceof OfflinePlayer) {
							if(DisguiseManager.isDisguised((OfflinePlayer)target))
								disguise = DisguiseManager.getDisguise((OfflinePlayer)target).clone();
						} else if(target instanceof LivingEntity) {
							if(DisguiseManager.isDisguised((LivingEntity)target))
								disguise = DisguiseManager.getDisguise((LivingEntity)target).clone();
						}
					}
					for(String argument : args) {
						DisguiseType type = DisguiseType.fromString(argument);
						if(type != null) {
							try {
								disguise = type.newInstance();
								break;
							} catch(OutdatedServerException e) {
							} catch(UnsupportedOperationException e) {
							}
						}
					}
					if(disguise != null) {
						completions.addAll(Subtypes.listSubtypeArguments(disguise, args.length > 0 ? args[args.length - 1].contains("=") : false));
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("undisguise")) {
			if(args.length < 2) {
				if(sender.hasPermission("iDisguise.undisguise.all")) {
					completions.addAll(Arrays.asList("*", "*e", "*o", "*p"));
				}
				if(sender.hasPermission("iDisguise.undisguise.others")) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(DisguiseManager.isDisguised(player)) {
							completions.add("{" + player.getName() + "}");
						}
					}
					if(sender instanceof Player) {
						for(Entity entity : ((Player)sender).getNearbyEntities(5.0, 5.0, 5.0)) {
							if(entity instanceof LivingEntity && DisguiseManager.isDisguised((LivingEntity)entity)) {
								completions.add("[" + entity.getEntityId() + "]");
							}
						}
					}
				}
			} else {
				completions.add("ignore");
			}
		}
		if(args.length > 0) {
			for(int i = 0; i < completions.size(); i++) {
				if(!StringUtil.startsWithIgnoreCase(completions.get(i).replace("{", ""), args[args.length - 1].replace('_', '-'))) {
					completions.remove(i);
					i--;
				}
			}
		}
		return completions;
	}
	
	private Collection<? extends Object> parseTargets(String argument, CommandSender sender) {
		if(argument.charAt(0) == '@' || argument.charAt(0) == '#') {
			return VanillaTargetParser.getInstance().parseVanillaTargets('@' + argument.substring(1), sender);
		} else {
			List<Object> targets = new ArrayList<Object>();
			for(String arg : argument.split(",")) {
				Object target = null;
				if(arg.matches("<[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}>")) {
					target = Bukkit.getOfflinePlayer(UUID.fromString(argument.substring(1, 37)));
				} else if(argument.matches("\\[[0-9]+\\]")) {
					target = EntityIdList.getEntityByEntityId(Integer.parseInt(argument.substring(1, argument.length() - 1)));
				} else if(argument.matches("\\{[A-Za-z0-9_]{1,16}\\}")) {
					target = Bukkit.getOfflinePlayer(argument.substring(1, argument.length() - 1));
				} else if(Bukkit.getPlayerExact(argument) != null) {
					target = Bukkit.getPlayerExact(argument);
				} else if(Bukkit.matchPlayer(argument).size() == 1) {
					target = Bukkit.matchPlayer(argument).get(0);
				}
				if(target != null) targets.add(target);
			}
			return targets;
		}
	}
	
	private boolean disguise(Object target, Disguise disguise, boolean fireEvent) {
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
	
	private boolean undisguise(Object target, boolean fireEvent) {
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
	
	private void sendHelpMessage(CommandSender sender, Command command, String alias, int pageNumber) { // TODO: rework help message
		if(!sender.hasPermission("iDisguise.help")) {
			sender.sendMessage(language.NO_PERMISSION);
			return;
		}
		alias = alias.toLowerCase(Locale.ENGLISH);
		boolean self = command.getName().equalsIgnoreCase("disguise");
		String disguiseCommand = "/" + (self ? alias : alias + " <target>");
		String undisguiseCommand = "/" + alias.replaceAll("o?disguise$", "undisguise").replaceAll("o?dis$", "undis").replaceAll("o?d$", "ud");
		
		StringBuilder builder = new StringBuilder();
		
		Calendar today = Calendar.getInstance();
		if(today.get(Calendar.MONTH) == Calendar.NOVEMBER && today.get(Calendar.DAY_OF_MONTH) == 6) {
			builder.append("\n" + language.EASTER_EGG_BIRTHDAY.replace("%age%", Integer.toString(today.get(Calendar.YEAR) - 2012)));
		}
		
		builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " help").replace("%description%", language.HELP_HELP));
		if(sender.hasPermission("iDisguise.player.display-name")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " player [skin] <name>").replace("%description%", self ? language.HELP_PLAYER_SELF : language.HELP_PLAYER_OTHER));
		} else {
			builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " player <name>").replace("%description%", self ? language.HELP_PLAYER_SELF : language.HELP_PLAYER_OTHER));
		}
		if(sender.hasPermission("iDisguise.random")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " random").replace("%description%", self ? language.HELP_RANDOM_SELF : language.HELP_RANDOM_OTHER));
		}
		if(sender.hasPermission("iDisguise.reload")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", "/" + alias + " reload").replace("%description%", language.HELP_RELOAD));
		}
		if(sender.hasPermission("iDisguise.see-through")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", "/" + alias + " see-through [on/off]").replace("%description%", self ? language.HELP_SEE_THROUGH_SELF : language.HELP_SEE_THROUGH_OTHER));
		}
		builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " status").replace("%description%", self ? language.HELP_STATUS_SELF : language.HELP_STATUS_OTHER));
		if(self) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", undisguiseCommand).replace("%description%", language.HELP_UNDISGUISE_SELF));
		}
		if(sender.hasPermission("iDisguise.undisguise.all")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", undisguiseCommand + " <*/*o/*p/*e> [ignore]").replace("%description%", language.HELP_UNDISGUISE_ALL_NEW));
		}
		if(sender.hasPermission("iDisguise.undisguise.others")) {
			builder.append("\n" + language.HELP_BASE.replace("%command%", undisguiseCommand + " <target> [ignore]").replace("%description%", language.HELP_UNDISGUISE_OTHER));
		}
		builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " [subtype] <type> [subtype]").replace("%description%", self ? language.HELP_DISGUISE_SELF : language.HELP_DISGUISE_OTHER));
		builder.append("\n" + language.HELP_BASE.replace("%command%", disguiseCommand + " <subtype>").replace("%description%", language.HELP_SUBTYPE));
		if(!self) {
			for(String message : new String[] {language.HELP_TARGET_TITLE, language.HELP_TARGET_UID, language.HELP_TARGET_EID, language.HELP_TARGET_VANILLA, language.HELP_TARGET_NAME_EXACT, language.HELP_TARGET_NAME_MATCH}) {
				builder.append("\n" + message);
			}
		}
		StringBuilder builder2 = new StringBuilder();
		String color = ChatColor.getLastColors(language.HELP_TYPES);
		for(DisguiseType type : DisguiseType.values()) {
			if(!type.isPlayer()) {
				String format = !type.isAvailable() ? language.HELP_TYPES_NOT_SUPPORTED : hasPermission(sender, type) ? language.HELP_TYPES_AVAILABLE : language.HELP_TYPES_NO_PERMISSION;
				if(format.contains("%type%")) {	
					builder2.append(format.replace("%type%", type.getCustomCommandArgument()));
					builder2.append(color + ", ");
				}
			}
		}
		if(builder2.length() > 2) {
			builder.append("\n" + language.HELP_TYPES.replace("%types%", builder2.substring(0, builder2.length() - 2)));
		}
		
		ChatPaginator.ChatPage page = ChatPaginator.paginate(builder.substring(1), pageNumber, 45, 9);
		sender.sendMessage(language.HELP_INFO_NEW.replace("%name%", "iDisguise").replace("%version%", getVersion()).replace("%page%", Integer.toString(page.getPageNumber())).replace("%total%", Integer.toString(page.getTotalPages())));
		for(String line : page.getLines()) {
			sender.sendMessage(line);
		}
	}
	
	private boolean hasPermission(CommandSender sender, DisguiseType type) {
		if(type.isMob()) return sender.hasPermission("iDisguise.mob." + type.name().toLowerCase(Locale.ENGLISH));
		if(type.isObject()) return sender.hasPermission("iDisguise.object." + type.name().toLowerCase(Locale.ENGLISH));
		return false;
	}
	
	private boolean hasPermission(CommandSender sender, Disguise disguise) {
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
							return (((EndermanDisguise)disguise).getBlockInHand().equals(Material.AIR) || sender.hasPermission("iDisguise.mob.enderman.block"));
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
				switch(disguise.getType()) {
					case FALLING_BLOCK:	
						return sender.hasPermission("iDisguise.object.falling_block.material." + ((FallingBlockDisguise)disguise).getMaterial().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
					case ITEM:
						return sender.hasPermission("iDisguise.object.item.material." + ((ItemDisguise)disguise).getMaterial().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
					default:
						return true;
				}
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
		DisguiseManager.disguiseViewSelf = PacketHandler.disguiseViewSelf = configuration.DISGUISE_VIEW_SELF;
		PacketHandler.showOriginalPlayerName = configuration.NAME_TAG_SHOWN;
		PacketHandler.modifyPlayerListEntry = configuration.MODIFY_PLAYER_LIST_ENTRY;
		DisguiseManager.modifyScoreboardPackets = PacketHandler.modifyScoreboardPackets = configuration.MODIFY_SCOREBOARD_PACKETS;
		PacketHandler.replaceSoundEffects = configuration.REPLACE_SOUND_EFFECTS;
		PacketHandler.bungeeCord = configuration.BUNGEE_CORD;
		
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