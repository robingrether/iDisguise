package de.robingrether.idisguise.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.GuardianDisguise;
import de.robingrether.idisguise.disguise.HorseDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.ZombieDisguise;
import de.robingrether.util.StringUtil;

public class Sounds {
	
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<DisguiseType, Sounds>();
	private static boolean enabled = false;
	private static String[] soundsToReplace;
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static void setEnabled(boolean enabled) {
		Sounds.enabled = enabled;
	}
	
	public static Sounds getSoundsForEntity(DisguiseType type) {
		return entitySounds.get(type);
	}
	
	public static boolean setSoundsForEntity(DisguiseType type, Sounds sounds) {
		if(type.isMob()) {
			entitySounds.put(type, sounds);
			return true;
		}
		return false;
	}
	
	public static boolean isSoundFromPlayer(String sound) {
		return StringUtil.equals(sound, soundsToReplace);
	}
	
	public static String replaceSoundFromPlayer(String sound, MobDisguise disguise) {
		Sounds sounds = getSoundsForEntity(disguise.getType());
		String replacement = null;
		if(sound.equals(soundsToReplace[0])) {
			replacement = sounds.death(disguise);
		} else if(sound.equals(soundsToReplace[1])) {
			replacement = sounds.fallBig(disguise);
		} else if(sound.equals(soundsToReplace[2])) {
			replacement = sounds.fallSmall(disguise);
		} else if(sound.equals(soundsToReplace[3])) {
			replacement = sounds.hit(disguise);
		} else if(sound.equals(soundsToReplace[4])) {
			replacement = sounds.splash(disguise);
		} else if(sound.equals(soundsToReplace[5])) {
			replacement = sounds.swim(disguise);
		}
		if(replacement != null && !replacement.isEmpty()) {
			return replacement;
		} else {
			return null;
		}
	}
	
	private static final Pattern soundPattern = Pattern.compile("([A-Z_]+)->(.+)");
	
	public static void init(String file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Sounds.class.getResourceAsStream(file)));
			String line;
			while((line = reader.readLine()) != null) {
				Matcher soundMatcher = soundPattern.matcher(line);
				if(soundMatcher.matches()) {
					try {
						String name = soundMatcher.group(1);
						final String[] arguments = soundMatcher.group(2).split(",");
						switch(name) {
							case "_":
								soundsToReplace = arguments;
								break;
							case "GUARDIAN":
								setSoundsForEntity(DisguiseType.GUARDIAN, new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										return (disguise instanceof GuardianDisguise && ((GuardianDisguise)disguise).isElder()) ? arguments[4] : arguments[5];
									}
									
									public String hit(MobDisguise disguise) {
										return (disguise instanceof GuardianDisguise && ((GuardianDisguise)disguise).isElder()) ? arguments[6] : arguments[7];
									}
									
								});
								break;
							case "HORSE":
								setSoundsForEntity(DisguiseType.HORSE, new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										if(disguise instanceof HorseDisguise) {
											switch(((HorseDisguise)disguise).getVariant()) {
												case HORSE:
													return arguments[4];
												case DONKEY:
												case MULE:
													return arguments[5];
												case UNDEAD_HORSE:
													return arguments[6];
												case SKELETON_HORSE:
													return arguments[7];
											}
										}
										return arguments[4];
									}
									
									public String hit(MobDisguise disguise) {
										if(disguise instanceof HorseDisguise) {
											switch(((HorseDisguise)disguise).getVariant()) {
												case HORSE:
													return arguments[8];
												case DONKEY:
												case MULE:
													return arguments[9];
												case UNDEAD_HORSE:
													return arguments[10];
												case SKELETON_HORSE:
													return arguments[11];
											}
										}
										return arguments[8];
									}
									
								});
								break;
							case "MAGMA_CUBE":
							case "SLIME":
								setSoundsForEntity(DisguiseType.valueOf(name), new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										return (disguise instanceof SizedDisguise && ((SizedDisguise)disguise).getSize() > 1) ? arguments[4] : arguments[5];
									}
									
									public String hit(MobDisguise disguise) {
										return (disguise instanceof SizedDisguise && ((SizedDisguise)disguise).getSize() > 1) ? arguments[6] : arguments[7];
									}
									
								});
								break;
							case "ZOMBIE":
								setSoundsForEntity(DisguiseType.ZOMBIE, new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										return (disguise instanceof ZombieDisguise && ((ZombieDisguise)disguise).isVillager()) ? arguments[4] : arguments[5];
									}
									
									public String hit(MobDisguise disguise) {
										return (disguise instanceof ZombieDisguise && ((ZombieDisguise)disguise).isVillager()) ? arguments[6] : arguments[7];
									}
									
								});
								break;
							default:
								setSoundsForEntity(DisguiseType.valueOf(name), new Sounds(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]));
								break;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						if(VersionHelper.debug()) {
							Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot parse line: " + line, e);
						}
					}
				}
			}
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot load the required sound effect configuration.");
			}
		}
	}
	
	protected String death, fallBig, fallSmall, hit, splash, swim;
	
	public Sounds(String death, String fallBig, String fallSmall, String hit, String splash, String swim) {
		this.death = death;
		this.fallBig = fallBig;
		this.fallSmall = fallSmall;
		this.hit = hit;
		this.splash = splash;
		this.swim = swim;
	}
	
	public String death(MobDisguise disguise) {
		return death;
	}
	
	public String fallBig(MobDisguise disguise) {
		return fallBig;
	}
	
	public String fallSmall(MobDisguise disguise) {
		return fallSmall;
	}
	
	public String hit(MobDisguise disguise) {
		return hit;
	}
	
	public String splash(MobDisguise disguise) {
		return splash;
	}
	
	public String swim(MobDisguise disguise) {
		return swim;
	}
	
}