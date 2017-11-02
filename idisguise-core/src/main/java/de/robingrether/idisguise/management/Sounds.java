package de.robingrether.idisguise.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;

public class Sounds {
	
	private static Map<DisguiseType, Sounds> entitySounds = new ConcurrentHashMap<DisguiseType, Sounds>();
	
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
	
	public static String replaceSoundEffect(DisguiseType source, String soundEffect, Disguise target) {
		Sounds sourceSounds = getSoundsForEntity(source);
		Sounds targetSounds = getSoundsForEntity(target.getType());
		if(sourceSounds != null) {
			SoundEffectType type = sourceSounds.matchSoundEffect(soundEffect);
			if(type != null) {
				if(targetSounds != null) {
					String targetSoundEffect;
					while((targetSoundEffect = targetSounds.getSoundEffect(type, target)) == null) {
						type = type.fallback;
						if(type == null) break;
					}
					return targetSoundEffect != null && !targetSoundEffect.isEmpty() ? targetSoundEffect : null;
				}
				return null;
			}
		}
		return soundEffect;
	}
	
	public static void init(String file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Sounds.class.getResourceAsStream(file)));
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(reader);
			for(DisguiseType type : DisguiseType.values()) {
				if(fileConfiguration.isConfigurationSection(type.name())) {
					setSoundsForEntity(type, new Sounds(fileConfiguration.getConfigurationSection(type.name())));
				}
			}
			reader.close();
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required sound effect configuration.", e);
			}
		}
	}
	
	private Map<SoundEffectType, String> typeToSoundEffect;
	private Map<String, SoundEffectType> soundEffectToType;
	
	public Sounds(ConfigurationSection section) {
		for(SoundEffectType type : SoundEffectType.values()) {
			if(section.isList(type.name())) {
				List<String> soundEffects = section.getStringList(type.name());
				typeToSoundEffect.put(type, soundEffects.get(0));
				for(String soundEffect : soundEffects) {
					soundEffectToType.put(soundEffect, type);
				}
			}
		}
	}
	
	public SoundEffectType matchSoundEffect(String soundEffect) {
		return soundEffectToType.get(soundEffect);
	}
	
	public String getSoundEffect(SoundEffectType type) {
		return typeToSoundEffect.get(type);
	}
	
	public String getSoundEffect(SoundEffectType type, Disguise target) {
		return getSoundEffect(type);
	}
	
	public enum SoundEffectType {
		
		HURT(null), DEATH(HURT), SMALL_FALL(null), BIG_FALL(SMALL_FALL), SPLASH(null), SWIM(null), STEP(null), AMBIENT(null), EAT(AMBIENT), ANGRY(AMBIENT);
		
		public final SoundEffectType fallback;
		
		private SoundEffectType(SoundEffectType fallback) {
			this.fallback = fallback;
		}
		
	}
	
}