package de.robingrether.idisguise.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;

public abstract class Sounds {
	
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<DisguiseType, Sounds>();
	private static boolean enabled = false;
	
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
	
	public abstract String death(MobDisguise disguise);
	
	public abstract String fallBig(MobDisguise disguise);
	
	public abstract String fallSmall(MobDisguise disguise);
	
	public abstract String hit(MobDisguise disguise);
	
	public abstract String splash(MobDisguise disguise);
	
	public abstract String swim(MobDisguise disguise);
	
}