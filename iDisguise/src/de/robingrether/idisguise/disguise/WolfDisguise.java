package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.DyeColor;

/**
 * Represents a disguise as a wolf.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class WolfDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -2279695225712768959L;
	private DyeColor collarColor;
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
		super(DisguiseType.WOLF, adult);
		this.collarColor = collarColor;
		this.tamed = tamed;
		this.angry = angry;
	}
	
	/**
	 * Returns the collar color.
	 * 
	 * @since 5.1.1
	 * @return the collar color
	 */
	public DyeColor getCollarColor() {
		return collarColor;
	}
	
	/**
	 * Sets the collar color.
	 * 
	 * @since 5.1.1
	 * @param collarColor the collar color
	 */
	public void setCollarColor(DyeColor collarColor) {
		this.collarColor = collarColor;
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
		WolfDisguise clone = new WolfDisguise(adult, collarColor, tamed, angry);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof WolfDisguise && ((WolfDisguise)object).collarColor.equals(collarColor) && ((WolfDisguise)object).angry == angry && ((WolfDisguise)object).tamed == tamed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			try {
				DyeColor collarColor = DyeColor.valueOf(argument.replace('-', '_').toUpperCase(Locale.ENGLISH));
				setCollarColor(collarColor);
				return true;
			} catch(IllegalArgumentException e) {
			}
			switch(argument.replace('-', '_').toLowerCase(Locale.ENGLISH)) {
				case "tamed":
				case "tame":
					setTamed(true);
					return true;
				case "not_tamed":
				case "nottamed":
				case "not_tame":
				case "nottame":
					setTamed(false);
					return true;
				case "angry":
				case "aggressive":
					setAngry(true);
					return true;
				case "not_angry":
				case "notangry":
				case "not_aggressive":
				case "notaggressive":
					setAngry(false);
					return true;
				default:
					return false;
			}
		}
	}
	
}