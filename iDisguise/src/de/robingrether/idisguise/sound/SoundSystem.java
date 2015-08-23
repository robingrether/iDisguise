package de.robingrether.idisguise.sound;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.robingrether.idisguise.disguise.DisguiseType;

/**
 * This class represents a sound system.<br>
 * This class also manages the sound systems.
 * 
 * @since 2.2.1
 * @author Robingrether
 */
public abstract class SoundSystem {
	
	private static final ConcurrentHashMap<DisguiseType, SoundSystem> soundSystems;
	private static final Random staticRandom;
	private static boolean enabled = false;
	protected Random random;
	
	public SoundSystem() {
		random = new Random();
	}
	
	/**
	 * Gets the sound to play on death
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return the sound to play
	 */
	public abstract Sound getDeathSound(Player player);
	
	/**
	 * Gets the sound to play on hurt
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return the sound to play
	 */
	public abstract Sound getHurtSound(Player player);
	
	/**
	 * Gets an idle sound to play, happens randomly
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return the sound to play
	 */
	public abstract Sound getIdleSound(Player player);
	
	/**
	 * Gets the current sound system for a specific disguise type
	 * 
	 * @since 2.2.1
	 * @param type the disguise type
	 * @return the current sound system
	 */
	public static SoundSystem getSoundSystem(DisguiseType type) {
		if(soundSystems.containsKey(type)) {
			return soundSystems.get(type);
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a sound system as the new sound system for a specific disguise type
	 * 
	 * @since 2.2.1
	 * @param type the disguise type
	 * @param soundSystem the new sound system
	 */
	public static void setSoundSystem(DisguiseType type, SoundSystem soundSystem) {
		soundSystems.put(type, soundSystem);
	}
	
	/**
	 * Checks whether the sound system is enabled
	 * 
	 * @since 2.2.1
	 * @return true if it is enabled, false if not
	 */
	public static boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets whether the sound system is enabled
	 * 
	 * @since 2.2.1
	 * @param enabled whether the sound system should be enabled
	 */
	public static void setEnabled(boolean enabled) {
		SoundSystem.enabled = enabled;
	}
	
	public static void playDeathSound(Player player, DisguiseType type) {
		if(enabled) {
			SoundSystem soundSystem = getSoundSystem(type);
			if(soundSystem != null) {
				Sound sound = soundSystem.getDeathSound(player);
				if(sound != null) {
					player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
				}
			}
		}
	}
	
	public static void playHurtSound(Player player, DisguiseType type) {
		if(enabled) {
			SoundSystem soundSystem = getSoundSystem(type);
			if(soundSystem != null) {
				Sound sound = soundSystem.getHurtSound(player);
				if(sound != null) {
					player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
				}
			}
		}
	}
	
	public static void playIdleSound(Player player, DisguiseType type) {
		if(enabled && staticRandom.nextInt(50) == 1) {
			SoundSystem soundSystem = getSoundSystem(type);
			if(soundSystem != null) {
				Sound sound = soundSystem.getIdleSound(player);
				if(sound != null) {
					player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
				}
			}
		}
	}
	
	static {
		soundSystems = new ConcurrentHashMap<DisguiseType, SoundSystem>();
		setSoundSystem(DisguiseType.BAT, new SimpleSoundSystem(Sound.BAT_DEATH, Sound.BAT_HURT, Sound.BAT_IDLE));
		setSoundSystem(DisguiseType.BLAZE, new SimpleSoundSystem(Sound.BLAZE_DEATH, Sound.BLAZE_HIT, Sound.BLAZE_BREATH));
		setSoundSystem(DisguiseType.CAVE_SPIDER, new AdvancedSoundSystem(new Sound[] {Sound.SPIDER_DEATH}, null, new Sound[] {Sound.SPIDER_IDLE, Sound.SPIDER_WALK}));
		setSoundSystem(DisguiseType.CHICKEN, new AdvancedSoundSystem(new Sound[] {Sound.CHICKEN_HURT}, new Sound[] {Sound.CHICKEN_HURT}, new Sound[] {Sound.CHICKEN_IDLE, Sound.CHICKEN_WALK}));
		setSoundSystem(DisguiseType.COW, new AdvancedSoundSystem(new Sound[] {Sound.COW_HURT}, new Sound[] {Sound.COW_HURT}, new Sound[] {Sound.COW_IDLE, Sound.COW_WALK}));
		setSoundSystem(DisguiseType.CREEPER, new CreeperSoundSystem());
		setSoundSystem(DisguiseType.ENDER_DRAGON, new SimpleSoundSystem(Sound.ENDERDRAGON_DEATH, Sound.ENDERDRAGON_HIT, Sound.ENDERDRAGON_GROWL));
		setSoundSystem(DisguiseType.ENDERMAN, new SimpleSoundSystem(Sound.ENDERMAN_DEATH, Sound.ENDERMAN_HIT, Sound.ENDERMAN_IDLE));
		setSoundSystem(DisguiseType.GHAST, new AdvancedSoundSystem(new Sound[] {Sound.GHAST_DEATH}, null, new Sound[] {Sound.GHAST_MOAN, Sound.GHAST_SCREAM, Sound.GHAST_SCREAM2}));
		setSoundSystem(DisguiseType.HORSE, new AdvancedSoundSystem(null, new Sound[] {Sound.HORSE_HIT}, new Sound [] {Sound.HORSE_BREATHE, Sound.HORSE_IDLE}));
		setSoundSystem(DisguiseType.IRON_GOLEM, new SimpleSoundSystem(Sound.IRONGOLEM_DEATH, Sound.IRONGOLEM_HIT, Sound.IRONGOLEM_WALK));
		setSoundSystem(DisguiseType.MAGMA_CUBE, new AdvancedSoundSystem(null, null, new Sound[] {Sound.MAGMACUBE_JUMP, Sound.MAGMACUBE_WALK, Sound.MAGMACUBE_WALK2}));
		setSoundSystem(DisguiseType.MUSHROOM_COW, new AdvancedSoundSystem(new Sound[] {Sound.COW_HURT}, new Sound[] {Sound.COW_HURT}, new Sound[] {Sound.COW_IDLE, Sound.COW_WALK}));
		setSoundSystem(DisguiseType.OCELOT, new AdvancedSoundSystem(null, new Sound[] {Sound.CAT_HISS, Sound.CAT_HIT}, new Sound[] {Sound.CAT_MEOW}));
		setSoundSystem(DisguiseType.PIG, new AdvancedSoundSystem(new Sound[] {Sound.PIG_DEATH}, null, new Sound[] {Sound.PIG_IDLE, Sound.PIG_WALK}));
		setSoundSystem(DisguiseType.PIG_ZOMBIE, new SimpleSoundSystem(Sound.ZOMBIE_PIG_DEATH, Sound.ZOMBIE_PIG_HURT, Sound.ZOMBIE_PIG_IDLE));
		setSoundSystem(DisguiseType.SHEEP, new AdvancedSoundSystem(null, null, new Sound[] {Sound.SHEEP_IDLE, Sound.SHEEP_WALK}));
		setSoundSystem(DisguiseType.SILVERFISH, new AdvancedSoundSystem(new Sound[] {Sound.SILVERFISH_KILL}, new Sound[] {Sound.SILVERFISH_HIT}, new Sound[] {Sound.SILVERFISH_IDLE, Sound.SILVERFISH_WALK}));
		setSoundSystem(DisguiseType.SKELETON, new AdvancedSoundSystem(new Sound[] {Sound.SKELETON_DEATH}, new Sound[] {Sound.SKELETON_HURT}, new Sound[] {Sound.SKELETON_IDLE, Sound.SKELETON_WALK}));
		setSoundSystem(DisguiseType.SLIME, new AdvancedSoundSystem(null, new Sound[] {Sound.SLIME_ATTACK}, new Sound[] {Sound.SLIME_WALK, Sound.SLIME_WALK2}));
		setSoundSystem(DisguiseType.SPIDER, new AdvancedSoundSystem(new Sound[] {Sound.SPIDER_DEATH}, null, new Sound[] {Sound.SPIDER_IDLE, Sound.SPIDER_WALK}));
		setSoundSystem(DisguiseType.VILLAGER, new SimpleSoundSystem(Sound.VILLAGER_DEATH, Sound.VILLAGER_HIT, Sound.VILLAGER_IDLE));
		setSoundSystem(DisguiseType.WITHER, new SimpleSoundSystem(Sound.WITHER_DEATH, Sound.WITHER_HURT, Sound.WITHER_IDLE));
		setSoundSystem(DisguiseType.WOLF, new AdvancedSoundSystem(new Sound[] {Sound.WOLF_DEATH}, new Sound[] {Sound.WOLF_HOWL, Sound.WOLF_HURT, Sound.WOLF_WHINE}, new Sound[] {Sound.WOLF_BARK, Sound.WOLF_PANT, Sound.WOLF_WALK}));
		setSoundSystem(DisguiseType.ZOMBIE, new SimpleSoundSystem(Sound.ZOMBIE_DEATH, Sound.ZOMBIE_HURT, Sound.ZOMBIE_IDLE));
		staticRandom = new Random();
	}
	
}