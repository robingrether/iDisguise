package de.robingrether.idisguise;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager;
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
import de.robingrether.idisguise.disguise.HorseDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.OcelotDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.SkeletonDisguise;
import de.robingrether.idisguise.disguise.VillagerDisguise;
import de.robingrether.idisguise.disguise.WolfDisguise;
import de.robingrether.idisguise.disguise.ZombieDisguise;
import de.robingrether.idisguise.io.Configuration;
import de.robingrether.idisguise.io.Metrics.Graph;
import de.robingrether.idisguise.io.Metrics.Plotter;
import de.robingrether.idisguise.io.lang.LanguageFile;
import de.robingrether.idisguise.io.Metrics;
import de.robingrether.idisguise.io.SLAPI;
import de.robingrether.idisguise.io.UpdateCheck;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.DisguiseList;
import de.robingrether.idisguise.management.GhostFactory;
import de.robingrether.idisguise.management.ProfileUtil;
import de.robingrether.idisguise.sound.SoundSystem;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.RandomUtil;
import de.robingrether.util.StringUtil;

public class iDisguise extends JavaPlugin {
	
	public static final File directory = new File("plugins/iDisguise");
	
	public iDisguiseListener listener;
	public Configuration configuration;
	@Deprecated
	public LanguageFile lang;
	public Metrics metrics;
	
