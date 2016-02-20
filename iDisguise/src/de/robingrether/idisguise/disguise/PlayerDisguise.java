package de.robingrether.idisguise.disguise;

import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.util.Validate;

/**
 * Represents a disguise as a player.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class PlayerDisguise extends Disguise {
	
	private static final long serialVersionUID = -5730346799961856914L;
	private final String name;
	private String playerListName;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.1.3
	 * @param name the player to disguise as
	 * @throws IllegalArgumentException the given name is not valid.
	 */
	public PlayerDisguise(String name) {
		this(name, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.3.1
	 * @param name the player to disguise as
	 * @param ghost whether the disguise should be a ghost or not
	 * @throws IllegalArgumentException the given name is not valid.
	 */
	public PlayerDisguise(String name, boolean ghost) {
		super(ghost ? DisguiseType.GHOST : DisguiseType.PLAYER);
		if(!Validate.minecraftUsername(name)) {
			throw new IllegalArgumentException("The given name is invalid!");
		}
		this.name = this.playerListName = PlayerHelper.instance.getCaseCorrectedName(name);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param name the player to disguise as
	 * @param ghost whether the disguise should be a ghost or not
	 * @param playerListName the name to show in the player list
	 * @throws IllegalArgumentException the given name is not valid.
	 */
	public PlayerDisguise(String name, boolean ghost, String playerListName) {
		super(ghost ? DisguiseType.GHOST : DisguiseType.PLAYER);
		if(!Validate.minecraftUsername(name)) {
			throw new IllegalArgumentException("The given name is invalid!");
		}
		this.name = PlayerHelper.instance.getCaseCorrectedName(name);
		this.playerListName = playerListName;
	}
	
	/**
	 * Returns the name.
	 * 
	 * @since 2.1.3
	 * @return the player's name to disguise as
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks whether this disguise is a ghost.
	 * 
	 * @since 2.3.1
	 * @return <code>true</code> if this is a ghost
	 */
	public boolean isGhost() {
		return type == DisguiseType.GHOST;
	}
	
	/**
	 * Gets the player list name.
	 * 
	 * @since 5.1.1
	 * @return the player list name
	 */
	public String getPlayerListName() {
		return playerListName;
	}
	
	/**
	 * Sets the player list name.
	 * 
	 * @since 5.1.1
	 * @param playerListName the new player list name
	 */
	public void setPlayerListName(String playerListName) {
		this.playerListName = playerListName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PlayerDisguise clone() {
		return new PlayerDisguise(new String(name), isGhost());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof PlayerDisguise && ((PlayerDisguise)object).name.equalsIgnoreCase(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		return false;
	}
	
}