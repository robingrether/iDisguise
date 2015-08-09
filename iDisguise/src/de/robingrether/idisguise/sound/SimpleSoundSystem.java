package de.robingrether.idisguise.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Represents a simple sound system which plays a defined sound on death, hurt, idle.<br />
 * This class can be extended.
 * 
 * @since 2.2.1
 * @author Robingrether
 */
public class SimpleSoundSystem extends SoundSystem {
	
	protected Sound death, hurt, idle;
	
	/**
	 * Creates a simple sound system from a death sound, a hurt sound and an idle sound.<br />
	 * Each sound can be null.
	 * 
	 * @since 2.2.1
	 * @param death the sound to play on death
	 * @param hurt the sound to play on hurt
	 * @param idle the sound to play randomly/idle
	 */
	public SimpleSoundSystem(Sound death, Sound hurt, Sound idle) {
		this.death = death;
		this.hurt = hurt;
		this.idle = idle;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2.2.1
	 */
	public Sound getDeathSound(Player player) {
		return death;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2.2.1
	 */
	public Sound getHurtSound(Player player) {
		return hurt;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2.2.1
	 */
	public Sound getIdleSound(Player player) {
		return idle;
	}
	
}