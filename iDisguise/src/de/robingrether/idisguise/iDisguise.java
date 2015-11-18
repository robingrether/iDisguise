package de.robingrether.idisguise;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.idisguise.disguise.ColoredDisguise;
import de.robingrether.idisguise.disguise.CreeperDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.DisguiseType.Type;
import de.robingrether.idisguise.disguise.EndermanDisguise;
import de.robingrether.idisguise.disguise.FallingBlockDisguise;
import de.robingrether.idisguise.disguise.GuardianDisguise;
import de.robingrether.idisguise.disguise.HorseDisguise;
import de.robingrether.idisguise.disguise.HorseDisguise.Armor;
import de.robingrether.idisguise.disguise.HorseDisguise.Color;
import de.robingrether.idisguise.disguise.HorseDisguise.Style;
import de.robingrether.idisguise.disguise.HorseDisguise.Variant;
import de.robingrether.idisguise.disguise.ItemDisguise;
import de.robingrether.idisguise.disguise.MinecartDisguise;
import de.robingrether.idisguise.disguise.RabbitDisguise.RabbitType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.OcelotDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.RabbitDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.SkeletonDisguise;
import de.robingrether.idisguise.disguise.VillagerDisguise;
import de.robingrether.idisguise.disguise.WolfDisguise;
import de.robingrether.idisguise.disguise.ZombieDisguise;
import de.robingrether.idisguise.io.Configuration;
import de.robingrether.idisguise.io.Metrics.Graph;
import de.robingrether.idisguise.io.Metrics.Plotter;
import de.robingrether.idisguise.io.Metrics;
import de.robingrether.idisguise.io.SLAPI;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.ChannelRegister;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.PacketHelper;
import de.robingrether.idisguise.management.Sounds;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.util.RandomUtil;
import de.robingrether.util.StringUtil;
import de.robingrether.util.Validate;

public class iDisguise extends JavaPlugin {
	
	public static final File directory = new File("plugins/iDisguise");
	
	private EventListener listener;
	private Configuration configuration;
	private Metrics metrics;
	private boolean enabled = false;
	
