package de.robingrether.idisguise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.OutdatedServerException;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.Subtypes;
import de.robingrether.idisguise.disguise.DisguiseType.Type;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.util.EntityIdList;
import de.robingrether.idisguise.management.util.VanillaTargetParser;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.RandomUtil;
import de.robingrether.util.StringUtil;
import de.robingrether.util.Validate;

public class CommandExecutor implements TabExecutor {
	
	private final iDisguise plugin;
	
	CommandExecutor(iDisguise plugin) {
		this.plugin = plugin;
		
		registerHelpPages();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(StringUtil.equalsIgnoreCase(command.getName(), "disguise", "odisguise")) {
			if(args.length == 0) {
				sendHelpMessage(sender, command, alias, 1);
			} else if(StringUtil.equalsIgnoreCase(args[0], "help", "?")) {
				int pageNumber = 0;
				if(args.length > 1) {
					try {
						pageNumber = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) { // fail silently
					}
				}
				sendHelpMessage(sender, command, alias, pageNumber);
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("iDisguise.reload")) {
					plugin.onReload();
					sender.sendMessage(plugin.getLanguage().RELOAD_COMPLETE);
				} else {
					sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
				}
			} else {
				Collection<? extends Object> targets = null;
				boolean disguiseSelf;
				if(command.getName().equalsIgnoreCase("disguise")) {
					if(sender instanceof Player) {
						targets = Arrays.asList((Player)sender);
						disguiseSelf = true;
					} else {
						sender.sendMessage(plugin.getLanguage().CONSOLE_USE_OTHER_COMMAND);
						return true;
					}
				} else if(args.length > 1) {
					if(sender.hasPermission("iDisguise.others")) {
						targets = parseTargets(args[0], sender);
						if(targets.size() == 0) {
							sender.sendMessage(plugin.getLanguage().CANNOT_FIND_TARGETS);
							return true;
						}
						disguiseSelf = false;
						args = Arrays.copyOfRange(args, 1, args.length);
					} else {
						sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
						return true;
					}
				} else {
					sendHelpMessage(sender, command, alias, 1);
					return true;
				}
				if(StringUtil.equalsIgnoreCase(args[0], "status", "state", "stats")) {
					for(Object target : targets) {
						if(target instanceof OfflinePlayer) {
							OfflinePlayer player = (OfflinePlayer)target;
							if(DisguiseManager.isDisguised(player)) {
								if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
									PlayerDisguise disguise = (PlayerDisguise)DisguiseManager.getDisguise(player);
									sender.sendMessage((disguiseSelf ? plugin.getLanguage().STATUS_PLAYER_SELF : plugin.getLanguage().STATUS_PLAYER_OTHER).replace("%player%", player.getName() != null ? player.getName() : player.getUniqueId().toString()).replace("%type%", disguise.getType().getCustomCommandArgument()).replace("%name%", disguise.getDisplayName()));
									sender.sendMessage(plugin.getLanguage().STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								} else {
									Disguise disguise = DisguiseManager.getDisguise(player);
									sender.sendMessage((disguiseSelf ? plugin.getLanguage().STATUS_SELF : plugin.getLanguage().STATUS_OTHER).replace("%player%", player.getName() != null ? player.getName() : player.getUniqueId().toString()).replace("%type%", disguise.getType().getCustomCommandArgument()));
									sender.sendMessage(plugin.getLanguage().STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								}
							} else {
								sender.sendMessage((disguiseSelf ? plugin.getLanguage().STATUS_NOT_DISGUISED_SELF : plugin.getLanguage().STATUS_NOT_DISGUISED_OTHER).replace("%player%", player.getName() != null ? player.getName() : player.getUniqueId().toString()));
							}
						} else {
							LivingEntity livingEntity = (LivingEntity)target;
							if(DisguiseManager.isDisguised(livingEntity)) {
								if(DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise) {
									PlayerDisguise disguise = (PlayerDisguise)DisguiseManager.getDisguise(livingEntity);
									sender.sendMessage(plugin.getLanguage().STATUS_PLAYER_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()).replace("%name%", disguise.getDisplayName()));
									sender.sendMessage(plugin.getLanguage().STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								} else {
									Disguise disguise = DisguiseManager.getDisguise(livingEntity);
									sender.sendMessage(plugin.getLanguage().STATUS_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()));
									sender.sendMessage(plugin.getLanguage().STATUS_SUBTYPES.replace("%subtypes%", disguise.toString()));
								}
							} else {
								sender.sendMessage(plugin.getLanguage().STATUS_NOT_DISGUISED_OTHER.replace("%player%", livingEntity.getType().name() + " [" + livingEntity.getEntityId() + "]"));
							}
						}
					}
				} else if(StringUtil.equalsIgnoreCase(args[0], "seethrough", "see-through")) {
					for(Object target : targets) {
						if(sender.hasPermission("iDisguise.see-through")) {
							if(target instanceof OfflinePlayer) {
								OfflinePlayer player = (OfflinePlayer)target;
								if(args.length < 2) {
									sender.sendMessage((DisguiseManager.canSeeThrough(player) ? disguiseSelf ? plugin.getLanguage().SEE_THROUGH_STATUS_ON_SELF : plugin.getLanguage().SEE_THROUGH_STATUS_ON_OTHER : disguiseSelf ? plugin.getLanguage().SEE_THROUGH_STATUS_OFF_SELF : plugin.getLanguage().SEE_THROUGH_STATUS_OFF_OTHER).replace("%player%", player.getName() != null ? player.getName() : player.getUniqueId().toString()));
								} else if(StringUtil.equalsIgnoreCase(args[1], "on", "off")) {
									boolean seeThrough = args[1].equalsIgnoreCase("on");
									DisguiseManager.setSeeThrough(player, seeThrough);
									sender.sendMessage((seeThrough ? disguiseSelf ? plugin.getLanguage().SEE_THROUGH_ENABLE_SELF : plugin.getLanguage().SEE_THROUGH_ENABLE_OTHER : disguiseSelf ? plugin.getLanguage().SEE_THROUGH_DISABLE_SELF : plugin.getLanguage().SEE_THROUGH_DISABLE_OTHER).replace("%player%", player.getName() != null ? player.getName() : player.getUniqueId().toString()));
								} else {
									sender.sendMessage(plugin.getLanguage().WRONG_USAGE_SEE_THROUGH.replace("%argument%", args[1]));
								}
							} else {
								sender.sendMessage(plugin.getLanguage().SEE_THROUGH_ENTITY);
							}
						} else {
							sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
						}
					}
				} else {
					Disguise disguise = null;
					if(StringUtil.equalsIgnoreCase(args[0], "player", "p")) {
						if(args.length < 2) {
							sender.sendMessage(plugin.getLanguage().WRONG_USAGE_NO_NAME);
							return true;
						} else {
							String skinName = args.length == 2 ? args[1].replaceAll("&[0-9a-fk-or]", "") : args[1];
							String displayName = args.length == 2 ? ChatColor.translateAlternateColorCodes('&', args[1]) : ChatColor.translateAlternateColorCodes('&', args[2].replace("\\s", " "));
							if(!Validate.minecraftUsername(skinName)) {
								sender.sendMessage(plugin.getLanguage().INVALID_NAME);
								return true;
							} else {
								disguise = new PlayerDisguise(skinName, displayName);
							}
						}
					} else if(StringUtil.equalsIgnoreCase(args[0], "random")) {
						if(sender.hasPermission("iDisguise.random")) {
							disguise = DisguiseType.random(RandomUtil.nextBoolean() ? Type.MOB : Type.OBJECT).newInstance();
						} else {
							sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
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
									sender.sendMessage(plugin.getLanguage().WRONG_USAGE_TWO_DISGUISE_TYPES);
									return true;
								}
								try {
									disguise = type.newInstance();
									match = true;
									iterator.remove();
								} catch(OutdatedServerException e) {
									sender.sendMessage(plugin.getLanguage().OUTDATED_SERVER);
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
							sender.sendMessage(plugin.getLanguage().WRONG_USAGE_UNKNOWN_ARGUMENTS.replace("%arguments%", StringUtil.join(", ", unknown_args.toArray(new String[0]))));
						}
						if(!match) return true;
					}
					if(plugin.hasPermission(sender, disguise)) {
						int successes = 0;
						for(Object target : targets) {
							if(plugin.disguise(target, disguise, true)) successes++;
						}
						if(targets.size() == 1) {
							Object target = targets.iterator().next();
							if(successes == 1) {
								sender.sendMessage((disguiseSelf ? plugin.getLanguage().DISGUISE_SUCCESS_SELF : plugin.getLanguage().DISGUISE_SUCCESS_OTHER)
										.replace("%player%", target instanceof OfflinePlayer ? ((OfflinePlayer)target).getName() != null ? ((OfflinePlayer)target).getName() : ((OfflinePlayer)target).getUniqueId().toString() : ((LivingEntity)target).getType().name() + " [" + ((LivingEntity)target).getEntityId() + "]").replace("%type%", disguise.getType().getCustomCommandArgument()));
							} else {
								sender.sendMessage(plugin.getLanguage().EVENT_CANCELLED);
							}
						} else {
							sender.sendMessage(plugin.getLanguage().DISGUISE_SUCCESS_MULTIPLE.replace("%share%", Integer.toString(successes)).replace("%total%", Integer.toString(targets.size())).replace("%type%", disguise.getType().getCustomCommandArgument()));
						}
					} else {
						sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("undisguise")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					if(DisguiseManager.isDisguised((Player)sender)) {
						if(!plugin.getConfiguration().UNDISGUISE_PERMISSION || sender.hasPermission("iDisguise.undisguise")) {
							UndisguiseEvent event = new UndisguiseEvent((Player)sender, DisguiseManager.getDisguise((Player)sender), false);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								sender.sendMessage(plugin.getLanguage().EVENT_CANCELLED);
							} else {
								DisguiseManager.undisguise((Player)sender);
								sender.sendMessage(plugin.getLanguage().UNDISGUISE_SUCCESS_SELF);
							}
						} else {
							sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
						}
					} else {
						sender.sendMessage(plugin.getLanguage().UNDISGUISE_NOT_DISGUISED_SELF);
					}
				} else {
					sender.sendMessage(plugin.getLanguage().UNDISGUISE_CONSOLE);
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
							disguisedEntities.removeIf(target -> target instanceof Player ? !on : target instanceof OfflinePlayer ? !off : target instanceof LivingEntity ? !ent : true);
							targets = disguisedEntities;
						} else {
							sender.sendMessage(plugin.getLanguage().WRONG_USAGE_UNKNOWN_ARGUMENTS.replace("%arguments%", args[0]));
							return true;
						}
					} else {
						sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
						return true;
					}
				} else {
					if(sender.hasPermission("iDisguise.undisguise.others")) {
						targets = parseTargets(args[0], sender);
						if(targets.size() == 0) {
							sender.sendMessage(plugin.getLanguage().CANNOT_FIND_TARGETS);
							return true;
						}
					} else {
						sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
						return true;
					}
				}
				boolean fireEvent = args.length > 1 ? !args[1].equalsIgnoreCase("ignore") : true;
				int successes = 0, total = 0;
				for(Object target : targets) {
					if(target instanceof OfflinePlayer ? DisguiseManager.isDisguised((OfflinePlayer)target) : DisguiseManager.isDisguised((LivingEntity)target)) {
						total++;
						if(plugin.undisguise(target, fireEvent)) successes++;
					}
				}
				sender.sendMessage(plugin.getLanguage().UNDISGUISE_SUCCESS_MULTIPLE.replace("%share%", Integer.toString(successes)).replace("%total%", Integer.toString(total)));
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
					completions.addAll(Arrays.asList("?", "help", "player", "status"));
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
				completions.addAll(Arrays.asList("?", "help"));
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
						completions.addAll(Arrays.asList("player", "status"));
						if(sender.hasPermission("iDisguise.random")) {
							completions.add("random");
						}
						if(sender.hasPermission("iDisguise.see-through") && ObjectUtil.instanceOf(OfflinePlayer.class, targets)) {
							completions.add("see-through");
						}
						for(DisguiseType type : DisguiseType.values()) {
							if(!type.isPlayer() && type.isAvailable() && plugin.hasPermission(sender, type.newInstance())) {
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
	
	private final Pattern accountIdPattern = Pattern.compile("<?([0-9A-Fa-f]{8})-?([0-9A-Fa-f]{4})-?([0-9A-Fa-f]{4})-?([0-9A-Fa-f]{4})-?([0-9A-Fa-f]{12})>?");
	
	private Collection<? extends Object> parseTargets(String argument, CommandSender sender) {
		if(argument.charAt(0) == '@' || argument.charAt(0) == '#') {
			return VanillaTargetParser.getInstance().parseVanillaTargets('@' + argument.substring(1), sender);
		} else {
			List<Object> targets = new ArrayList<Object>();
			for(String arg : argument.split(",")) {
				Object target = null;
				Matcher matcher = accountIdPattern.matcher(arg);
				if(matcher.matches()) {
					target = Bukkit.getOfflinePlayer(UUID.fromString(matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + "-" + matcher.group(4) + "-" + matcher.group(5)));
				} else if(arg.matches("\\[[0-9]+\\]")) {
					target = EntityIdList.getEntityByEntityId(Integer.parseInt(arg.substring(1, arg.length() - 1)));
				} else if(arg.matches("\\{[A-Za-z0-9_]{1,16}\\}")) {
					target = Bukkit.getOfflinePlayer(arg.substring(1, arg.length() - 1));
				} else if(Bukkit.getPlayerExact(arg) != null) {
					target = Bukkit.getPlayerExact(arg);
				} else if(Bukkit.matchPlayer(arg).size() == 1) {
					target = Bukkit.matchPlayer(arg).get(0);
				}
				if(target != null) targets.add(target);
			}
			return targets;
		}
	}
	
	private List<HelpPage> helpPages;
	
	private void registerHelpPages() {
		helpPages = new ArrayList<HelpPage>();
		
		helpPages.add(null);
		
		helpPages.add(new HelpPage(plugin.getLanguage().HELP_TITLE_DISGUISE) {
			void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
				Calendar today = Calendar.getInstance();
				if(today.get(Calendar.MONTH) == Calendar.NOVEMBER && today.get(Calendar.DAY_OF_MONTH) == 6) {
					sender.sendMessage(plugin.getLanguage().EASTER_EGG_BIRTHDAY.replace("%age%", Integer.toString(today.get(Calendar.YEAR) - 2012)));
				}
				if(sender.hasPermission("iDisguise.player.display-name")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " player [skin] <name>").replace("%description%", self ? plugin.getLanguage().HELP_PLAYER_SELF : plugin.getLanguage().HELP_PLAYER_OTHER));
				} else {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " player <name>").replace("%description%", self ? plugin.getLanguage().HELP_PLAYER_SELF : plugin.getLanguage().HELP_PLAYER_OTHER));
				}
				sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " [subtypes ...] <type> [subtypes ...]").replace("%description%", self ? plugin.getLanguage().HELP_DISGUISE_SELF : plugin.getLanguage().HELP_DISGUISE_OTHER));
				sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " <subtypes ...>").replace("%description%", plugin.getLanguage().HELP_SUBTYPE));
				if(sender.hasPermission("iDisguise.random")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " random").replace("%description%", self ? plugin.getLanguage().HELP_RANDOM_SELF : plugin.getLanguage().HELP_RANDOM_OTHER));
				}
				sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " status").replace("%description%", self ? plugin.getLanguage().HELP_STATUS_SELF : plugin.getLanguage().HELP_STATUS_OTHER));
			}
		});
		
		helpPages.add(new HelpPage(plugin.getLanguage().HELP_TITLE_UNDISGUISE) {
			void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
				if(self) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", undisguiseCommand).replace("%description%", plugin.getLanguage().HELP_UNDISGUISE_SELF));
				}
				if(sender.hasPermission("iDisguise.undisguise.all")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", undisguiseCommand + " <*/*o/*p/*e> [ignore]").replace("%description%", plugin.getLanguage().HELP_UNDISGUISE_ALL_NEW));
				}
				if(sender.hasPermission("iDisguise.undisguise.others")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", undisguiseCommand + " <target> [ignore]").replace("%description%", plugin.getLanguage().HELP_UNDISGUISE_OTHER));
				}
			}
		});
		
		helpPages.add(new HelpPage(plugin.getLanguage().HELP_TITLE_TYPES) {
			void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
				StringBuilder builder = new StringBuilder();
				String color = ChatColor.getLastColors(plugin.getLanguage().HELP_TYPES);
				for(DisguiseType type : DisguiseType.values()) {
					if(!type.isPlayer()) {
						String format = !type.isAvailable() ? plugin.getLanguage().HELP_TYPES_NOT_SUPPORTED : plugin.hasPermission(sender, type) ? plugin.getLanguage().HELP_TYPES_AVAILABLE : plugin.getLanguage().HELP_TYPES_NO_PERMISSION;
						if(format.contains("%type%")) {	
							builder.append(format.replace("%type%", type.getCustomCommandArgument()));
							builder.append(color + ", ");
						}
					}
				}
				if(builder.length() > 2) {
					sender.sendMessage(plugin.getLanguage().HELP_TYPES.replace("%types%", builder.substring(0, builder.length() - 2)));
				}
			}
		});
		
		helpPages.add(new HelpPage(plugin.getLanguage().HELP_TITLE_FEATURES) {
			void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
				if(sender.hasPermission("iDisguise.reload")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", "/" + alias + " reload").replace("%description%", plugin.getLanguage().HELP_RELOAD));
				}
				if(sender.hasPermission("iDisguise.see-through")) {
					sender.sendMessage(plugin.getLanguage().HELP_BASE.replace("%command%", disguiseCommand + " see-through [on/off]").replace("%description%", self ? plugin.getLanguage().HELP_SEE_THROUGH_SELF : plugin.getLanguage().HELP_SEE_THROUGH_OTHER));
				}
			}
		});
		
		helpPages.add(new HelpPage(plugin.getLanguage().HELP_TITLE_TARGETS) {
			void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
				for(String message : new String[] {plugin.getLanguage().HELP_TARGET_UID, plugin.getLanguage().HELP_TARGET_EID, plugin.getLanguage().HELP_TARGET_VANILLA, plugin.getLanguage().HELP_TARGET_NAME_EXACT, plugin.getLanguage().HELP_TARGET_NAME_MATCH, plugin.getLanguage().HELP_TARGET_VANILLA_TIP}) {
					sender.sendMessage(message);
				}
			}
		});
	}
	
	private void sendHelpMessage(CommandSender sender, Command command, String alias, int pageNumber) {
		if(!sender.hasPermission("iDisguise.help")) {
			sender.sendMessage(plugin.getLanguage().NO_PERMISSION);
			return;
		}
		alias = alias.toLowerCase(Locale.ENGLISH);
		boolean self = command.getName().equalsIgnoreCase("disguise");
		String disguiseCommand = "/" + (self ? alias : alias + " <target>");
		String undisguiseCommand = "/" + (alias.equals("d") ? "ud" : alias.endsWith("s") ? "undis" : "undisguise");
		int totalPages = helpPages.size() - 1;
		if(pageNumber < 1) pageNumber = 1;
		if(pageNumber > totalPages) pageNumber = totalPages;
		
		helpPages.get(pageNumber).send(sender, pageNumber, totalPages, alias, self, disguiseCommand, undisguiseCommand);
	}
	
	abstract class HelpPage {
		
		final String title;
		
		HelpPage(String title) {
			this.title = title;
		}
		
		final void send(CommandSender sender, int pageNumber, int totalPages,  String alias, boolean self, String disguiseCommand, String undisguiseCommand) {
			sender.sendMessage(plugin.getLanguage().HELP_TITLE.replace("%title%", title).replace("%name%", "iDisguise").replace("%version%", plugin.getVersion()).replace("%page%", Integer.toString(pageNumber)).replace("%total%", Integer.toString(totalPages)));
			sendContent(sender, alias, self, disguiseCommand, undisguiseCommand);
			sender.sendMessage(plugin.getLanguage().HELP_INFO.replace("%command%", "/" + alias + " help [page]"));
			
			StringBuilder builder = new StringBuilder();
			for(int i = 1; i < helpPages.size(); i++) {
				builder.append(plugin.getLanguage().HELP_INFO_FORMAT.replace("%title%", helpPages.get(i).title).replace("%page%", Integer.toString(i)));
			}
			if(builder.length() > 2) sender.sendMessage(builder.substring(0, builder.length() - 2));
		}
		
		abstract void sendContent(CommandSender sender, String alias, boolean self, String disguiseCommand, String undisguiseCommand);
		
	}
	
}