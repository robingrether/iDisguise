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
import org.bukkit.entity.Skeleton.SkeletonType;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.GuardianDisguise;
import de.robingrether.idisguise.disguise.HorseDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;
import de.robingrether.idisguise.disguise.SkeletonDisguise;
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
		if(sounds != null) {	
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
			}
		}
		return null;
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
						final String[] arguments = soundMatcher.group(2).split(",", -1);
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
							case "SKELETON":
								setSoundsForEntity(DisguiseType.SKELETON, new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										if(disguise instanceof SkeletonDisguise) {
											return ((SkeletonDisguise)disguise).getSkeletonType().equals(SkeletonType.WITHER) ? arguments[4] : ((SkeletonDisguise)disguise).getSkeletonType().name().equals("STRAY") ? arguments[5] : arguments[6];
										}
										return arguments[6];
									}
									
									public String hit(MobDisguise disguise) {
										if(disguise instanceof SkeletonDisguise) {
											return ((SkeletonDisguise)disguise).getSkeletonType().equals(SkeletonType.WITHER) ? arguments[7] : ((SkeletonDisguise)disguise).getSkeletonType().name().equals("STRAY") ? arguments[8] : arguments[9];
										}
										return arguments[9];
									}
									
								});
								break;
							case "ZOMBIE":
								setSoundsForEntity(DisguiseType.ZOMBIE, new Sounds(null, arguments[0], arguments[1], null, arguments[2], arguments[3]) {
									
									public String death(MobDisguise disguise) {
										if(disguise instanceof ZombieDisguise) {
											return ((ZombieDisguise)disguise).isVillager() ? arguments[4] : ((ZombieDisguise)disguise).isHusk() ? arguments[5] : arguments[6];
										}
										return arguments[6];
									}
									
									public String hit(MobDisguise disguise) {
										if(disguise instanceof ZombieDisguise) {
											return ((ZombieDisguise)disguise).isVillager() ? arguments[7] : ((ZombieDisguise)disguise).isHusk() ? arguments[8] : arguments[9];
										}
										return arguments[9];
									}
									
								});
								break;
							default:
								setSoundsForEntity(DisguiseType.valueOf(name), new Sounds(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]));
								break;
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot parse line: " + line, e);
						}
					}
				}
			}
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required sound effect configuration.", e);
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