	public void onEnable() {
		checkDirectory();
		listener = new iDisguiseListener(this);
		configuration = new Configuration(directory);
		configuration.loadData();
		configuration.saveData();
		lang = new LanguageFile(getLocalization());
		DisguiseManager.setAttribute(0, showOriginalPlayerNames());
		SoundSystem.setEnabled(isSoundSystemEnabled());
		try {
			metrics = new Metrics(this);
			Graph graph1 = metrics.createGraph("Disguise Count");
			graph1.addPlotter(new Plotter("Disguise Count") {
				public int getValue() {
					return DisguiseManager.getOnlineDisguiseCount();
				}
			});
			@Deprecated
			Graph graph2 = metrics.createGraph("Language");
			graph2.addPlotter(new Plotter(lang.getLocalization()) {
				public int getValue() {
					return 1;
				}
			});
			Graph graph3 = metrics.createGraph("Sound System");
			graph3.addPlotter(new Plotter(isSoundSystemEnabled() ? "enabled" : "disabled") {
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch(Exception e) {
		}
		if(saveDisguises()) {
			loadData();
		}
		getServer().getPluginManager().registerEvents(listener, this);
		if(isGhostDisguiseEnabled()) {
			GhostFactory.enable(this);
		}
		getServer().getServicesManager().register(DisguiseAPI.class, getAPI(), this, ServicePriority.Normal);
		if(checkForUpdates()) {
			getServer().getScheduler().runTaskLaterAsynchronously(this, new UpdateCheck(getFullName(), getServer().getConsoleSender(), "[iDisguise] " + "An update for iDisguise is available: %s"), 20L);
		}
		System.out.println("[iDisguise] " + String.format("iDisguise v%s enabled!", getVersion()));
	}
	
	public void onDisable() {
		if(isGhostDisguiseEnabled()) {
			GhostFactory.disable();
		}
		getServer().getScheduler().cancelTasks(this);
		if(saveDisguises()) {
			saveData();
		}
		System.out.println("[iDisguise] " + String.format("iDisguise v%s disabled!", getVersion()));
	}
	
	public void onReload() {
		onDisable();
		onEnable();
	}
	
	public boolean onCommandNew(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(StringUtil.equalsIgnoreCase(cmd.getName(), "d", "dis", "disguise")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You have to use '/odisguise' from console.");
				return true;
			}
			if(args.length == 0 || StringUtil.equalsIgnoreCase(args[0], "?", "help")) {
				sender.sendMessage(ChatColor.GREEN + "iDisguise v" + getVersion() + " Help");
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " help - Shows this");
				if(player.hasPermission("iDisguise.player")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " player <name> - Disguise as a player");
				}
				if(isGhostDisguiseEnabled() && player.hasPermission("iDisguise.player.ghost")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " ghost <name> - Disguise as a ghost player");
				}
				if(player.hasPermission("iDisguise.random")) {
					sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " random - Disguise as a random mob");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " status - Shows what you are currently disguised as");
				if(!requirePermissionForUndisguising() || player.hasPermission("iDisguise.undisguise")) {
					sender.sendMessage(ChatColor.GOLD + "/undisguise - Undisguise");
				}
				if(player.hasPermission("iDisguise.undisguise.all")) {
					sender.sendMessage(ChatColor.GOLD + "/undisguise <*/all> - Undisguise everyone");
				}
				if(player.hasPermission("iDisguise.undisguise.others")) {
					sender.sendMessage(ChatColor.GOLD + "/undisguise <name> - Undisguise another player");
				}
				sender.sendMessage(ChatColor.GOLD + "/" + cmd.getName() + " [subtype] <mobtype> [subtype] - Disguise as a mob with optional subtypes");
				if(DisguiseManager.isDisguised(player)) {
					sendHelpMessage(player, DisguiseManager.getDisguise(player).getType());
				}
			}
			// disguise einfügen
			return true;
		} else if(StringUtil.equalsIgnoreCase(cmd.getName(), "ud", "undis", "undisguise")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			}
			if(args.length == 0) {
				if(player == null) {
					sender.sendMessage(ChatColor.RED + "You cannot undisguise as console.");
				} else {
					if(DisguiseManager.isDisguised(player)) {
						UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.getDisguise(player), false);
						getServer().getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							DisguiseManager.undisguiseToAll(player);
							sender.sendMessage(ChatColor.GOLD + "You were undisguised.");
						} else {
							sender.sendMessage(ChatColor.RED + "You are not allowed to undisguise.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You are not disguised.");
					}
				}
			} else if(args[0].equals("*")) {
				if(player == null || player.hasPermission("iDisguise.admin")) {
					if(args.length > 1 && args[1].equalsIgnoreCase("ignore")) {
						DisguiseManager.undisguiseAll();
						sender.sendMessage(ChatColor.GOLD + "Undisguised everyone ignoring event cancelling.");
					} else {
						int count = 0;
						int total = DisguiseManager.getDisguiseList().getPlayers().size();
						for(UUID uuid : DisguiseManager.getDisguiseList().getPlayers()) {
							if(Bukkit.getPlayer(uuid) != null) {
								UndisguiseEvent event = new UndisguiseEvent(Bukkit.getPlayer(uuid), DisguiseManager.getDisguise(Bukkit.getPlayer(uuid)), true);
								getServer().getPluginManager().callEvent(event);
								if(!event.isCancelled()) {
									DisguiseManager.undisguiseToAll(Bukkit.getPlayer(uuid));
									count++;
								}
							} else {
								DisguiseManager.getDisguiseList().removeDisguise(uuid);
								count++;
							}
						}
						sender.sendMessage(ChatColor.GOLD + Integer.toString(count) + " of " + total + " disguised players were undisguised.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You are not allowed to undisguise everyone.");
				}
			} else {
				// add undisguise other players
			}
		} //else if - add /odisguise
		return true;
	}
	
	private void sendHelpMessage(CommandSender sender, DisguiseType type) {
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
				sender.sendMessage(ChatColor.GRAY + " Variant: donkey, normal, mule, skeleton, undead");
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
				sender.sendMessage(ChatColor.GRAY + " Size: small, tiny, medium, normal, big, <1-1000>");
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
				sender.sendMessage(ChatColor.GRAY + " Zombie type: normal, villager");
				break;
			default: break;
		}
	}
	
	@Deprecated
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		if(!StringUtil.equalsIgnoreCase(cmd.getName(), "d", "dis", "disguise", "ud", "undis", "undisguise")) {
			return false;
		}
		if(player == null) {
			sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.console"));
			return true;
		}
		if(StringUtil.equalsIgnoreCase(cmd.getName(), "ud", "undis", "undisguise")) {
			if(args.length > 0 && StringUtil.equalsIgnoreCase(args[0], "all", "*")) {
				if(player.hasPermission("iDisguise.admin")) {
					DisguiseManager.undisguiseAll();
					sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.unall.success"));
				} else {
					sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
				}
			} else {
				if(requirePermissionForUndisguising() && (!player.hasPermission("iDisguise.undisguise"))) {
					sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
					return true;
				}
				if(DisguiseManager.isDisguised(player)) {
					UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.getDisguise(player).clone(), false);
					getServer().getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						DisguiseManager.undisguiseToAll(player);
						sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.un.success"));
					}
				} else {
					sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.un.not"));
				}
			}
			return true;
		}
		if(args.length == 0) {
			displayHelp(player, cmd.getName().toLowerCase());
			return true;
		}
		if(args[0].equalsIgnoreCase("ghost")) {
			if(!isDisguisingPermittedInWorld(player.getWorld()) && !player.hasPermission("iDisguise.admin")) {
				player.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.badworld"));
				return true;
			}
			if(!player.hasPermission("iDisguise.ghost")) {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
				return true;
			}
			String name = "";
			if(args.length == 2) {
				if(args[1].length() > 16) {
					sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.player.longname"));
					return true;
				} else {
					name = args[1];
				}
			} else {
				name = player.getName();
			}
			executeDisguise(player, new PlayerDisguise(name, true));
		} else if(args[0].equalsIgnoreCase("player")) {
			if(!isDisguisingPermittedInWorld(player.getWorld()) && !player.hasPermission("iDisguise.admin")) {
				player.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.badworld"));
				return true;
			}
			if(!player.hasPermission("iDisguise.player")) {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
				return true;
			}
			if(args.length == 2) {
				if(args[1].length() > 16) {
					sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.player.longname"));
				} else {
					executeDisguise(player, new PlayerDisguise(args[1], false));
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/" + cmd.getName() + " player <name>");
			}
		} else if(StringUtil.equalsIgnoreCase(args[0], "stats", "status", "state")) {
			Disguise disguise = DisguiseManager.getDisguise(player);
			if(disguise == null) {
				sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.stats.not"));
			} else {
				sender.sendMessage(ChatColor.GREEN + String.format("Mobtype: %s(%s)", lang.getString("mob." + getLangNameFor(disguise.getType())), getNameFor(disguise.getType())));
				if(disguise instanceof PlayerDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Name: " + ((PlayerDisguise)disguise).getName());
				}
				if(disguise instanceof MobDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Adult: " + Boolean.toString(((MobDisguise)disguise).isAdult()));
					sender.sendMessage(ChatColor.GREEN + "Custom name: " + ((MobDisguise)disguise).getCustomName());
				}
				if(disguise instanceof ColoredDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Color: " + ((ColoredDisguise)disguise).getColor().toString());
				}
				if(disguise instanceof WolfDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Tamed: " + Boolean.toString(((WolfDisguise)disguise).isTamed()));
					sender.sendMessage(ChatColor.GREEN + "Angry: " + Boolean.toString(((WolfDisguise)disguise).isAngry()));
				}
				if(disguise instanceof PigDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Saddled: " + Boolean.toString(((PigDisguise)disguise).isSaddled()));
				}
				if(disguise instanceof VillagerDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Profession: " + ((VillagerDisguise)disguise).getProfession().toString());
				}
				if(disguise instanceof OcelotDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Cat type: " + ((OcelotDisguise)disguise).getCatType().toString());
				}
				if(disguise instanceof HorseDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Variant: " + ((HorseDisguise)disguise).getVariant().toString());
					sender.sendMessage(ChatColor.GREEN + "Style: " + ((HorseDisguise)disguise).getStyle().toString());
					sender.sendMessage(ChatColor.GREEN + "Color: " + ((HorseDisguise)disguise).getColor().toString());
					sender.sendMessage(ChatColor.GREEN + "Saddled: " + Boolean.toString(((HorseDisguise)disguise).isSaddled()));
					sender.sendMessage(ChatColor.GREEN + "Chest: " + Boolean.toString(((HorseDisguise)disguise).hasChest()));
					sender.sendMessage(ChatColor.GREEN + "Armor: " + ((HorseDisguise)disguise).getArmor().toString());
				}
				if(disguise instanceof SizedDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Size: " + ((SizedDisguise)disguise).getSize());
				}
				if(disguise instanceof EndermanDisguise) {
					sender.sendMessage(ChatColor.GREEN + "Block in hand: " + ((EndermanDisguise)disguise).getBlockInHand().name() + ":" + ((EndermanDisguise)disguise).getBlockInHandData());
				}
			}
		} else if(args[0].equalsIgnoreCase("un")) {
			if(requirePermissionForUndisguising() && (!player.hasPermission("iDisguise.undisguise"))) {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
				return true;
			}
			if(DisguiseManager.isDisguised(player)) {
				UndisguiseEvent event = new UndisguiseEvent(player, DisguiseManager.getDisguise(player).clone(), false);
				getServer().getPluginManager().callEvent(event);
				if(!event.isCancelled()) {
					DisguiseManager.undisguiseToAll(player);
					sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.un.success"));
				}
			} else {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.un.not"));
			}
		} else if(args[0].equalsIgnoreCase("unall")) {
			if(player.hasPermission("iDisguise.admin")) {
				DisguiseManager.undisguiseAll();
				sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.unall.success"));
			} else {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
			}
		} else if(args[0].equalsIgnoreCase("reload")) {
			if(player.hasPermission("iDisguise.admin")) {
				onReload();
				sender.sendMessage(ChatColor.GREEN + lang.getString("cmd.disguise.reload"));
			} else {
				sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
			}
		} else {
			Disguise disguise = DisguiseManager.isDisguised(player) ? DisguiseManager.getDisguise(player).clone() : null;
			for(String argument : args) {
				if(argument.equalsIgnoreCase("bat")) {
					disguise = new MobDisguise(DisguiseType.BAT, true);
				} else if(argument.equalsIgnoreCase("blaze")) {
					disguise = new MobDisguise(DisguiseType.BLAZE, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "cave_spider", "cave-spider", "cavespider")) {
					disguise = new MobDisguise(DisguiseType.CAVE_SPIDER, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "charged_creeper", "charged-creeper", "chargedcreeper", "charged", "powered_creeper", "powered-creeper", "poweredcreeper", "powered")) {
					disguise = new CreeperDisguise(true);
				} else if(argument.equalsIgnoreCase("chicken")) {
					disguise = new MobDisguise(DisguiseType.CHICKEN, true);
				} else if(argument.equalsIgnoreCase("cow")) {
					disguise = new MobDisguise(DisguiseType.COW, true);
				} else if(argument.equalsIgnoreCase("creeper")) {
					disguise = new CreeperDisguise();
				} else if(argument.equalsIgnoreCase("donkey")) {
					disguise = new HorseDisguise(true, Horse.Variant.DONKEY, Horse.Style.NONE, Horse.Color.GRAY, false, false, HorseDisguise.Armor.NONE);
				} else if(StringUtil.equalsIgnoreCase(argument, "ender_dragon", "ender-dragon", "enderdragon", "dragon")) {
					disguise = new MobDisguise(DisguiseType.ENDER_DRAGON, true);
				} else if(argument.equalsIgnoreCase("enderman")) {
					disguise = new EndermanDisguise(Material.AIR);
				} else if(argument.equalsIgnoreCase("ghast")) {
					disguise = new MobDisguise(DisguiseType.GHAST, true);
				} else if(argument.equalsIgnoreCase("giant")) {
					disguise = new MobDisguise(DisguiseType.GIANT, true);
				} else if(argument.equalsIgnoreCase("horse")) {
					disguise = new HorseDisguise(true, Horse.Variant.HORSE, Horse.Style.NONE, Horse.Color.GRAY, false, false, HorseDisguise.Armor.NONE);
				} else if(StringUtil.equalsIgnoreCase(argument, "iron_golem", "iron-golem", "irongolem", "golem")) {
					disguise = new MobDisguise(DisguiseType.IRON_GOLEM, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "magma_cube", "magma-cube", "magmacube", "lava_cube", "lava-cube", "lavacube")) {
					disguise = new SizedDisguise(DisguiseType.MAGMA_CUBE, 1);
				} else if(argument.equalsIgnoreCase("mule")) {
					disguise = new HorseDisguise(true, Horse.Variant.MULE, Horse.Style.NONE, Horse.Color.GRAY, false, false, HorseDisguise.Armor.NONE);
				} else if(StringUtil.equalsIgnoreCase(argument, "mushroom_cow", "mushroom-cow", "mushroomcow", "mooshroom")) {
					disguise = new MobDisguise(DisguiseType.MUSHROOM_COW, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "ocelot", "cat")) {
					disguise = new OcelotDisguise(Ocelot.Type.WILD_OCELOT, true);
				} else if(argument.equalsIgnoreCase("pig")) {
					disguise = new PigDisguise(true, false);
				} else if(StringUtil.equalsIgnoreCase(argument, "pig_zombie", "pig-zombie", "pigzombie", "pigman", "zombie_pigman", "zombie-pigman", "zombiepigman")) {
					disguise = new MobDisguise(DisguiseType.PIG_ZOMBIE, true);
				} else if(argument.equalsIgnoreCase("random")) {
					if(!player.hasPermission("iDisguise.random")) {
						sender.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
						return true;
					}
					DisguiseType type = DisguiseType.random(Type.MOB);
					boolean adult = RandomUtil.nextBoolean();
					if(type.equals(DisguiseType.SHEEP)) {
						disguise = new ColoredDisguise(type, adult, DyeColor.values()[RandomUtil.nextInt(DyeColor.values().length)]);
					} else if(type.equals(DisguiseType.OCELOT)) {
						disguise = new OcelotDisguise(Ocelot.Type.values()[RandomUtil.nextInt(Ocelot.Type.values().length)], adult);
					} else if(type.equals(DisguiseType.PIG)) {
						disguise = new PigDisguise(adult, RandomUtil.nextBoolean());
					} else if(type.equals(DisguiseType.VILLAGER)) {
						disguise = new VillagerDisguise(adult, Villager.Profession.values()[RandomUtil.nextInt(Villager.Profession.values().length)]);
					} else if(type.equals(DisguiseType.WOLF)) {
						disguise = new WolfDisguise(adult, DyeColor.values()[RandomUtil.nextInt(DyeColor.values().length)], RandomUtil.nextBoolean(), RandomUtil.nextBoolean());
					} else if(ObjectUtil.equals(type, DisguiseType.SLIME , DisguiseType.MAGMA_CUBE)) {
						disguise = new SizedDisguise(type, RandomUtil.nextInt(256) + 1);
					} else if(type.equals(DisguiseType.HORSE)) {
						disguise = new HorseDisguise(RandomUtil.nextBoolean(), Horse.Variant.values()[RandomUtil.nextInt(Horse.Variant.values().length)], Horse.Style.values()[RandomUtil.nextInt(Horse.Style.values().length)], Horse.Color.values()[RandomUtil.nextInt(Horse.Color.values().length)], RandomUtil.nextBoolean(), RandomUtil.nextBoolean(), RandomUtil.nextEnumValue(HorseDisguise.Armor.class));
					} else if(type.equals(DisguiseType.ENDERMAN)) {
						Material blockInHand;
						do {
							blockInHand = RandomUtil.nextEnumValue(Material.class);
						} while(!blockInHand.isBlock());
						disguise = new EndermanDisguise(blockInHand, RandomUtil.nextInt(256));
					} else if(type.equals(DisguiseType.CREEPER)) {
						disguise = new CreeperDisguise(RandomUtil.nextBoolean());
					} else if(type.equals(DisguiseType.SKELETON)) {
						disguise = new SkeletonDisguise(RandomUtil.nextEnumValue(SkeletonType.class));
					} else if(type.equals(DisguiseType.ZOMBIE)) {
						disguise = new ZombieDisguise(adult, RandomUtil.nextBoolean());
					} else {
						disguise = new MobDisguise(type, adult);
					}
				} else if(argument.equalsIgnoreCase("sheep")) {
					disguise = new ColoredDisguise(DisguiseType.SHEEP, true, DyeColor.WHITE);
				} else if(argument.equalsIgnoreCase("silverfish")) {
					disguise = new MobDisguise(DisguiseType.SILVERFISH, true);
				} else if(argument.equalsIgnoreCase("skeleton")) {
					disguise = new SkeletonDisguise();
				} else if(StringUtil.equalsIgnoreCase(argument, "skeleton_horse", "skeleton-horse", "skeletonhorse", "skeletal_horse", "skeletal-horse", "skeletalhorse")) {
					disguise = new HorseDisguise(true, Horse.Variant.SKELETON_HORSE, Horse.Style.NONE, Horse.Color.GRAY, false, false, HorseDisguise.Armor.NONE);
				} else if(StringUtil.equalsIgnoreCase(argument, "slime", "cube")) {
					disguise = new SizedDisguise(DisguiseType.SLIME, 1);
				} else if(StringUtil.equalsIgnoreCase(argument, "snowman", "snow_golem", "sonw-golem", "snowgolem")) {
					disguise = new MobDisguise(DisguiseType.SNOWMAN, true);
				} else if(argument.equalsIgnoreCase("spider")) {
					disguise = new MobDisguise(DisguiseType.SPIDER, true);
				} else if(argument.equalsIgnoreCase("squid")) {
					disguise = new MobDisguise(DisguiseType.SQUID, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "undead_horse", "undead-horse", "undeadhorse", "zombie_horse", "zombie-horse", "zombiehorse")) {
					disguise = new HorseDisguise(true, Horse.Variant.UNDEAD_HORSE, Horse.Style.NONE, Horse.Color.GRAY, false, false, HorseDisguise.Armor.NONE);
				} else if(StringUtil.equalsIgnoreCase(argument, "villager", "blacksmith", "butcher", "farmer", "librarian", "priest")) {
					Villager.Profession profession = Villager.Profession.FARMER;
					try {
						profession = Villager.Profession.valueOf(argument.toUpperCase());
					} catch(IllegalArgumentException e) {
					}
					disguise = new VillagerDisguise(true, profession);
				} else if(argument.equalsIgnoreCase("witch")) {
					disguise = new MobDisguise(DisguiseType.WITCH, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "wither", "wither_boss", "wither-boss", "witherboss")) {
					disguise = new MobDisguise(DisguiseType.WITHER, true);
				} else if(StringUtil.equalsIgnoreCase(argument, "wither_skeleton", "wither-skeleton", "witherskeleton")) {
					disguise = new SkeletonDisguise(SkeletonType.WITHER);
				} else if(StringUtil.equalsIgnoreCase(argument, "wolf", "dog")) {
					disguise = new WolfDisguise(true, DyeColor.RED, false, false);
				} else if(argument.equalsIgnoreCase("zombie")) {
					disguise = new ZombieDisguise(true);
				} else if(StringUtil.equalsIgnoreCase(argument, "zombie_villager", "zombie-villager", "zombievillager", "villager_zombie", "villager-zombie", "villagerzombie")) {
					disguise = new ZombieDisguise(true, true);
				}
			}
			for(String argument : args) {
				if(disguise instanceof MobDisguise) {
					if(StringUtil.equalsIgnoreCase(argument, "baby", "child")) {
						((MobDisguise)disguise).setAdult(false);
					} else if(argument.equalsIgnoreCase("adult")) {
						((MobDisguise)disguise).setAdult(true);
					}
				}
				if(disguise instanceof ColoredDisguise) {
					try {
						DyeColor color = DyeColor.valueOf(argument.toUpperCase());
						((ColoredDisguise)disguise).setColor(color);
					} catch(IllegalArgumentException e) {
					}
				}
				if(disguise instanceof HorseDisguise) {
					try {
						Horse.Color color = Horse.Color.valueOf(argument.toUpperCase());
						((HorseDisguise)disguise).setColor(color);
					} catch(IllegalArgumentException e) {
					}
					try {
						Horse.Style style = Horse.Style.valueOf(argument.toUpperCase());
						((HorseDisguise)disguise).setStyle(style);
					} catch(IllegalArgumentException e) {
					}
					try {
						Horse.Variant variant = Horse.Variant.valueOf(argument.toUpperCase());
						((HorseDisguise)disguise).setVariant(variant);
					} catch(IllegalArgumentException e) {
					}
					if(StringUtil.equalsIgnoreCase(argument, "saddle", "saddled")) {
						((HorseDisguise)disguise).setSaddled(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "nosaddle", "no-saddle", "no_saddle", "notsaddled", "not-saddled", "not_saddled")) {
						((HorseDisguise)disguise).setSaddled(false);
					}
					if(StringUtil.equalsIgnoreCase(argument, "chest", "haschest", "has-chest", "has_chest")) {
						((HorseDisguise)disguise).setHasChest(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "nochest", "no-chest", "no_chest")) {
						((HorseDisguise)disguise).setHasChest(false);
					}
					if(StringUtil.equalsIgnoreCase(argument, "saddle", "saddled")) {
						((HorseDisguise)disguise).setSaddled(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "nosaddle", "no-saddle", "no_saddle", "notsaddled", "not-saddled", "not_saddled")) {
						((HorseDisguise)disguise).setSaddled(false);
					}
					try {
						HorseDisguise.Armor armor = HorseDisguise.Armor.valueOf(argument.toUpperCase());
						((HorseDisguise)disguise).setArmor(armor);
					} catch(IllegalArgumentException e) {
					}
				}
				if(disguise instanceof OcelotDisguise) {
					try {
						Ocelot.Type catType = Ocelot.Type.valueOf(argument.toUpperCase());
						((OcelotDisguise)disguise).setCatType(catType);
					} catch(IllegalArgumentException e) {
					}
				}
				if(disguise instanceof PigDisguise) {
					if(StringUtil.equalsIgnoreCase(argument, "saddle", "saddled")) {
						((PigDisguise)disguise).setSaddled(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "nosaddle", "no-saddle", "no_saddle", "notsaddled", "not-saddled", "not_saddled")) {
						((PigDisguise)disguise).setSaddled(false);
					}
				}
				if(disguise instanceof SizedDisguise) {
					if(StringUtil.equalsIgnoreCase(argument, "small", "tiny")) {
						((SizedDisguise)disguise).setSize(1);
					} else if(StringUtil.equalsIgnoreCase(argument, "medium", "normal")) {
						((SizedDisguise)disguise).setSize(2);
					} else if(argument.equalsIgnoreCase("big")) {
						((SizedDisguise)disguise).setSize(4);
					} else {
						try {
							int size = Integer.parseInt(argument);
							if(size > 0) {
								((SizedDisguise)disguise).setSize(size);
							}
						} catch(NumberFormatException e) {
						}
					}
				}
				if(disguise instanceof VillagerDisguise) {
					try {
						Villager.Profession profession = Villager.Profession.valueOf(argument.toUpperCase());
						((VillagerDisguise)disguise).setProfession(profession);
					} catch(IllegalArgumentException e) {
					}
				}
				if(disguise instanceof WolfDisguise) {
					if(argument.equalsIgnoreCase("tamed")) {
						((WolfDisguise)disguise).setTamed(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "non-tamed", "nontamed", "non_tamed", "not-tamed", "nottamed", "not_tamed", "untamed")) {
						((WolfDisguise)disguise).setTamed(false);
					} else if(StringUtil.equalsIgnoreCase(argument, "angry", "aggressive")) {
						((WolfDisguise)disguise).setAngry(true);
					} else if(StringUtil.equalsIgnoreCase(argument, "not-angry", "notangry", "not_angry", "non-angry", "nonangry", "non_angry", "not-aggressive", "notaggressive", "not_aggressive", "non-aggressive", "nonaggressive", "non_aggressive")) {
						((WolfDisguise)disguise).setAngry(false);
					}
				}
				if(disguise instanceof EndermanDisguise) {
					try {
						Material blockInHand = Material.valueOf(argument.toUpperCase());
						((EndermanDisguise)disguise).setBlockInHand(blockInHand);
					} catch(IllegalArgumentException e) {
					}
					try {
						int blockInHandData = Integer.parseInt(argument);
						if(blockInHandData >= 0 && blockInHandData < 256) {
							((EndermanDisguise)disguise).setBlockInHandData(blockInHandData);
						}
					} catch(NumberFormatException e) {
					}
				}
			}
			if(disguise == null || disguise.equals(DisguiseManager.getDisguise(player))) {
				displayHelp(player, cmd.getName().toLowerCase());
			} else {
				executeDisguise(player, disguise);
			}
		}
		return true;
	}
	
	private void displayHelp(Player player, String alias) {
		String undisguiseAlias = "u" + (alias.equalsIgnoreCase("d") ? "" : "n") + alias;
		player.sendMessage(ChatColor.BLUE + getFullName());
		player.sendMessage(ChatColor.GREEN + "/" + alias + " <ghost/player> <name> - Disguise as a player or a ghost");
		player.sendMessage(ChatColor.GREEN + "/" + alias + " ghost - Disguise as a ghost of yourself");
		player.sendMessage(ChatColor.GREEN + "/" + alias + " random - Disguise as a random mob");
		player.sendMessage(ChatColor.GREEN + "/" + alias + " reload - Reloads the config");
		player.sendMessage(ChatColor.GREEN + "/" + alias + " stats - Shows what you are disguised as");
		player.sendMessage(ChatColor.GREEN + "/" + undisguiseAlias + " - Undisguise yourself");
		player.sendMessage(ChatColor.GREEN + "/" + undisguiseAlias + " <*/all> - Undisguise everyone");
		player.sendMessage(ChatColor.GREEN + "/" + alias + " <type/subtype>");
		player.sendMessage(ChatColor.GREEN + "Types:");
		player.sendMessage(ChatColor.GREEN + " bat, blaze, cave_spider, charged_creeper, chicken, cow, creeper, donkey, ender_dragon, enderman, ghast, giant, horse, iron_golem, magma_cube, mule, mushroom_cow, ocelot, pig, pig_zombie, sheep, silverfish, skeleton, skeleton_horse, slime, snowman, spider, squid, undead_horse, villager, witch, wither, wither_skeleton, wolf, zombie, zombie_villager");
		player.sendMessage(ChatColor.GREEN + "Subtypes:");
		StringBuilder builder;
		player.sendMessage(ChatColor.GREEN + " Age: adult, baby, child");
		builder = new StringBuilder(DyeColor.values()[0].name().toLowerCase());
		for(int i = 1; i < DyeColor.values().length; i++) {
			builder.append(", ");
			builder.append(DyeColor.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Color: " + builder.toString());
		builder = new StringBuilder(Horse.Color.values()[0].name().toLowerCase());
		for(int i = 1; i < Horse.Color.values().length; i++) {
			builder.append(", ");
			builder.append(Horse.Color.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Horse color: " + builder.toString());
		builder = new StringBuilder(Horse.Style.values()[0].name().toLowerCase());
		for(int i = 1; i < Horse.Style.values().length; i++) {
			builder.append(", ");
			builder.append(Horse.Style.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Horse style: " + builder.toString());
		player.sendMessage(ChatColor.GREEN + " Horse chest: has_chest, no_chest");
		player.sendMessage(ChatColor.GREEN + " Horse saddle: saddled, not_saddled");
		builder = new StringBuilder(HorseDisguise.Armor.values()[0].toString().toLowerCase());
		for(int i = 1; i < HorseDisguise.Armor.values().length; i++) {
			builder.append(", ");
			builder.append(HorseDisguise.Armor.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Horse armor: " + builder.toString());
		builder = new StringBuilder(Ocelot.Type.values()[0].name().toLowerCase());
		for(int i = 1; i < Ocelot.Type.values().length; i++) {
			builder.append(", ");
			builder.append(Ocelot.Type.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Cat type: " + builder.toString());
		player.sendMessage(ChatColor.GREEN + " Pig saddle: saddled, not_saddled");
		player.sendMessage(ChatColor.GREEN + " Slime size: small, tiny, medium, normal, big, <number>");
		builder = new StringBuilder(Villager.Profession.values()[0].name().toLowerCase());
		for(int i = 1; i < Villager.Profession.values().length; i++) {
			builder.append(", ");
			builder.append(Villager.Profession.values()[i].name().toLowerCase());
		}
		player.sendMessage(ChatColor.GREEN + " Villager profession: " + builder.toString());
		player.sendMessage(ChatColor.GREEN + " Wolf tamed: tamed, not_tamed");
		player.sendMessage(ChatColor.GREEN + " Wolf angry: angry, not_angry");
		player.sendMessage(ChatColor.GREEN + " Enderman block in hand: <block>");
		player.sendMessage(ChatColor.GREEN + " Enderman block in hand data: <data>");
	}
	
	@Deprecated
	private void executeDisguise(Player player, Disguise disguise) {
		if(!isDisguisingPermittedInWorld(player.getWorld()) && !player.hasPermission("iDisguise.admin")) {
			player.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.badworld"));
			return;
		}
		if(!player.hasPermission("iDisguise." + getNameFor(disguise.getType()))) {
			player.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
			return;
		}
		if(disguise instanceof MobDisguise && (!((MobDisguise)disguise).isAdult()) && (!player.hasPermission("iDisguise.baby"))) {
			player.sendMessage(ChatColor.RED + lang.getString("cmd.disguise.noperm"));
			return;
		}
		DisguiseEvent event = new DisguiseEvent(player, disguise);
		getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()) {
			disguise = event.getDisguise();
			DisguiseManager.disguiseToAll(player, disguise);
			if(disguise instanceof MobDisguise) {
				player.sendMessage(ChatColor.GREEN + String.format(lang.getString("cmd.disguise.success.mob"), lang.getString("mob." + getLangNameFor(disguise.getType())), getNameFor(disguise.getType())));
			} else if(disguise instanceof PlayerDisguise) {
				player.sendMessage(ChatColor.GREEN + String.format(lang.getString("cmd.disguise.success." + (((PlayerDisguise)disguise).isGhost() ? "ghost" : "player")), ((PlayerDisguise)disguise).getName()));
			}
		}
	}
	
	private void checkDirectory() {
		if(!directory.exists()) {
			directory.mkdir();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadData() {
		File dataFile = new File(directory, "data.bin");
		File oldDataFile = new File(directory, "disguise.bin");
		if(dataFile.exists()) {
			Object map = SLAPI.load(dataFile);
			if(map instanceof ConcurrentHashMap) {
				DisguiseManager.setDisguiseList(new DisguiseList((ConcurrentHashMap<UUID, Disguise>)map));
			}
		} else if(oldDataFile.exists()) {
			Object oldMap = SLAPI.load(oldDataFile);
			if(oldMap instanceof Map) {
				ConcurrentHashMap<UUID, Disguise> converted = new ConcurrentHashMap<UUID, Disguise>();
				for(Entry<String, Disguise> entry : ((Map<String, Disguise>)oldMap).entrySet()) {
					converted.put(ProfileUtil.getUniqueId(entry.getKey()), entry.getValue());
				}
				DisguiseManager.setDisguiseList(new DisguiseList(converted));
			}
			oldDataFile.delete();
		}
	}
	
	private void saveData() {
		File dataFile = new File(directory, "data.bin");
		SLAPI.save(DisguiseManager.getDisguiseList().getMap(), dataFile);
	}
	
	public DisguiseAPI getAPI() {
		return new DisguiseAPI() {
			public void disguiseToAll(Player player, Disguise disguise) {
				DisguiseManager.disguiseToAll(player, disguise);
			}
			
			public void undisguiseToAll(Player player) {
				DisguiseManager.undisguiseToAll(player);
			}
			
			public void undisguiseAll() {
				DisguiseManager.undisguiseAll();
			}
			
			public boolean isDisguised(Player player) {
				return DisguiseManager.isDisguised(player);
			}
			
			public Disguise getDisguise(Player player) {
				return DisguiseManager.getDisguise(player).clone();
			}
			
			public int getOnlineDisguiseCount() {
				return DisguiseManager.getOnlineDisguiseCount();
			}
			
			public String getLocale() {
				return getLocalization();
			}
			
			public String getLocalizedPhrase(String name) {
				return getLangString(name);
			}
			
			public SoundSystem getSoundSystem(DisguiseType type) {
				return SoundSystem.getSoundSystem(type);
			}
			
			public void setSoundSystem(DisguiseType type, SoundSystem soundSystem) {
				SoundSystem.setSoundSystem(type, soundSystem);
			}
		};
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "iDisguise v" + getVersion();
	}
	
	public boolean saveDisguises() {
		return configuration.getBoolean("save-disguises");
	}
	
	public boolean canDisguisedPlayersBeDamaged() {
		return configuration.getBoolean("entity-damage-while-disguised");
	}
	
	public boolean undisguisePlayerWhenHitByLiving() {
		return configuration.getBoolean("undisguise-on-hit");
	}
	
	public boolean requirePermissionForUndisguising() {
		return configuration.getBoolean("permission-for-undisguise");
	}
	
	public boolean isDisguisingPermittedInWorld(World world) {
		return isDisguisingPermittedInWorld(world.getName());
	}
	
	public boolean isDisguisingPermittedInWorld(String world) {
		return !configuration.getStringList("prohibited-worlds").contains(world);
	}
	
	public boolean checkForUpdates() {
		return configuration.getBoolean("check-for-updates");
	}
	
	@Deprecated
	public String getLangNameFor(DisguiseType type) {
		return type.name().toLowerCase().replace("_", "-");
	}
	
	@Deprecated
	public String getNameFor(DisguiseType type) {
		return type.name().toLowerCase();
	}
	
	@Deprecated
	public String getLocalization() {
		return "enUS";
	}
	
	@Deprecated
	public String getLangString(String name) {
		return lang.getString(name);
	}
	
	public boolean undisguisePlayerWhenHitByProjectile() {
		return configuration.getBoolean("undisguise-on-projectile-hit");
	}
	
	public boolean undisguisePlayerWhenHitsOtherPlayer() {
		return configuration.getBoolean("undisguise-on-hit-other");
	}
	
	public boolean isSoundSystemEnabled() {
		return configuration.getBoolean("sound-system");
	}
	
	public boolean showOriginalPlayerNames() {
		return configuration.getBoolean("show-name-while-disguised");
	}
	
	public boolean canMobsTargetDisguisedPlayers() {
		return !configuration.getBoolean("no-target-while-disguised");
	}
	
	public boolean isGhostDisguiseEnabled() {
		return configuration.getBoolean("ghost-disguises");
	}
	
}