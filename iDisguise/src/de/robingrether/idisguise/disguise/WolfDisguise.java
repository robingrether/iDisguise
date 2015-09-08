package de.robingrether.idisguise.disguise;

import org.bukkit.DyeColor;

/**
 * Represents a disguise as a wolf.
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class WolfDisguise extends ColoredDisguise {
	
	private static final long serialVersionUID = -6959472065307897736L;
	private boolean tamed;
	private boolean angry;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public WolfDisguise() {
		this(true, DyeColor.RED, false, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param adult should the disguise be an adult
	 * @param collarColor the collar color
	 * @param tamed should the wolf be tamed
	 * @param angry should the wolf be angry
	 */
	public WolfDisguise(boolean adult, DyeColor collarColor, boolean tamed, boolean angry) {
		super(DisguiseType.WOLF, adult, collarColor);
		this.tamed = tamed;
		this.angry = angry;
	}
	
	/**
	 * Gets whether the wolf is tamed.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the wolf is tamed
	 */
	public boolean isTamed() {
		return tamed;
	}
	
	/**
	 * Sets whether the wolf is tamed.
	 * 
	 * @since 3.0.1
	 * @param tamed should the wolf be tamed
	 */
	public void setTamed(boolean tamed) {
		this.tamed = tamed;
	}
	
	/**
	 * Gets whether the wolf is angry.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the wolf is angry
	 */
	public boolean isAngry() {
		return angry;
	}
	
	/**
	 * Sets whether the wolf is angry.
	 * 
	 * @since 3.0.1
	 * @param angry should the wolf be angry
	 */
	public void setAngry(boolean angry) {
		this.angry = angry;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public WolfDisguise clone() {
		WolfDisguise clone = new WolfDisguise(adult, getColor(), tamed, angry);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof WolfDisguise && ((WolfDisguise)object).angry == angry && ((WolfDisguise)object).tamed == tamed;
	}
	
}