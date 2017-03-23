package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.DyeColor;

/**
 * Represents a disguise as a wolf.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class WolfDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -6203460877408219137L;
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
		if(tamed) this.angry = false;
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
		if(angry) this.tamed = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + collarColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-') + "; " + (tamed ? "tamed" : "not-tamed") + "; " + (angry ? "angry" : "not-angry");
	}
	
	static {
		for(DyeColor collarColor : DyeColor.values()) {
			Subtypes.registerSubtype(WolfDisguise.class, "setCollarColor", collarColor, collarColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerSubtype(WolfDisguise.class, "setTamed", true, "tamed");
		Subtypes.registerSubtype(WolfDisguise.class, "setTamed", false, "not-tamed");
		Subtypes.registerSubtype(WolfDisguise.class, "setAngry", true, "angry");
		Subtypes.registerSubtype(WolfDisguise.class, "setAngry", false, "not-angry");
	}
	
}