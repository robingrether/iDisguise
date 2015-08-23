package de.robingrether.idisguise.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Represents an advanced sound system which randomly plays one of an array of defined sounds on death, hurt, idle.<br>
 * This class can be extended.
 * 
 * @since 2.2.1
 * @author Robingrether
 */
public class AdvancedSoundSystem extends SoundSystem {
	
	protected Sound[] death, hurt, idle;
	
	/**
	 * Creates an advanced sound system from an array of death sounds, an array of hurt sounds and an array of idle sounds.<br>
	 * Each array can be null.
	 * 
	 * @since 2.2.1
	 * @param death the sounds to play on death
	 * @param hurt the sounds to play on hurt
	 * @param idle the sounds to play randomly/idle
	 */
	public AdvancedSoundSystem(Sound[] death, Sound[] hurt, Sound[] idle) {
		this.death = death;
		this.hurt = hurt;
		this.idle = idle;
	}
	
	/**
	 * Randomly chooses a sound to play on death
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return a sound to play on death or null
	 */
	public Sound getDeathSound(Player player) {
		if(death != null) {
			return death[random.nextInt(death.length)];
		} else {
			return null;
		}
	}
	
	/**
	 * Randomly chooses a sound to play on hurt
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return a sound to play on hurt or null
	 */
	public Sound getHurtSound(Player player) {
		if(hurt != null) {
			return hurt[random.nextInt(hurt.length)];
		} else {
			return null;
		}
	}
	
	/**
	 * Randomly chooses a sound to play randomly/idle
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return a sound to play randomly/idle or null
	 */
	public Sound getIdleSound(Player player) {
		if(idle != null) {
			return idle[random.nextInt(idle.length)];
		} else {
			return null;
		}
	}
	
}