	public void onEnable() {
		if(!VersionHelper.init()) {
			getLogger().log(Level.SEVERE, String.format("%s is not compatible with your server version!", getFullName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		checkDirectory();
		listener = new EventListener(this);
		configuration = new Configuration(this, directory);
		configuration.loadData();
		configuration.saveData();
		PacketHelper.instance.setAttribute(0, configuration.getBoolean(Configuration.SHOW_PLAYER_NAMES));
		Sounds.setEnabled(configuration.getBoolean(Configuration.REPLACE_SOUNDS));
		try {
			metrics = new Metrics(this);
			Graph graph1 = metrics.createGraph("Disguise Count");
			graph1.addPlotter(new Plotter("Disguise Count") {
				public int getValue() {
					return DisguiseManager.instance.getOnlineDisguiseCount();
				}
			});
			Graph graph3 = metrics.createGraph("Sound System");
			graph3.addPlotter(new Plotter(configuration.getBoolean(Configuration.REPLACE_SOUNDS) ? "enabled" : "disabled") {
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch(Exception e) {
		}
		if(configuration.getBoolean(Configuration.STORE_DISGUISES)) {
			loadData();
		}
		getServer().getPluginManager().registerEvents(listener, this);
		if(configuration.getBoolean(Configuration.GHOST_DISGUISES)) {
			GhostFactory.instance.enable(this);
		}
		getServer().getServicesManager().register(DisguiseAPI.class, getAPI(), this, ServicePriority.Normal);
		if(configuration.getBoolean(Configuration.CHECK_FOR_UPDATES)) {
			getServer().getScheduler().runTaskLaterAsynchronously(this, new UpdateCheck(this, getServer().getConsoleSender(), ChatColor.GOLD + "[iDisguise] " + "An update for iDisguise is available: " + ChatColor.ITALIC + "%s"), 20L);
		}
		getLogger().log(Level.INFO, String.format("%s enabled!", getFullName()));
		enabled = true;
		ChannelRegister.instance.registerOnlinePlayers();
	}
	
	public void onDisable() {
		if(!enabled) {
			return;
		}
		if(configuration.getBoolean(Configuration.GHOST_DISGUISES)) {
			GhostFactory.instance.disable();
		}
		getServer().getScheduler().cancelTasks(this);
		if(configuration.getBoolean(Configuration.STORE_DISGUISES)) {
			saveData();
		}
		getLogger().log(Level.INFO, String.format("%s disabled!", getFullName()));
		enabled = false;
	}
	
	public void onReload() {
		if(!enabled) {
			return;
		}
		if(configuration.getBoolean(Configuration.GHOST_DISGUISES)) {
			GhostFactory.instance.disable();
		}
		if(configuration.getBoolean(Configuration.STORE_DISGUISES)) {
			saveData();
		}
		enabled = false;
		configuration = new Configuration(this, directory);
		configuration.loadData();
		configuration.saveData();
		PacketHelper.instance.setAttribute(0, configuration.getBoolean(Configuration.SHOW_PLAYER_NAMES));
		Sounds.setEnabled(configuration.getBoolean(Configuration.REPLACE_SOUNDS));
		if(configuration.getBoolean(Configuration.STORE_DISGUISES)) {
			loadData();
		}
		if(configuration.getBoolean(Configuration.GHOST_DISGUISES)) {
			GhostFactory.instance.enable(this);
		}
		enabled = true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(StringUtil.equalsIgnoreCase(cmd.getName(), "d", "dis", "disguise")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You have to use " + ChatColor.ITALIC + "/odisguise" + ChatColor.RESET + ChatColor.RED + " from console.");
				return true;
			}
			if(!(isDisguisingPermittedInWorld(player.getWorld()) || player.hasPermission("iDisguise.everywhere"))) {
				sender.sendMessage(ChatColor.RED + "Using this plugin is prohibited in this world.");
				return true;
			}
			if(args.length == 0 || StringUtil.equalsIgnoreCase(args[0], "?", "help")) {
				sender.sendMessage(ChatColor.GREEN + getFullName() + " - Help");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " help - Shows this");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " player <name> - Disguise as a player");
				if(getConfiguration().getBoolean(Configuration.GHOST_DISGUISES) && player.hasPermission("iDisguise.ghost")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " ghost <name> - Disguise as a ghost player");
				}
				if(player.hasPermission("iDisguise.random")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " random - Disguise as a random mob");
				}
				if(player.hasPermission("iDisguise.reload")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " reload - Reloads the config file");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " status - Shows what you are currently disguised as");
				if(!getConfiguration().getBoolean(Configuration.UNDISGUISE_PERMISSION) || player.hasPermission("iDisguise.undisguise")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("d") ? "" : "n") + cmd.getName() + " - Undisguise");
				}
				if(player.hasPermission("iDisguise.undisguise.all")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("d") ? "" : "n") + cmd.getName() + " * - Undisguise everyone");
				}
				if(player.hasPermission("iDisguise.undisguise.others")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("d") ? "" : "n") + cmd.getName() + " <player> - Undisguise another player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " [subtype] <mobtype> [subtype] - Disguise as a mob with optional subtypes");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <subtype> - Change your current subtypes");
				sender.sendMessage(ChatColor.GOLD + "Mobtypes:");
				sender.sendMessage(ChatColor.GRAY + " bat, blaze, cave_spider, chicken, cow, creeper, ender_dragon, enderman, endermite, ghast, giant, guardian, horse, iron_golem, magma_cube, mushroom_cow, ocelot, pig, pig_zombie, rabbit, sheep, silverfish, skeleton, slime, snowman, spider, squid, villager, witch, witherboss, wolf, zombie");
				if(DisguiseManager.instance.isDisguised(player)) {
					sendSubtypeInformation(sender, DisguiseManager.instance.getDisguise(player).getType());
				}
			} else if(StringUtil.equalsIgnoreCase(args[0], "player", "p")) {
				if(args.length == 1) {
					sender.sendMessage(ChatColor.RED + "Wrong usage: " + ChatColor.ITALIC + "/" + cmd.getName() + " " + args[0] + " <name>");
				} else if(!Validate.minecraftUsername(args[1])) {
					sender.sendMessage(ChatColor.RED + "The given username is invalid.");
				} else {
					PlayerDisguise disguise = new PlayerDisguise(args[1], false);
					if(hasPermission(player, disguise)) {
						DisguiseEvent event = new DisguiseEvent(player, disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							sender.sendMessage(ChatColor.RED + "Some plugin denies you to disguise.");
						} else {
							DisguiseManager.instance.disguise(player, disguise);
							sender.sendMessage(ChatColor.GOLD + "You disguised as a player called " + ChatColor.ITALIC + args[1]);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You are not allowed to disguise.");
					}
				}
			} else if(StringUtil.equalsIgnoreCase(args[0], "ghost", "g")) {
				if(!getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
					sender.sendMessage(ChatColor.RED + "This feature is disabled!");
				} else if(args.length == 1) {
					if(DisguiseManager.instance.isDisguised(player) && (DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
						PlayerDisguise disguise = new PlayerDisguise(((PlayerDisguise)DisguiseManager.instance.getDisguise(player)).getName(), true);
						if(hasPermission(player, disguise)) {
							DisguiseEvent event = new DisguiseEvent(player, disguise);
							getServer().getPluginManager().callEvent(event);
							if(event.isCancelled()) {
								sender.sendMessage(ChatColor.RED + "Some plugin denies you to disguise.");
							} else {
								DisguiseManager.instance.disguise(player, disguise);
								sender.sendMessage(ChatColor.GOLD + "You disguised as a ghost called " + ChatColor.ITALIC + disguise.getName());
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You are not allowed to disguise.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Wrong usage: " + ChatColor.ITALIC + "/" + cmd.getName() + " " + args[0] + " <name>");
					}
				} else if(!Validate.minecraftUsername(args[1])) {
					sender.sendMessage(ChatColor.RED + "The given username is invalid.");
				} else {
					PlayerDisguise disguise = new PlayerDisguise(args[1], true);
					if(hasPermission(player, disguise)) {
						DisguiseEvent event = new DisguiseEvent(player, disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							sender.sendMessage(ChatColor.RED + "Some plugin denies you to disguise.");
						} else {
							DisguiseManager.instance.disguise(player, disguise);
							sender.sendMessage(ChatColor.GOLD + "You disguised as a ghost called " + ChatColor.ITALIC + args[1]);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You are not allowed to disguise.");
					}
				}
			} else if(StringUtil.equalsIgnoreCase(args[0], "status", "state", "stat", "stats")) {
				if(DisguiseManager.instance.isDisguised(player)) {
					Disguise disguise = DisguiseManager.instance.getDisguise(player);
					if(disguise instanceof PlayerDisguise) {
						sender.sendMessage(ChatColor.GOLD + "You are disguised as a " + (((PlayerDisguise)disguise).isGhost() ? "ghost" : "player") + " called " + ChatColor.ITALIC + ((PlayerDisguise)disguise).getName());
					} else if(disguise instanceof MobDisguise) {
						sender.sendMessage(ChatColor.GOLD + "You are disguised as a " + disguise.getType().name().toLowerCase(Locale.ENGLISH));
						sender.sendMessage(ChatColor.GRAY + "Your subtypes:");
						sender.sendMessage(ChatColor.GRAY + " Age: " + (((MobDisguise)disguise).isAdult() ? "adult" : "baby"));
						switch(disguise.getType()) {
							case CREEPER:
								sender.sendMessage(ChatColor.GRAY + " Creeper: " + (((CreeperDisguise)disguise).isPowered() ? "powered" : "not-powered"));
								break;
							case ENDERMAN:
								sender.sendMessage(ChatColor.GRAY + " Block in Hand: " + ((EndermanDisguise)disguise).getBlockInHand().name().toLowerCase(Locale.ENGLISH));
								sender.sendMessage(ChatColor.GRAY + " Data: " + ((EndermanDisguise)disguise).getBlockInHandData());
								break;
							case GUARDIAN:
								sender.sendMessage(ChatColor.GRAY + " Guardian type: " + (((GuardianDisguise)disguise).isElder() ? "elder" : "not-elder"));
								break;
							case HORSE:
								sender.sendMessage(ChatColor.GRAY + " Variant: " + ((HorseDisguise)disguise).getVariant().name().toLowerCase(Locale.ENGLISH).replace("_horse", "").replace("horse", "normal").replace("skeleton", "skeletal"));
								sender.sendMessage(ChatColor.GRAY + " Style: " + ((HorseDisguise)disguise).getStyle().name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings"));
								sender.sendMessage(ChatColor.GRAY + " Color: " + ((HorseDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								sender.sendMessage(ChatColor.GRAY + " Saddle: " + (((HorseDisguise)disguise).isSaddled() ? "saddled" : "not-saddled"));
								sender.sendMessage(ChatColor.GRAY + " Chest: " + (((HorseDisguise)disguise).hasChest() ? "chest" : "no-chest"));
								sender.sendMessage(ChatColor.GRAY + " Armor: " + ((HorseDisguise)disguise).getArmor().name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor"));
								break;
							case OCELOT:
								sender.sendMessage(ChatColor.GRAY + " Cat type: " + ((OcelotDisguise)disguise).getCatType().name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""));
								break;
							case PIG:
								sender.sendMessage(ChatColor.GRAY + " Saddle: " + (((PigDisguise)disguise).isSaddled() ? "saddled" : "not-saddled"));
								break;
							case RABBIT:
								sender.sendMessage(ChatColor.GRAY + " Rabbit type: " + ((RabbitDisguise)disguise).getRabbitType().name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer"));
								break;
							case SHEEP:
								sender.sendMessage(ChatColor.GRAY + " Color: " + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								break;
							case SKELETON:
								sender.sendMessage(ChatColor.GRAY + " Skeleton type: " + (((SkeletonDisguise)disguise).getSkeletonType().equals(SkeletonType.NORMAL) ? "normal" : "wither"));
								break;
							case MAGMA_CUBE:
							case SLIME:
								sender.sendMessage(ChatColor.GRAY + " Size: " + ((SizedDisguise)disguise).getSize() + (((SizedDisguise)disguise).getSize() == 1 ? " (tiny)" : (((SizedDisguise)disguise).getSize() == 2 ? " (normal)" : (((SizedDisguise)disguise).getSize() == 4 ? " (big)" : ""))));
								break;
							case VILLAGER:
								sender.sendMessage(ChatColor.GRAY + " Profession: " + ((VillagerDisguise)disguise).getProfession().name().toLowerCase(Locale.ENGLISH));
								break;
							case WOLF:
								sender.sendMessage(ChatColor.GRAY + " Collar: " + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								sender.sendMessage(ChatColor.GRAY + " Tamed: " + (((WolfDisguise)disguise).isTamed() ? "tamed" : "not-tamed"));
								sender.sendMessage(ChatColor.GRAY + " Angry: " + (((WolfDisguise)disguise).isAngry() ? "angry" : "not-angry"));
								break;
							case ZOMBIE:
								sender.sendMessage(ChatColor.GRAY + " Zombie type: " + (((ZombieDisguise)disguise).isVillager() ? "infected" : "normal"));
								break;
							default: break;
						}
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + "You are not disguised.");
				}
			} else if(args[0].equalsIgnoreCase("random")) {
				if(player.hasPermission("iDisguise.random")) {
					DisguiseType type = RandomUtil.nextBoolean() ? DisguiseType.random(Type.MOB) : DisguiseType.random(Type.OBJECT);
					Disguise disguise;
					switch(type) {
						case CREEPER:
							disguise = new CreeperDisguise(RandomUtil.nextBoolean());
							break;
						case ENDERMAN:
							disguise = new EndermanDisguise(Material.getMaterial(RandomUtil.nextInt(198)), RandomUtil.nextInt(16));
							break;
						case GUARDIAN:
							disguise = new GuardianDisguise(RandomUtil.nextBoolean());
							break;
						case HORSE:
							disguise = new HorseDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Variant.class), RandomUtil.nextEnumValue(Style.class), RandomUtil.nextEnumValue(Color.class), RandomUtil.nextBoolean(), RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Armor.class));
							break;
						case OCELOT:
							disguise = new OcelotDisguise(RandomUtil.nextEnumValue(Ocelot.Type.class), RandomUtil.nextBoolean());
							break;
						case PIG:
							disguise = new PigDisguise(RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
							break;
						case RABBIT:
							disguise = new RabbitDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(RabbitType.class));
							break;
						case SHEEP:
							disguise = new ColoredDisguise(type, RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(DyeColor.class));
							break;
						case SKELETON:
							disguise = new SkeletonDisguise(RandomUtil.nextEnumValue(SkeletonType.class));
							break;
						case MAGMA_CUBE:
						case SLIME:
							disguise = new SizedDisguise(type, RandomUtil.nextInt(1000) + 1);
							break;
						case VILLAGER:
							disguise = new VillagerDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Profession.class));
							break;
						case WOLF:
							disguise = new WolfDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(DyeColor.class), RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
							break;
						case ZOMBIE:
							disguise = new ZombieDisguise(RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
							break;
						case BOAT:
						case ENDER_CRYSTAL:
							disguise = new ObjectDisguise(type);
							break;
						case FALLING_BLOCK:
							disguise = new FallingBlockDisguise(Material.getMaterial(RandomUtil.nextInt(95) + 1));
							break;
						case ITEM:
							disguise = new ItemDisguise(new ItemStack(RandomUtil.nextEnumValue(Material.class), RandomUtil.nextInt(64) + 1));
							break;
						case MINECART:
							disguise = new MinecartDisguise(Material.getMaterial(RandomUtil.nextInt(95) + 1));
							break;
						default:
							disguise = new MobDisguise(type, RandomUtil.nextBoolean());
							break;
					}
					DisguiseEvent event = new DisguiseEvent(player, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						sender.sendMessage(ChatColor.RED + "Some plugin denies you to disguise.");
					} else {
						DisguiseManager.instance.disguise(player, disguise);
						sender.sendMessage(ChatColor.GOLD + "You disguised as a random mob. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You are not allowed to disguise.");
				}
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(player.hasPermission("iDisguise.reload")) {
					onReload();
					sender.sendMessage(ChatColor.GOLD + "Reloaded config file.");
				} else {
					sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
				}
			} else {
				Disguise disguise = DisguiseManager.instance.isDisguised(player) ? DisguiseManager.instance.getDisguise(player).clone() : null;
				boolean changed = false;
				for(String argument : args) {
					DisguiseType type = DisguiseType.Matcher.match(argument.toLowerCase(Locale.ENGLISH));
					if(type != null) {
						disguise = type.newInstance();
						if(disguise == null) {
							
						} else {
							changed = true;
							break;
						}
					}
				}
				if(disguise != null) {
					for(String argument : args) {
						changed |= disguise.applySubtype(argument);
					}
				}
				if(changed) {
					if(hasPermission(player, disguise)) {
						DisguiseEvent event = new DisguiseEvent(player, disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							sender.sendMessage(ChatColor.RED + "Some plugin denies you to disguise.");
						} else {
							DisguiseManager.instance.disguise(player, disguise);
							sender.sendMessage(ChatColor.GOLD + "You disguised. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " status" + ChatColor.RESET + ChatColor.GOLD + " for information about your disguise.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You are not allowed to disguise.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Wrong usage. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " help" + ChatColor.RESET + ChatColor.RED + " for additional information.");
				}
			}
			return true;
		} else if(StringUtil.equalsIgnoreCase(cmd.getName(), "ud", "undis", "undisguise")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			}
			if(args.length == 0) {
				if(player == null) {
					sender.sendMessage(ChatColor.RED + "You cannot undisguise as console.");
				} else {
					if(DisguiseManager.instance.isDisguised(player)) {
						if(!getConfiguration().getBoolean(Configuration.UNDISGUISE_PERMISSION) || player.hasPermission("iDisguise.undisguise")) {
							UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.instance.getDisguise(player), false);
							getServer().getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								DisguiseManager.instance.undisguise(player);
								sender.sendMessage(ChatColor.GOLD + "You were undisguised.");
							} else {
								sender.sendMessage(ChatColor.RED + "Some plugin denies you to undisguise.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You are not allowed to undisguise.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You are not disguised.");
					}
				}
			} else if(args[0].equals("*")) {
				if(player == null || player.hasPermission("iDisguise.undisguise.all")) {
					if(args.length > 1 && args[1].equalsIgnoreCase("ignore")) {
						DisguiseManager.instance.undisguiseAll();
						sender.sendMessage(ChatColor.GOLD + "Undisguised everyone ignoring event cancelling.");
					} else {
						int count = 0;
						int total = DisguiseManager.instance.getDisguisedPlayers().size();
						for(OfflinePlayer offlinePlayer : DisguiseManager.instance.getDisguisedPlayers()) {
							if(offlinePlayer.isOnline()) {
								UndisguiseEvent event = new UndisguiseEvent(offlinePlayer.getPlayer(), DisguiseManager.instance.getDisguise(offlinePlayer.getPlayer()), true);
								getServer().getPluginManager().callEvent(event);
								if(!event.isCancelled()) {
									DisguiseManager.instance.undisguise(offlinePlayer.getPlayer());
									count++;
								}
							} else {
								DisguiseManager.instance.removeDisguise(offlinePlayer);
								count++;
							}
						}
						sender.sendMessage(ChatColor.GOLD + Integer.toString(count) + " of " + total + " disguised players were undisguised.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You are not allowed to undisguise everyone.");
				}
			} else {
				if(player == null || player.hasPermission("iDisguise.undisguise.others")) {
					if(getServer().matchPlayer(args[0]).isEmpty()) {
						sender.sendMessage(ChatColor.RED + "Cannot find player " + ChatColor.ITALIC + args[0]);
					} else {
						player = getServer().matchPlayer(args[0]).get(0);
						if(DisguiseManager.instance.isDisguised(player)) {
							if(args.length > 1 && args[1].equalsIgnoreCase("ignore")) {
								DisguiseManager.instance.undisguise(player);
								sender.sendMessage(ChatColor.GOLD + "Undisguised " + ChatColor.ITALIC + player.getName());
							} else {
								UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.instance.getDisguise(player), false);
								getServer().getPluginManager().callEvent(event);
								if(!event.isCancelled()) {
									DisguiseManager.instance.undisguise(player);
									sender.sendMessage(ChatColor.GOLD + "Undisguised " + ChatColor.ITALIC + player.getName());
								} else {
									sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to undisguise.");
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " is not disguised.");
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You are not allowed to undisguise other players.");
				}
			}
		} else if(StringUtil.equalsIgnoreCase(cmd.getName(), "od", "odis", "odisguise")) {
			if(sender instanceof Player && !((Player)sender).hasPermission("iDisguise.others")) {
				sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
				return true;
			}
			if(args.length < 2) {
				sender.sendMessage(ChatColor.GREEN + getFullName() + " - Help");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> help - Shows this");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> player <name> - Disguise as a player");
				if(getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> ghost <name> - Disguise as a ghost player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> random - Disguise as a random mob");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> status - Shows what the player is currently disguised as");
				if(!(sender instanceof Player) || ((Player)sender).hasPermission("iDisguise.undisguise.all")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("od") ? "" : "n") + cmd.getName().substring(1) + " * - Undisguise everyone");
				}
				if(!(sender instanceof Player) || ((Player)sender).hasPermission("iDisguise.undisguise.others")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("od") ? "" : "n") + cmd.getName().substring(1) + " <player> - Undisguise another player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> [subtype] <mobtype> [subtype] - Disguise as a mob with optional subtypes");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> <subtype> - Change the current subtypes");
				sender.sendMessage(ChatColor.GOLD + "Mobtypes:");
				sender.sendMessage(ChatColor.GRAY + " bat, blaze, cave_spider, chicken, cow, creeper, ender_dragon, enderman, endermite, ghast, giant, guardian, horse, iron_golem, magma_cube, mushroom_cow, ocelot, pig, pig_zombie, rabbit, sheep, silverfish, skeleton, slime, snowman, spider, squid, villager, witch, witherboss, wolf, zombie");
				return true;
			} else {
				if(getServer().matchPlayer(args[0]).isEmpty()) {
					sender.sendMessage(ChatColor.RED + "Cannot find player " + ChatColor.ITALIC + args[0]);
					return true;
				} else {
					player = getServer().matchPlayer(args[0]).get(0);
				}
			}
			if(!isDisguisingPermittedInWorld(player.getWorld()) && sender instanceof Player && !((Player)sender).hasPermission("iDisguise.everywhere")) {
				sender.sendMessage(ChatColor.RED + "Using this plugin is prohibited in this world.");
			}
			if(StringUtil.equalsIgnoreCase(args[1], "?", "help")) {
				sender.sendMessage(ChatColor.GREEN + getFullName() + " - Help");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> help - Shows this");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> player <name> - Disguise as a player");
				if(getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> ghost <name> - Disguise as a ghost player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> random - Disguise as a random mob");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> status - Shows what the player is currently disguised as");
				if(!(sender instanceof Player) || ((Player)sender).hasPermission("iDisguise.undisguise.all")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("od") ? "" : "n") + cmd.getName().substring(1) + " * - Undisguise everyone");
				}
				if(!(sender instanceof Player) || ((Player)sender).hasPermission("iDisguise.undisguise.others")) {
					sender.sendMessage(ChatColor.GOLD + "/u" + (cmd.getName().equalsIgnoreCase("od") ? "" : "n") + cmd.getName().substring(1) + " <player> - Undisguise another player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> [subtype] <mobtype> [subtype] - Disguise as a mob with optional subtypes");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " <player> <subtype> - Change the current subtypes");
				sender.sendMessage(ChatColor.GOLD + "Mobtypes:");
				sender.sendMessage(ChatColor.GRAY + " bat, blaze, cave_spider, chicken, cow, creeper, ender_dragon, enderman, endermite, ghast, giant, guardian, horse, iron_golem, magma_cube, mushroom_cow, ocelot, pig, pig_zombie, rabbit, sheep, silverfish, skeleton, slime, snowman, spider, squid, villager, witch, witherboss, wolf, zombie");
				if(DisguiseManager.instance.isDisguised(player)) {
					sendSubtypeInformation(sender, DisguiseManager.instance.getDisguise(player).getType());
				}
			} else if(StringUtil.equalsIgnoreCase(args[1], "player", "p")) {
				if(args.length == 2) {
					sender.sendMessage(ChatColor.RED + "Wrong usage: " + ChatColor.ITALIC + "/" + cmd.getName() + " " + player.getName() + " " + args[1] + " <name>");
				} else if(!Validate.minecraftUsername(args[2])) {
					sender.sendMessage(ChatColor.RED + "The given username is invalid.");
				} else {
					PlayerDisguise disguise = new PlayerDisguise(args[2], false);
					DisguiseEvent event = new DisguiseEvent(player, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to disguise.");
					} else {
						DisguiseManager.instance.disguise(player, disguise);
						sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " disguised as a player called " + ChatColor.ITALIC + args[2]);
					}
				}
			} else if(StringUtil.equalsIgnoreCase(args[1], "ghost", "g")) {
				if(!getConfiguration().getBoolean(Configuration.GHOST_DISGUISES)) {
					sender.sendMessage(ChatColor.RED + "This feature is disabled!");
				} else if(args.length == 2) {
					if(DisguiseManager.instance.isDisguised(player) && (DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
						PlayerDisguise disguise = new PlayerDisguise(((PlayerDisguise)DisguiseManager.instance.getDisguise(player)).getName(), true);
						DisguiseEvent event = new DisguiseEvent(player, disguise);
						getServer().getPluginManager().callEvent(event);
						if(event.isCancelled()) {
							sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to disguise.");
						} else {
							DisguiseManager.instance.disguise(player, disguise);
							sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " disguised as a ghost called " + ChatColor.ITALIC + disguise.getName());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Wrong usage: " + ChatColor.ITALIC + "/" + cmd.getName() + " " + player.getName() + " " + args[1] + " <name>");
					}
				} else if(!Validate.minecraftUsername(args[2])) {
					sender.sendMessage(ChatColor.RED + "The given username is invalid.");
				} else {
					PlayerDisguise disguise = new PlayerDisguise(args[2], true);
					DisguiseEvent event = new DisguiseEvent(player, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to disguise.");
					} else {
						DisguiseManager.instance.disguise(player, disguise);
						sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " disguised as a ghost called " + ChatColor.ITALIC + args[2]);
					}
				}
			} else if(StringUtil.equalsIgnoreCase(args[1], "status", "state", "stat", "stats")) {
				if(DisguiseManager.instance.isDisguised(player)) {
					Disguise disguise = DisguiseManager.instance.getDisguise(player);
					if(disguise instanceof PlayerDisguise) {
						sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " is disguised as a " + (((PlayerDisguise)disguise).isGhost() ? "ghost" : "player") + " called " + ChatColor.ITALIC + ((PlayerDisguise)disguise).getName());
					} else if(disguise instanceof MobDisguise) {
						sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " is disguised as a " + disguise.getType().name().toLowerCase(Locale.ENGLISH));
						sender.sendMessage(ChatColor.GRAY + "Subtypes:");
						sender.sendMessage(ChatColor.GRAY + " Age: " + (((MobDisguise)disguise).isAdult() ? "adult" : "baby"));
						switch(disguise.getType()) {
							case CREEPER:
								sender.sendMessage(ChatColor.GRAY + " Creeper: " + (((CreeperDisguise)disguise).isPowered() ? "powered" : "not-powered"));
								break;
							case ENDERMAN:
								sender.sendMessage(ChatColor.GRAY + " Block in Hand: " + ((EndermanDisguise)disguise).getBlockInHand().name().toLowerCase(Locale.ENGLISH));
								sender.sendMessage(ChatColor.GRAY + " Data: " + ((EndermanDisguise)disguise).getBlockInHandData());
								break;
							case GUARDIAN:
								sender.sendMessage(ChatColor.GRAY + " Guardian type: " + (((GuardianDisguise)disguise).isElder() ? "elder" : "not-elder"));
								break;
							case HORSE:
								sender.sendMessage(ChatColor.GRAY + " Variant: " + ((HorseDisguise)disguise).getVariant().name().toLowerCase(Locale.ENGLISH).replace("_horse", "").replace("horse", "normal").replace("skeleton", "skeletal"));
								sender.sendMessage(ChatColor.GRAY + " Style: " + ((HorseDisguise)disguise).getStyle().name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings"));
								sender.sendMessage(ChatColor.GRAY + " Color: " + ((HorseDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								sender.sendMessage(ChatColor.GRAY + " Saddle: " + (((HorseDisguise)disguise).isSaddled() ? "saddled" : "not-saddled"));
								sender.sendMessage(ChatColor.GRAY + " Chest: " + (((HorseDisguise)disguise).hasChest() ? "chest" : "no-chest"));
								sender.sendMessage(ChatColor.GRAY + " Armor: " + ((HorseDisguise)disguise).getArmor().name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor"));
								break;
							case OCELOT:
								sender.sendMessage(ChatColor.GRAY + " Cat type: " + ((OcelotDisguise)disguise).getCatType().name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""));
								break;
							case PIG:
								sender.sendMessage(ChatColor.GRAY + " Saddle: " + (((PigDisguise)disguise).isSaddled() ? "saddled" : "not-saddled"));
								break;
							case RABBIT:
								sender.sendMessage(ChatColor.GRAY + " Rabbit type: " + ((RabbitDisguise)disguise).getRabbitType().name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer"));
								break;
							case SHEEP:
								sender.sendMessage(ChatColor.GRAY + " Color: " + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								break;
							case SKELETON:
								sender.sendMessage(ChatColor.GRAY + " Skeleton type: " + (((SkeletonDisguise)disguise).getSkeletonType().equals(SkeletonType.NORMAL) ? "normal" : "wither"));
								break;
							case MAGMA_CUBE:
							case SLIME:
								sender.sendMessage(ChatColor.GRAY + " Size: " + ((SizedDisguise)disguise).getSize() + (((SizedDisguise)disguise).getSize() == 1 ? " (tiny)" : (((SizedDisguise)disguise).getSize() == 2 ? " (normal)" : (((SizedDisguise)disguise).getSize() == 4 ? " (big)" : ""))));
								break;
							case VILLAGER:
								sender.sendMessage(ChatColor.GRAY + " Profession: " + ((VillagerDisguise)disguise).getProfession().name().toLowerCase(Locale.ENGLISH));
								break;
							case WOLF:
								sender.sendMessage(ChatColor.GRAY + " Collar: " + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
								sender.sendMessage(ChatColor.GRAY + " Tamed: " + (((WolfDisguise)disguise).isTamed() ? "tamed" : "not-tamed"));
								sender.sendMessage(ChatColor.GRAY + " Angry: " + (((WolfDisguise)disguise).isAngry() ? "angry" : "not-angry"));
								break;
							case ZOMBIE:
								sender.sendMessage(ChatColor.GRAY + " Zombie type: " + (((ZombieDisguise)disguise).isVillager() ? "infected" : "normal"));
								break;
							default: break;
						}
					}
				} else {
					sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " is not disguised.");
				}
			} else if(args[1].equalsIgnoreCase("random")) {
				DisguiseType type = RandomUtil.nextBoolean() ? DisguiseType.random(Type.MOB) : DisguiseType.random(Type.OBJECT);
				Disguise disguise;
				switch(type) {
					case CREEPER:
						disguise = new CreeperDisguise(RandomUtil.nextBoolean());
						break;
					case ENDERMAN:
						disguise = new EndermanDisguise(Material.getMaterial(RandomUtil.nextInt(198)), RandomUtil.nextInt(16));
						break;
					case GUARDIAN:
						disguise = new GuardianDisguise(RandomUtil.nextBoolean());
						break;
					case HORSE:
						disguise = new HorseDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Variant.class), RandomUtil.nextEnumValue(Style.class), RandomUtil.nextEnumValue(Color.class), RandomUtil.nextBoolean(), RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Armor.class));
						break;
					case OCELOT:
						disguise = new OcelotDisguise(RandomUtil.nextEnumValue(Ocelot.Type.class), RandomUtil.nextBoolean());
						break;
					case PIG:
						disguise = new PigDisguise(RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
						break;
					case RABBIT:
						disguise = new RabbitDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(RabbitType.class));
						break;
					case SHEEP:
						disguise = new ColoredDisguise(type, RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(DyeColor.class));
						break;
					case SKELETON:
						disguise = new SkeletonDisguise(RandomUtil.nextEnumValue(SkeletonType.class));
						break;
					case MAGMA_CUBE:
					case SLIME:
						disguise = new SizedDisguise(type, RandomUtil.nextInt(1000) + 1);
						break;
					case VILLAGER:
						disguise = new VillagerDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(Profession.class));
						break;
					case WOLF:
						disguise = new WolfDisguise(RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(DyeColor.class), RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
						break;
					case ZOMBIE:
						disguise = new ZombieDisguise(RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
						break;
					case BOAT:
					case ENDER_CRYSTAL:
						disguise = new ObjectDisguise(type);
						break;
					case FALLING_BLOCK:
						disguise = new FallingBlockDisguise(Material.getMaterial(RandomUtil.nextInt(95) + 1));
						break;
					case ITEM:
						disguise = new ItemDisguise(new ItemStack(RandomUtil.nextEnumValue(Material.class), RandomUtil.nextInt(64) + 1));
						break;
					case MINECART:
						disguise = new MinecartDisguise(Material.getMaterial(RandomUtil.nextInt(95) + 1));
						break;
					default:
						disguise = new MobDisguise(type, RandomUtil.nextBoolean());
						break;
				}
				DisguiseEvent event = new DisguiseEvent(player, disguise);
				getServer().getPluginManager().callEvent(event);
				if(event.isCancelled()) {
					sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to disguise.");
				} else {
					DisguiseManager.instance.disguise(player, disguise);
					sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " disguised as a random mob. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " " + player.getName() + " status" + ChatColor.RESET + ChatColor.GOLD + " to get more information.");
				}
			} else {
				Disguise disguise = DisguiseManager.instance.isDisguised(player) ? DisguiseManager.instance.getDisguise(player).clone() : null;
				boolean changed = false;
				for(String argument : Arrays.copyOfRange(args, 1, args.length)) {
					DisguiseType type = DisguiseType.Matcher.match(argument.toLowerCase(Locale.ENGLISH));
					if(type != null) {
						disguise = type.newInstance();
						if(disguise == null) {
							
						} else {
							changed = true;
							break;
						}
					}
				}
				if(disguise != null) {
					for(String argument : args) {
						changed |= disguise.applySubtype(argument);
					}
				}
				if(changed) {
					DisguiseEvent event = new DisguiseEvent(player, disguise);
					getServer().getPluginManager().callEvent(event);
					if(event.isCancelled()) {
						sender.sendMessage(ChatColor.RED + "Some plugin denies " + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.RED + " to disguise.");
					} else {
						DisguiseManager.instance.disguise(player, disguise);
						sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + player.getName() + ChatColor.RESET + ChatColor.GOLD + " disguised. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " " + player.getName() + " status" + ChatColor.RESET + ChatColor.GOLD + " for information about the disguise.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Wrong usage. Type " + ChatColor.ITALIC + "/" + cmd.getName() + " " + player.getName() + " help" + ChatColor.RESET + ChatColor.RED + " for additional information.");
				}
			}
			return true;
		}
		return true;
	}
	
	private void sendSubtypeInformation(CommandSender sender, DisguiseType type) {
		sender.sendMessage(ChatColor.GOLD + "Information about subtypes:");
		sender.sendMessage(ChatColor.GRAY + " Age: adult, baby");
		switch(type) {
			case CREEPER:
				sender.sendMessage(ChatColor.GRAY + " Creeper: powered, not-powered");
				break;
			case ENDERMAN:
				sender.sendMessage(ChatColor.GRAY + " Block in hand: <material-name>");
				sender.sendMessage(ChatColor.GRAY + " Data: <0-255>");
				break;
			case GUARDIAN:
				sender.sendMessage(ChatColor.GRAY + " Guardian type: elder, not-elder");
				break;
			case HORSE:
				sender.sendMessage(ChatColor.GRAY + " Variant: donkey, normal, mule, skeletal, undead");
				sender.sendMessage(ChatColor.GRAY + " Style: black-dots, no-markings, white-stripes, white-dots, whitefield");
				sender.sendMessage(ChatColor.GRAY + " Color: black, brown, chestnut, creamy, dark-brown, gray, white");
				sender.sendMessage(ChatColor.GRAY + " Saddle: saddled, not-saddled");
				sender.sendMessage(ChatColor.GRAY + " Chest: chest, no-chest");
				sender.sendMessage(ChatColor.GRAY + " Armor: no-armor, iron, gold, diamond");
				break;
			case OCELOT:
				sender.sendMessage(ChatColor.GRAY + " Cat type: black, red, siamese, wild");
				break;
			case PIG:
				sender.sendMessage(ChatColor.GRAY + " Saddle: saddled, not-saddled");
				break;
			case RABBIT:
				sender.sendMessage(ChatColor.GRAY + " Rabbit type: black, black-white, brown, gold, salt-pepper, killer, white");
				break;
			case SHEEP:
				sender.sendMessage(ChatColor.GRAY + " Color: black, blue, brown, cyan, gray, green, light-blue, lime, magenta, orange, pink, purple, red, silver, white, yellow");
				break;
			case SKELETON:
				sender.sendMessage(ChatColor.GRAY + " Skeleton type: normal, wither");
				break;
			case MAGMA_CUBE:
			case SLIME:
				sender.sendMessage(ChatColor.GRAY + " Size: tiny, normal, big, <1-1000>");
				break;
			case VILLAGER:
				sender.sendMessage(ChatColor.GRAY + " Profession: blacksmith, butcher, farmer, librarian, priest");
				break;
			case WOLF:
				sender.sendMessage(ChatColor.GRAY + " Collar: black, blue, brown, cyan, gray, green, light-blue, lime, magenta, orange, pink, purple, red, silver, white, yellow");
				sender.sendMessage(ChatColor.GRAY + " Tamed: tamed, not-tamed");
				sender.sendMessage(ChatColor.GRAY + " Angry: angry, not-angry");
				break;
			case ZOMBIE:
				sender.sendMessage(ChatColor.GRAY + " Zombie type: normal, infected");
				break;
			default: break;
		}
	}
	
	private boolean hasPermission(Player player, Disguise disguise) {
		switch(disguise.getType()) {
			case BAT:
				return player.hasPermission("iDisguise.mob.bat");
			case BLAZE:
				return player.hasPermission("iDisguise.mob.blaze");
			case CAVE_SPIDER:
				return player.hasPermission("iDisguise.mob.cave_spider");
			case CHICKEN:
				return player.hasPermission("iDisguise.mob.chicken") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby"));
			case COW:
				return player.hasPermission("iDisguise.mob.cow") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby"));
			case CREEPER:
				return player.hasPermission("iDisguise.mob.creeper") && (!((CreeperDisguise)disguise).isPowered() || player.hasPermission("iDisguise.mob.creeper.powered"));
			case ENDER_DRAGON:
				return player.hasPermission("iDisguise.mob.ender_dragon");
			case ENDERMAN:
				return player.hasPermission("iDisguise.mob.enderman") && (((EndermanDisguise)disguise).getBlockInHand().equals(Material.AIR) || player.hasPermission("iDisguise.mob.enderman.block"));
			case ENDERMITE:
				return player.hasPermission("iDisguise.mob.endermite");
			case GHAST:
				return player.hasPermission("iDisguise.mob.ghast");
			case GHOST:
				return player.hasPermission("iDisguise.ghost") && (player.hasPermission("iDisguise.player.name.*") || player.hasPermission("iDisguise.player.name." + ((PlayerDisguise)disguise).getName().toLowerCase(Locale.ENGLISH))) && (isPlayerDisguisePermitted(((PlayerDisguise)disguise).getName().toLowerCase(Locale.ENGLISH)) || player.hasPermission("iDisguise.player.prohibited"));
			case GIANT:
				return player.hasPermission("iDisguise.mob.giant");
			case GUARDIAN:
				return player.hasPermission("iDisguise.mob.guardian") && (!((GuardianDisguise)disguise).isElder() || player.hasPermission("iDisguise.mob.guardian.elder"));
			case HORSE:
				return player.hasPermission("iDisguise.mob.horse") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.horse.variant." + ((HorseDisguise)disguise).getVariant().name().toLowerCase(Locale.ENGLISH).replace("_horse", "").replace("horse", "normal").replace("skeleton", "skeletal"));
			case IRON_GOLEM:
				return player.hasPermission("iDisguise.mob.iron_golem");
			case MAGMA_CUBE:
				return player.hasPermission("iDisguise.mob.magma_cube") && (((SizedDisguise)disguise).getSize() < 5 || player.hasPermission("iDisguise.mob.magma_cube.giant"));
			case MUSHROOM_COW:
				return player.hasPermission("iDisguise.mob.mushroom_cow") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby"));
			case OCELOT:
				return player.hasPermission("iDisguise.mob.ocelot") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.ocelot.type." + ((OcelotDisguise)disguise).getCatType().name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""));
			case PIG:
				return player.hasPermission("iDisguise.mob.pig") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && (!((PigDisguise)disguise).isSaddled() || player.hasPermission("iDisguise.mob.pig.saddled"));
			case PIG_ZOMBIE:
				return player.hasPermission("iDisguise.mob.pig_zombie") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby"));
			case PLAYER:
				return (player.hasPermission("iDisguise.player.name.*") || player.hasPermission("iDisguise.player.name." + ((PlayerDisguise)disguise).getName().toLowerCase(Locale.ENGLISH))) && (isPlayerDisguisePermitted(((PlayerDisguise)disguise).getName().toLowerCase(Locale.ENGLISH)) || player.hasPermission("iDisguise.player.prohibited"));
			case RABBIT:
				return player.hasPermission("iDisguise.mob.rabbit") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.rabbit.type." + ((RabbitDisguise)disguise).getRabbitType().name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer"));
			case SHEEP:
				return player.hasPermission("iDisguise.mob.sheep") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.sheep.color." + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			case SILVERFISH:
				return player.hasPermission("iDisguise.mob.silverfish");
			case SKELETON:
				return player.hasPermission("iDisguise.mob.skeleton") && (((SkeletonDisguise)disguise).getSkeletonType().equals(SkeletonType.NORMAL) || player.hasPermission("iDisguise.mob.skeleton.wither"));
			case SLIME:
				return player.hasPermission("iDisguise.mob.slime") && (((SizedDisguise)disguise).getSize() < 5 || player.hasPermission("iDisguise.mob.slime.giant"));
			case SNOWMAN:
				return player.hasPermission("iDisguise.mob.snowman");
			case SPIDER:
				return player.hasPermission("iDisguise.mob.spider");
			case SQUID:
				return player.hasPermission("iDisguise.mob.squid");
			case VILLAGER:
				return player.hasPermission("iDisguise.mob.villager") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.villager.profession." + ((VillagerDisguise)disguise).getProfession().name().toLowerCase(Locale.ENGLISH));
			case WITCH:
				return player.hasPermission("iDisguise.mob.witch");
			case WITHER:
				return player.hasPermission("iDisguise.mob.witherboss");
			case WOLF:
				return player.hasPermission("iDisguise.mob.wolf") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && player.hasPermission("iDisguise.mob.wolf.collar." + ((ColoredDisguise)disguise).getColor().name().toLowerCase(Locale.ENGLISH).replace('_', '-')) && (!((WolfDisguise)disguise).isTamed() || player.hasPermission("iDisguise.mob.wolf.tamed")) && (!((WolfDisguise)disguise).isAngry() || player.hasPermission("iDisguise.mob.wolf.angry"));
			case ZOMBIE:
				return player.hasPermission("iDisguise.mob.zombie") && (((MobDisguise)disguise).isAdult() || player.hasPermission("iDisguise.mob.baby")) && (!((ZombieDisguise)disguise).isVillager() || player.hasPermission("iDisguise.mob.zombie.infected"));
			case BOAT:
			case ENDER_CRYSTAL:
			case FALLING_BLOCK:
			case ITEM:
			case MINECART:
				return true;
			default:
				return false;
		}
	}
	
	private void checkDirectory() {
		if(!directory.exists()) {
			directory.mkdir();
		}
	}
	
	private void loadData() {
		File dataFile = new File(directory, "data.bin");
		File oldDataFile = new File(directory, "disguise.bin");
		if(dataFile.exists()) {
			Object map = SLAPI.load(dataFile);
			if(map instanceof Map) {
				DisguiseManager.instance.updateDisguises((Map)map);
			}
		} else if(oldDataFile.exists()) {
			Object map = SLAPI.load(oldDataFile);
			if(map instanceof Map) {
				DisguiseManager.instance.updateDisguises((Map)map);
			}
			oldDataFile.delete();
		}
	}
	
	private void saveData() {
		File dataFile = new File(directory, "data.bin");
		SLAPI.save(DisguiseManager.instance.getDisguises(), dataFile);
	}
	
	public DisguiseAPI getAPI() {
		return new DisguiseAPI() {
			
			public void disguiseToAll(Player player, Disguise disguise) {
				DisguiseManager.instance.disguise(player, disguise);
			}
			
			public void undisguiseToAll(Player player) {
				DisguiseManager.instance.undisguise(player);
			}
			
			public void undisguiseAll() {
				DisguiseManager.instance.undisguiseAll();
			}
			
			public boolean isDisguised(Player player) {
				return DisguiseManager.instance.isDisguised(player);
			}
			
			public Disguise getDisguise(Player player) {
				return DisguiseManager.instance.getDisguise(player).clone();
			}
			
			public int getOnlineDisguiseCount() {
				return DisguiseManager.instance.getOnlineDisguiseCount();
			}
			
			public String getLocale() {
				return "enUS";
			}
			
			public String getLocalizedPhrase(String name) {
				return null;
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
			
		};
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "iDisguise " + getVersion();
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public boolean isDisguisingPermittedInWorld(World world) {
		return isDisguisingPermittedInWorld(world.getName());
	}
	
	public boolean isDisguisingPermittedInWorld(String world) {
		return !getConfiguration().getStringList(Configuration.PROHIBITED_WORLDS).contains(world);
	}
	
	public boolean isPlayerDisguisePermitted(String name) {
		return !getConfiguration().getStringList(Configuration.PROHIBITED_PLAYERS).contains(name);
	}
	
	public boolean enabled() {
		return enabled;
	}
	
}