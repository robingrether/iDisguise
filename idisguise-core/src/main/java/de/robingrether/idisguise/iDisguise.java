package de.robingrether.idisguise;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseEvent;
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
import de.robingrether.idisguise.management.util.EntityIdList;
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
		configuration.loadData();
		configuration.saveData();
		language = new Language(this);
		language.loadData();
		language.saveData();
		PacketHandler.showOriginalPlayerName = configuration.NAME_TAG_SHOWN;
		PacketHandler.modifyPlayerListEntry = configuration.MODIFY_PLAYER_LIST_ENTRY;
		DisguiseManager.modifyScoreboardPackets = PacketHandler.modifyScoreboardPackets = configuration.MODIFY_SCOREBOARD_PACKETS;
		Sounds.setEnabled(configuration.REPLACE_SOUND_EFFECTS);
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
			loadData();
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
			EntityIdList.addPlayer(player);
			ProfileHelper.getInstance().registerGameProfile(player);
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
			saveData();
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
			saveData();
		}
		enabled = false;
		configuration.loadData();
		configuration.saveData();
		language.loadData();
		language.saveData();
		PacketHandler.showOriginalPlayerName = configuration.NAME_TAG_SHOWN;
		PacketHandler.modifyPlayerListEntry = configuration.MODIFY_PLAYER_LIST_ENTRY;
		DisguiseManager.modifyScoreboardPackets = PacketHandler.modifyScoreboardPackets = configuration.MODIFY_SCOREBOARD_PACKETS;
		Sounds.setEnabled(configuration.REPLACE_SOUND_EFFECTS);
		if(configuration.KEEP_DISGUISE_SHUTDOWN) {
			loadData();
		}
		enabled = true;
		DisguiseManager.resendPackets();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(StringUtil.equalsIgnoreCase(command.getName(), "disguise", "odisguise")) {
			if(args.length == 0) {
				sendHelpMessage(sender, command, alias);
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("iDisguise.reload")) {
					onReload();
					sender.sendMessage(language.RELOAD_COMPLETE);
				} else {
					sender.sendMessage(language.NO_PERMISSION);
				}
			} else {
				OfflinePlayer player = null;
				boolean disguiseSelf;
				if(command.getName().equalsIgnoreCase("disguise")) {
					if(sender instanceof Player) {
						player = (Player)sender;
						disguiseSelf = true;
					} else {
						sender.sendMessage(language.CONSOLE_USE_OTHER_COMMAND);
						return true;
					}
				} else if(args.length > 1) {
					if(sender.hasPermission("iDisguise.others")) {
						if(getServer().getPlayerExact(args[0]) != null) {
							player = getServer().getPlayerExact(args[0]);
						} else if(getServer().matchPlayer(args[0]).size() == 1) {
							player = getServer().matchPlayer(args[0]).get(0);
						} else if(getServer().getOfflinePlayer(args[0]) != null) {
							player = getServer().getOfflinePlayer(args[0]);
						} else {
							sender.sendMessage(language.CANNOT_FIND_PLAYER.replace("%player%", args[0]));
							return true;
						}
						disguiseSelf = false;
						args = Arrays.copyOfRange(args, 1, args.length);
					} else {
						sender.sendMessage(language.NO_PERMISSION);
						return true;
					}
				} else {
					sendHelpMessage(sender, command, alias);
					return true;
				}
				if(args[0].equalsIgnoreCase("help")) {
					sendHelpMessage(sender, command, alias);
				} else if(StringUtil.equalsIgnoreCase(args[0], "player", "p")) {
					if(args.length < 2) {
						sender.sendMessage(language.WRONG_USAGE_NO_NAME);
					} else {
						String skinName = args.length == 2 ? args[1].replaceAll("&[0-9a-fk-or]", "") : args[1], displayName = args.length == 2 ? ChatColor.translateAlternateColorCodes('&', args[1]) : ChatColor.translateAlternateColorCodes('&', args[2]);
						if(!Validate.minecraftUsername(skinName)) {
							sender.sendMessage(language.INVALID_NAME);
						} else {
							PlayerDisguise disguise = new PlayerDisguise(skinName, displayName);
							if(hasPermission(sender, disguise)) {
								if(player.isOnline()) {
									DisguiseEvent event = new DisguiseEvent(player.getPlayer(), disguise);
									getServer().getPluginManager().callEvent(event);
									if(event.isCancelled()) {
										sender.sendMessage(language.EVENT_CANCELLED);
									} else {
										DisguiseManager.disguise(player, disguise);
										sender.sendMessage((disguiseSelf ? language.DISGUISE_PLAYER_SUCCESS_SELF : language.DISGUISE_PLAYER_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()).replace("%name%", disguise.getDisplayName()));
									}
								} else {
									OfflinePlayerDisguiseEvent event = new OfflinePlayerDisguiseEvent(player, disguise);
									getServer().getPluginManager().callEvent(event);
									if(event.isCancelled()) {
										sender.sendMessage(language.EVENT_CANCELLED);
									} else {
										DisguiseManager.disguise(player, disguise);
										sender.sendMessage((disguiseSelf ? language.DISGUISE_PLAYER_SUCCESS_SELF : language.DISGUISE_PLAYER_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()).replace("%name%", disguise.getDisplayName()));
									}
								}
							} else {
								sender.sendMessage(language.NO_PERMISSION);
							}
						}
					}
				} else if(args[0].equalsIgnoreCase("random")) {
					if(sender.hasPermission("iDisguise.random")) {
						Disguise disguise = (RandomUtil.nextBoolean() ? DisguiseType.random(Type.MOB) : DisguiseType.random(Type.OBJECT)).newInstance();
						if(player.isOnline()) {
							DisguiseEvent event = new DisguiseEvent(player.getPlayer(), disguise);
							getServer().getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								sender.sendMessage(language.EVENT_CANCELLED);
							} else {
								DisguiseManager.disguise(player, disguise);
								sender.sendMessage((disguiseSelf ? language.DISGUISE_SUCCESS_SELF : language.DISGUISE_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()));
							}
						} else {
							OfflinePlayerDisguiseEvent event = new OfflinePlayerDisguiseEvent(player, disguise);
							getServer().getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								sender.sendMessage(language.EVENT_CANCELLED);
							} else {
								DisguiseManager.disguise(player, disguise);
								sender.sendMessage((disguiseSelf ? language.DISGUISE_SUCCESS_SELF : language.DISGUISE_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()));
							}
						}
					} else {
						sender.sendMessage(language.NO_PERMISSION);
					}
				} else if(StringUtil.equalsIgnoreCase(args[0], "status", "state", "stats")) {
					if(DisguiseManager.isDisguised(player)) {
						if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
							PlayerDisguise disguise = (PlayerDisguise)DisguiseManager.getDisguise(player);
							sender.sendMessage((disguiseSelf ? language.STATUS_PLAYER_SELF : language.STATUS_PLAYER_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()).replace("%name%", disguise.getDisplayName()));
							sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
						} else {
							Disguise disguise = DisguiseManager.getDisguise(player);
							sender.sendMessage((disguiseSelf ? language.STATUS_SELF : language.STATUS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()));
							sender.sendMessage(language.STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
						}
					} else {
						sender.sendMessage((disguiseSelf ? language.STATUS_NOT_DISGUISED_SELF : language.STATUS_NOT_DISGUISED_OTHER).replace("%player%", player.getName()));
					}
				} else if(StringUtil.equalsIgnoreCase(args[0], "seethrough", "see-through")) {
					if(sender.hasPermission("iDisguise.see-through")) {
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
						sender.sendMessage(language.NO_PERMISSION);
					}
				} else {
					Disguise disguise = DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
					boolean match = false;
					List<String> unknown_args = new ArrayList<String>(Arrays.asList(args));
					for(Iterator<String> iterator = unknown_args.iterator(); iterator.hasNext(); ) {
						DisguiseType type = DisguiseType.Matcher.match(iterator.next().toLowerCase(Locale.ENGLISH));
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
					if(match) {
						if(hasPermission(sender, disguise)) {
							if(player.isOnline()) {
								DisguiseEvent event = new DisguiseEvent(player.getPlayer(), disguise);
								getServer().getPluginManager().callEvent(event);
								if(event.isCancelled()) {
									sender.sendMessage(language.EVENT_CANCELLED);
								} else {
									DisguiseManager.disguise(player, disguise);
									sender.sendMessage((disguiseSelf ? language.DISGUISE_SUCCESS_SELF : language.DISGUISE_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()));
								}
							} else {
								OfflinePlayerDisguiseEvent event = new OfflinePlayerDisguiseEvent(player, disguise);
								getServer().getPluginManager().callEvent(event);
								if(event.isCancelled()) {
									sender.sendMessage(language.EVENT_CANCELLED);
								} else {
									DisguiseManager.disguise(player, disguise);
									sender.sendMessage((disguiseSelf ? language.DISGUISE_SUCCESS_SELF : language.DISGUISE_SUCCESS_OTHER).replace("%player%", player.getName()).replace("%type%", disguise.getType().toString()));
								}
							}
						} else {
							sender.sendMessage(language.NO_PERMISSION);
						}
					}
					if(!unknown_args.isEmpty()) {
						sender.sendMessage(language.WRONG_USAGE_UNKNOWN_ARGUMENTS.replace("%arguments%", StringUtil.join(", ", unknown_args.toArray(new String[0]))));
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
			} else if(args[0].equals("*")) {
				if(sender.hasPermission("iDisguise.undisguise.all")) {	
					if(args.length > 1 && args[1].equalsIgnoreCase("ignore")) {
						DisguiseManager.undisguiseAll();
						sender.sendMessage(language.UNDISGUISE_SUCCESS_ALL_IGNORE);
					} else {
						Set<Object> disguisedEntities = DisguiseManager.getDisguisedEntities();
						int share = 0, total = disguisedEntities.size();
						for(Object disguisable : disguisedEntities) {
							if(disguisable instanceof LivingEntity && !(disguisable instanceof Player)) {
								//TODO: entity disguise/undisguise event
								DisguiseManager.undisguise((LivingEntity)disguisable);
								share++;
							} else if(disguisable instanceof OfflinePlayer) {
								OfflinePlayer offlinePlayer = (OfflinePlayer)disguisable;
								if(offlinePlayer.isOnline()) {
									UndisguiseEvent event = new UndisguiseEvent(offlinePlayer.getPlayer(), DisguiseManager.getDisguise(offlinePlayer), true);
									getServer().getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										DisguiseManager.undisguise(offlinePlayer);
										share++;
									}
								} else {
									OfflinePlayerUndisguiseEvent event = new OfflinePlayerUndisguiseEvent(offlinePlayer, DisguiseManager.getDisguise(offlinePlayer), true);
									getServer().getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										DisguiseManager.undisguise(offlinePlayer);
										share++;
									}
								}
							}
						}
						sender.sendMessage(language.UNDISGUISE_SUCCESS_ALL.replace("%share%", Integer.toString(share)).replace("%total%", Integer.toString(total)));
					}
				} else {
					sender.sendMessage(language.NO_PERMISSION);
				}
			} else {
				if(sender.hasPermission("iDisguise.undisguise.others")) {
					OfflinePlayer player = null;
					if(getServer().getPlayerExact(args[0]) != null) {
						player = getServer().getPlayerExact(args[0]);
					} else if(getServer().matchPlayer(args[0]).size() == 1) {
						player = getServer().matchPlayer(args[0]).get(0);
					} else if(getServer().getOfflinePlayer(args[0]) != null) {
						player = getServer().getOfflinePlayer(args[0]);
					} else {
						sender.sendMessage(language.CANNOT_FIND_PLAYER.replace("%player%", args[0]));
						return true;
					}
					if(DisguiseManager.isDisguised(player)) {
						if(args.length > 1 && args[1].equalsIgnoreCase("ignore")) {
							DisguiseManager.undisguise(player);
							sender.sendMessage(language.UNDISGUISE_SUCCESS_OTHER.replace("%player%", player.getName()));
						} else {
							if(player.isOnline()) {
								UndisguiseEvent event = new UndisguiseEvent(player.getPlayer(), DisguiseManager.getDisguise(player), false);
								getServer().getPluginManager().callEvent(event);
								if(event.isCancelled()) {
									sender.sendMessage(language.EVENT_CANCELLED);
								} else {
									DisguiseManager.undisguise(player);
									sender.sendMessage(language.UNDISGUISE_SUCCESS_OTHER.replace("%player%", player.getName()));
								}
							} else {
								OfflinePlayerUndisguiseEvent event = new OfflinePlayerUndisguiseEvent(player, DisguiseManager.getDisguise(player), false);
								getServer().getPluginManager().callEvent(event);
								if(event.isCancelled()) {
									sender.sendMessage(language.EVENT_CANCELLED);
								} else {
									DisguiseManager.undisguise(player);
									sender.sendMessage(language.UNDISGUISE_SUCCESS_OTHER.replace("%player%", player.getName()));
								}
							}
						}
					} else {
						sender.sendMessage(language.UNDISGUISE_NOT_DISGUISED_OTHER.replace("%player%", player.getName()));
					}
				} else {
					sender.sendMessage(language.NO_PERMISSION);
				}
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
							completions.add(type.getDefaultCommandArgument());
						}
					}
				}
				Disguise disguise = DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
				for(String argument : args) {
					DisguiseType type = DisguiseType.Matcher.match(argument.toLowerCase(Locale.ENGLISH));
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
					completions.addAll(Subtypes.listSubtypeArguments(disguise));
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
						completions.add(player.getName());
					}
				}
			} else if(sender.hasPermission("iDisguise.others")) {
				OfflinePlayer player = null;
				if(getServer().getPlayerExact(args[0]) != null) {
					player = getServer().getPlayerExact(args[0]);
				} else if(getServer().matchPlayer(args[0]).size() == 1) {
					player = getServer().matchPlayer(args[0]).get(0);
				} else if(getServer().getOfflinePlayer(args[0]) != null) {
					player = getServer().getOfflinePlayer(args[0]);
				}
				if(player != null) {
					if(args.length < 3) {
						completions.addAll(Arrays.asList("help", "player", "status"));
						if(sender.hasPermission("iDisguise.random")) {
							completions.add("random");
						}
						if(sender.hasPermission("iDisguise.see-through")) {
							completions.add("see-through");
						}
						for(DisguiseType type : DisguiseType.values()) {
							if(!type.isPlayer() && type.isAvailable() && hasPermission(sender, type.newInstance())) {
								completions.add(type.getDefaultCommandArgument());
							}
						}
					}
					Disguise disguise = DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
					for(String argument : args) {
						DisguiseType type = DisguiseType.Matcher.match(argument.toLowerCase(Locale.ENGLISH));
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
						completions.addAll(Subtypes.listSubtypeArguments(disguise));
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("undisguise")) {
			if(args.length < 2) {
				if(sender.hasPermission("iDisguise.undisguise.all")) {
					completions.add("*");
				}
				if(sender.hasPermission("iDisguise.undisguise.others")) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(DisguiseManager.isDisguised(player)) {
							completions.add(player.getName());
						}
					}
				}
			} else {
				completions.add("ignore");
			}
		}
		if(args.length > 0) {
			for(int i = 0; i < completions.size(); i++) {
				if(!StringUtil.startsWithIgnoreCase(completions.get(i), args[args.length - 1])) {
					completions.remove(i);
					i--;
				}
			}
		}
		return completions;
	}
	
	private void sendHelpMessage(CommandSender sender, Command command, String alias) {
		if(!sender.hasPermission("iDisguise.help")) {
			sender.sendMessage(language.NO_PERMISSION);
			return;
		}
		alias = alias.toLowerCase(Locale.ENGLISH);
		boolean self = command.getName().equalsIgnoreCase("disguise");
		String disguiseCommand = "/" + (self ? alias : alias + " <player>");
		String undisguiseCommand = "/" + alias.replaceAll("o?disguise$", "undisguise").replaceAll("o?dis$", "undis").replaceAll("o?d$", "ud");
		sender.sendMessage(language.HELP_INFO.replace("%name%", "iDisguise").replace("%version%", getVersion()));
		
		Calendar today = Calendar.getInstance();
		if(today.get(Calendar.MONTH) == Calendar.NOVEMBER && today.get(Calendar.DAY_OF_MONTH) == 6) {
			sender.sendMessage(language.EASTER_EGG_BIRTHDAY.replace("%age%", Integer.toString(today.get(Calendar.YEAR) - 2012)));
		}
		
		sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " help").replace("%description%", language.HELP_HELP));
		if(sender.hasPermission("iDisguise.player.display-name")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " player [skin] <name>").replace("%description%", self ? language.HELP_PLAYER_SELF : language.HELP_PLAYER_OTHER));
		} else {
			sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " player <name>").replace("%description%", self ? language.HELP_PLAYER_SELF : language.HELP_PLAYER_OTHER));
		}
		if(sender.hasPermission("iDisguise.random")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " random").replace("%description%", self ? language.HELP_RANDOM_SELF : language.HELP_RANDOM_OTHER));
		}
		if(sender.hasPermission("iDisguise.reload")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", "/" + alias + " reload").replace("%description%", language.HELP_RELOAD));
		}
		if(sender.hasPermission("iDisguise.see-through")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", "/" + alias + " see-through [on/off]").replace("%description%", self ? language.HELP_SEE_THROUGH_SELF : language.HELP_SEE_THROUGH_OTHER));
		}
		sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " status").replace("%description%", self ? language.HELP_STATUS_SELF : language.HELP_STATUS_OTHER));
		if(self) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", undisguiseCommand).replace("%description%", language.HELP_UNDISGUISE_SELF));
		}
		if(sender.hasPermission("iDisguise.undisguise.all")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", undisguiseCommand + " * [ignore]").replace("%description%", language.HELP_UNDISGUISE_ALL));
		}
		if(sender.hasPermission("iDisguise.undisguise.others")) {
			sender.sendMessage(language.HELP_BASE.replace("%command%", undisguiseCommand + " <player> [ignore]").replace("%description%", language.HELP_UNDISGUISE_OTHER));
		}
		sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " [subtype] <type> [subtype]").replace("%description%", self ? language.HELP_DISGUISE_SELF : language.HELP_DISGUISE_OTHER));
		sender.sendMessage(language.HELP_BASE.replace("%command%", disguiseCommand + " <subtype>").replace("%description%", language.HELP_SUBTYPE));
		StringBuilder builder = new StringBuilder();
		String color = ChatColor.getLastColors(language.HELP_TYPES);
		for(DisguiseType type : DisguiseType.values()) {
			if(!type.isPlayer()) {
				String format = !type.isAvailable() ? language.HELP_TYPES_NOT_SUPPORTED : hasPermission(sender, type) ? language.HELP_TYPES_AVAILABLE : language.HELP_TYPES_NO_PERMISSION;
				if(format.contains("%type%")) {	
					builder.append(format.replace("%type%", type.getDefaultCommandArgument()));
					builder.append(color + ", ");
				}
			}
		}
		sender.sendMessage(language.HELP_TYPES.replace("%types%", builder.substring(0, builder.length() - 2)));
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
	 * Checks if the plugin directory (= data folder) exist and creates such a directory if not.
	 * 
	 * @return <code>true</code> if a file named 'debug' exists in that directory (which means that debug mode shall be enabled), <code>false</code> otherwise
	 */
	private boolean checkDirectory() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		return new File(getDataFolder(), "debug").isFile();
	}
	
	private void loadData() {
		File dataFile = new File(getDataFolder(), "disguises.dat");
		File oldDataFile = new File(getDataFolder(), "data.bin");
		if(dataFile.exists()) {
			DisguiseManager.updateDisguises(SLAPI.loadMap(dataFile));
		} else if(oldDataFile.exists()) {
			Object map = SLAPI.load(oldDataFile);
			if(map instanceof Map) {
				DisguiseManager.updateDisguises((Map<?, Disguise>)map);
			}
			oldDataFile.delete();
		}
	}
	
	private void saveData() {
		File dataFile = new File(getDataFolder(), "disguises.dat");
		SLAPI.saveMap(DisguiseManager.getDisguises(), dataFile);
	}
	
	public DisguiseAPI getAPI() {
		return new DisguiseAPI() {
			
			@Deprecated
			public void disguiseToAll(Player player, Disguise disguise) {
				disguise(player, disguise, false);
			}
			
			@Deprecated
			public void undisguiseToAll(Player player) {
				undisguise(player, false);
			}
			
			public boolean disguise(OfflinePlayer player, Disguise disguise) {
				return disguise(player, disguise, true);
			}
			
			public boolean disguise(OfflinePlayer player, Disguise disguise, boolean fireEvent) {
				if(fireEvent) {
					if(player.isOnline()) {
						DisguiseEvent event = new DisguiseEvent(player.getPlayer(), disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							return false;
						} else {
							DisguiseManager.disguise(player, disguise);
							return true;
						}
					} else {
						OfflinePlayerDisguiseEvent event = new OfflinePlayerDisguiseEvent(player, disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							return false;
						} else {
							DisguiseManager.disguise(player, disguise);
							return true;
						}
					}
				} else {
					DisguiseManager.disguise(player, disguise);
					return true;
				}
			}
			
			public boolean undisguise(OfflinePlayer player) {
				return undisguise(player, true);
			}
			
			public boolean undisguise(OfflinePlayer player, boolean fireEvent) {
				if(!isDisguised(player)) {
					return false;
				}
				if(fireEvent) {
					if(player.isOnline()) {
						UndisguiseEvent event = new UndisguiseEvent(player.getPlayer(), getDisguise(player), false);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							return false;
						} else {
							DisguiseManager.undisguise(player);
							return true;
						}
					} else {
						OfflinePlayerUndisguiseEvent event = new OfflinePlayerUndisguiseEvent(player, getDisguise(player), false);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							return false;
						} else {
							DisguiseManager.undisguise(player);
							return true;
						}
					}
				} else {
					DisguiseManager.undisguise(player);
					return true;
				}
			}
			
			public void undisguiseAll() {
				DisguiseManager.undisguiseAll();
			}
			
			@Deprecated
			public boolean isDisguised(Player player) {
				return isDisguised((OfflinePlayer)player);
			}
			
			public boolean isDisguised(OfflinePlayer player) {
				return DisguiseManager.isDisguised(player);
			}
			
			public boolean isDisguisedTo(OfflinePlayer player, Player observer) {
				return DisguiseManager.isDisguisedTo(player, observer);
			}
			
			@Deprecated
			public Disguise getDisguise(Player player) {
				return getDisguise((OfflinePlayer)player);
			}
			
			public Disguise getDisguise(OfflinePlayer player) {
				return DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
			}
			
			@Deprecated
			public int getOnlineDisguiseCount() {
				return getNumberOfDisguisedPlayers();
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
				return Sounds.isEnabled();
			}
			
			public void setSoundsEnabled(boolean enabled) {
				Sounds.setEnabled(enabled);
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