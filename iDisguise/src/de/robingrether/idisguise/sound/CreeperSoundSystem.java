package de.robingrether.idisguise.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Represents a simple sound system for creeper.
 * 
 * @since 2.2.1
 * @author Robingrether
 */
public class CreeperSoundSystem extends SimpleSoundSystem {
	
	/**
	 * Creates a simple sound system for creeper
	 * 
	 * @since 2.2.1
	 */
	public CreeperSoundSystem() {
		super(Sound.CREEPER_DEATH, Sound.CREEPER_DEATH, Sound.CREEPER_HISS);
	}
	
	/**
	 * Checks if another player is nearby and returns a sound or null
	 * 
	 * @since 2.2.1
	 * @param player the disguised player
	 * @return a sound to play randomly/idle or null
	 */
	public Sound getIdleSound(Player player) {
		boolean isPlayerNearby = false;
		for(Entity entity : player.getNearbyEntities(3.0, 3.0, 3.0)) {
			if(entity instanceof Player) {
				isPlayerNearby = true;
			}
		}
		if(isPlayerNearby) {
			return idle;
		} else {
			return null;
		}
	}
	
}