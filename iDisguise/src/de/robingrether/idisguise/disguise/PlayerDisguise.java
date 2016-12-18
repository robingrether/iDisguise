package de.robingrether.idisguise.disguise;

import java.util.Locale;

import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.util.Validate;

/**
 * Represents a disguise as a player.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class PlayerDisguise extends Disguise {
	
	private static final long serialVersionUID = -1352667720037212662L;
	private final String skinName;
	private String displayName;
	
	/**
	 * Creates an instance.<br>
	 * (normal player disguise, not ghost)
	 * 
	 * @since 2.1.3
	 * @param skinName the player skin
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	public PlayerDisguise(String skinName) {
		this(skinName, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.3.1
	 * @param skinName the player skin
	 * @param ghost <code>true</code> for a ghost, <code>false</code> for a normal player
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	public PlayerDisguise(String skinName, boolean ghost) {
		this(skinName, skinName, ghost);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @param skinName the player skin
	 * @param displayName the display name (above player's head and player list)
	 * @param ghost <code>true</code> for a ghost, <code>false</code> for a normal player
	 * @throws IllegalArgumentException the given skin name is not valid
	 */
	public PlayerDisguise(String skinName, String displayName, boolean ghost) {
		super(ghost ? DisguiseType.GHOST : DisguiseType.PLAYER);
		if(!Validate.minecraftUsername(skinName)) {
			throw new IllegalArgumentException("The given skin name is invalid!");
		}
		this.skinName = skinName.toLowerCase(Locale.ENGLISH);
		this.displayName = displayName;
		PlayerHelper.getInstance().loadGameProfileAsynchronously(this.skinName);
	}
	
	/**
	 * Returns the name.
	 * 
	 * @deprecated replaced by <code>getSkinName()</code>
	 * @since 2.1.3
	 * @return the skin name
	 */
	@Deprecated
	public String getName() {
		return this.skinName;
	}
	
	/**
	 * Returns the skin name.<br>
	 * This is always lower case as of 5.5.2.
	 * 
	 * @since 5.2.2
	 * @return the skin name
	 */
	public String getSkinName() {
		return skinName;
	}
	
	/**
	 * Returns the display name.
	 * 
	 * @since 5.2.2
	 * @return the display name (above player's head and player list)
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets the display name.
	 * 
	 * @since 5.2.2
	 * @param displayName the display name (above player's head and player list)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	 * {@inheritDoc}
	 */
	public PlayerDisguise clone() {
		return new PlayerDisguise(new String(skinName), isGhost());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && ((PlayerDisguise)object).skinName.equalsIgnoreCase(skinName) && ((PlayerDisguise)object).displayName.equals(displayName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s", super.toString(), skinName, displayName);
	}
	
}