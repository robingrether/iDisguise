package de.robingrether.idisguise.management;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.GuardianDisguise;
import de.robingrether.idisguise.disguise.HorseDisguise;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;

public class Sounds {
	
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<DisguiseType, Sounds>();
	private static boolean enabled = false;
	
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
	
	public static String getDeath(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.death(disguise);
			}
		}
		return null;
	}
	
	public static String getFallBig(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.fallBig(disguise);
			}
		}
		return null;
	}
	
	public static String getFallSmall(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.fallSmall(disguise);
			}
		}
		return null;
	}
	
	public static String getHit(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.hit(disguise);
			}
		}
		return null;
	}
	
	public static String getSplash(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.splash(disguise);
			}
		}
		return null;
	}
	
	public static String getSwim(MobDisguise disguise) {
		if(enabled) {
			Sounds sounds = entitySounds.get(disguise.getType());
			if(sounds != null) {
				return sounds.swim(disguise);
			}
		}
		return null;
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
	
	static {
		setSoundsForEntity(DisguiseType.BAT, new Sounds("mob.bat.death", null, null, "mob.bat.hurt", null, null));
		setSoundsForEntity(DisguiseType.BLAZE, new Sounds("mob.blaze.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.blaze.hit", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.CAVE_SPIDER, new Sounds("mob.spider.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.spider.say", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.CHICKEN, new Sounds("mob.chicken.hurt", null, null, "mob.chicken.hurt", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.COW, new Sounds("mob.cow.hurt", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.cow.hurt", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.CREEPER, new Sounds("mob.creeper.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.creeper.say", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.ENDER_DRAGON, new Sounds("mob.enderdragon.end", null, null, "mob.enderdragon.hit", null, null));
		setSoundsForEntity(DisguiseType.ENDERMAN, new Sounds("mob.enderman.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.enderman.hit", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.ENDERMITE, new Sounds("mob.silverfish.kill", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.silverfish.hit", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.GHAST, new Sounds("mob.ghast.death", null, null, "mob.ghast.scream", null, null));
		setSoundsForEntity(DisguiseType.GIANT, new Sounds("game.hostile.die", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "game.hostile.hurt", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.GUARDIAN, new Sounds(null, "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", null, "game.hostile.swim.splash", "game.hostile.swim") {
			
			public String death(MobDisguise disguise) {
				return (disguise instanceof GuardianDisguise && ((GuardianDisguise)disguise).isElder()) ? "mob.guardian.elder.death" : "mob.guardian.death";
			}
			
			public String hit(MobDisguise disguise) {
				return (disguise instanceof GuardianDisguise && ((GuardianDisguise)disguise).isElder()) ? "mob.guardian.elder.hit" : "mob.guardian.hit";
			}
			
		});
		setSoundsForEntity(DisguiseType.HORSE, new Sounds(null, "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", null, "game.neutral.swim.splash", "game.neutral.swim") {
			
			public String death(MobDisguise disguise) {
				if(disguise instanceof HorseDisguise) {
					switch(((HorseDisguise)disguise).getVariant()) {
						case HORSE:
							return "mob.horse.death";
						case DONKEY:
						case MULE:
							return "mob.horse.donkey.death";
						case UNDEAD_HORSE:
							return "mob.horse.zombie.death";
						case SKELETON_HORSE:
							return "mob.horse.skeleton.death";
					}
				}
				return "mob.horse.death";
			}
			
			public String hit(MobDisguise disguise) {
				if(disguise instanceof HorseDisguise) {
					switch(((HorseDisguise)disguise).getVariant()) {
						case HORSE:
							return "mob.horse.hit";
						case DONKEY:
						case MULE:
							return "mob.horse.donkey.hit";
						case UNDEAD_HORSE:
							return "mob.horse.zombie.hit";
						case SKELETON_HORSE:
							return "mob.horse.skeleton.hit";
					}
				}
				return "mob.horse.hit";
			}
			
		});
		setSoundsForEntity(DisguiseType.IRON_GOLEM, new Sounds("mob.irongolem.death", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.irongolem.hit", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.MAGMA_CUBE, new Sounds(null, "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", null, "game.neutral.swim.splash", "game.neutral.swim") {
			
			public String death(MobDisguise disguise) {
				return (disguise instanceof SizedDisguise && ((SizedDisguise)disguise).getSize() > 1) ? "mob.slime.big" : "mob.slime.small";
			}
			
			public String hit(MobDisguise disguise) {
				return (disguise instanceof SizedDisguise && ((SizedDisguise)disguise).getSize() > 1) ? "mob.slime.big" : "mob.slime.small";
			}
			
		});
		setSoundsForEntity(DisguiseType.MUSHROOM_COW, getSoundsForEntity(DisguiseType.COW));
		setSoundsForEntity(DisguiseType.OCELOT, new Sounds("mob.cat.hitt", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.cat.hitt", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.PIG, new Sounds("mob.pig.death", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.pig.say", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.PIG_ZOMBIE, new Sounds("mob.zombiepig.zpigdeath", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.zombiepig.zpighurt", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.RABBIT, new Sounds("mob.rabbit.death", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.rabbit.hurt", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.SHEEP, new Sounds("mob.sheep.say", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.sheep.say", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.SILVERFISH, getSoundsForEntity(DisguiseType.ENDERMITE));
		setSoundsForEntity(DisguiseType.SKELETON, new Sounds("mob.skeleton.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.skeleton.hurt", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.SLIME, getSoundsForEntity(DisguiseType.MAGMA_CUBE));
		setSoundsForEntity(DisguiseType.SNOWMAN, new Sounds(null, "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", null, "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.SPIDER, getSoundsForEntity(DisguiseType.CAVE_SPIDER));
		setSoundsForEntity(DisguiseType.SQUID, getSoundsForEntity(DisguiseType.SNOWMAN));
		setSoundsForEntity(DisguiseType.VILLAGER, new Sounds("mob.villager.death", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.villager.hit", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.WITCH, new Sounds(null, "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", null, "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.WITHER, new Sounds("mob.wither.death", null, null, "mob.wither.hurt", "game.hostile.swim.splash", "game.hostile.swim"));
		setSoundsForEntity(DisguiseType.WOLF, new Sounds("mob.wolf.death", "game.neutral.hurt.fall.big", "game.neutral.hurt.fall.small", "mob.wolf.hurt", "game.neutral.swim.splash", "game.neutral.swim"));
		setSoundsForEntity(DisguiseType.ZOMBIE, new Sounds("mob.zombie.death", "game.hostile.hurt.fall.big", "game.hostile.hurt.fall.small", "mob.zombie.hurt", "game.hostile.swim.splash", "game.hostile.swim"));
	}
	
}