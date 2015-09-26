package de.robingrether.idisguise.api;

import org.bukkit.entity.Player;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.sound.SoundSystem;

/**
 * The API to hook into iDisguise. The following code returns an object:<br>
 * <code>Bukkit.getServicesManager().getRegistration(DisguiseAPI.class).getProvider();</code>
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public interface DisguiseAPI {
	
	/**
	 * Disguises a player.
	 * 
	 * @since 3.0.1
	 * @param player the player to disguise
	 * @param disguise the disguise
	 */
	public void disguiseToAll(Player player, Disguise disguise);
	
	/**
	 * Undisguises a player.
	 * 
	 * @since 2.1.3
	 * @param player the player to undisguise
	 */
	public void undisguiseToAll(Player player);
	
	/**
	 * Undisguises all players.
	 * 
	 * @since 2.1.3
	 */
	public void undisguiseAll();
	
	/**
	 * Checks whether a player is disguised.
	 * 
	 * @since 2.1.3
	 * @param player the player to check
	 * @return true if disguised, false if not
	 */
	public boolean isDisguised(Player player);
	
	/**
	 * Gets a copy of a player's disguise.
	 * 
	 * @since 2.1.3
	 * @param player the player
	 * @return the disguise or null if not disguised
	 */
	public Disguise getDisguise(Player player);
	
	/**
	 * Counts the amount of online players who are disguised.
	 * 
	 * @since 2.1.3
	 * @return the counted amount
	 */
	public int getOnlineDisguiseCount();
	
	/**
	 * Gets the current locale for iDisguise.
	 * 
	 * @deprecated Localized messages are not supported anymore.
	 * @since 2.2.1
	 * @return always returns <code>enUS</code>, since this is not supported anymore
	 */
	@Deprecated
	public String getLocale();
	
	/**
	 * Gets a localized phrase.
	 * 
	 * @deprecated Localized messages are not supported anymore.
	 * @since 2.2.1
	 * @param name the name of the phrase
	 * @return always returns <code>null</code>, since this is not supported anymore
	 */
	@Deprecated
	public String getLocalizedPhrase(String name);
	
	/**
	 * Gets the current sound system for the given disguise type.
	 * 
	 * @since 2.2.1
	 * @deprecated the current sound system will be reworked in the future
	 * @param type the disguise type
	 * @return the current sound system
	 */
	@Deprecated
	public SoundSystem getSoundSystem(DisguiseType type);
	
	/**
	 * Sets a sound system as the new sound system for the given disguise type.
	 * 
	 * @since 2.2.1
	 * @deprecated the current sound system will be reworked in the future
	 * @param type the disguise type
	 * @param soundSystem the new sound system
	 */
	@Deprecated
	public void setSoundSystem(DisguiseType type, SoundSystem soundSystem);
	
}