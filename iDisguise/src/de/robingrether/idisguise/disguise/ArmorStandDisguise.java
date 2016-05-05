package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as an armor stand.
 * 
 * @since 5.2.2
 * @author RobinGrether
 */
public class ArmorStandDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = 7786128991288922802L;
	private boolean showArms;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 */
	public ArmorStandDisguise() {
		this(false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @param showArms whether the armor stand should have arms or not
	 */
	public ArmorStandDisguise(boolean showArms) {
		super(DisguiseType.ARMOR_STAND);
		this.showArms = showArms;
	}
	
	/**
	 * Indicates whether the armor stand has arms or not.
	 * 
	 * @since 5.2.2
	 * @return <code>true</code> if the armor stand has arms, <code>false</code> otherwise
	 */
	public boolean getShowArms() {
		return showArms;
	}
	
	/**
	 * Sets whether the armor stand should have arms or not.
	 * 
	 * @since 5.2.2
	 * @param showArms <code>true</code> if the armor stand should have arms, <code>false</code> otherwise
	 */
	public void setShowArms(boolean showArms) {
		this.showArms = showArms;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ArmorStandDisguise clone() {
		return new ArmorStandDisguise(showArms);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ArmorStandDisguise && ((ArmorStandDisguise)object).showArms == showArms;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
			switch(argument.replace('-', '_').toLowerCase(Locale.ENGLISH)) {
				case "arms":
				case "show_arms":
				case "showarms":
				case "has_arms":
				case "hasarms":
				case "with_arms":
				case "witharms":
					setShowArms(true);
					return true;
				case "no_arms":
				case "noarms":
				case "hide_arms":
				case "hidearms":
					setShowArms(false);
					return true;
				default:
					return false;
			}
	}
	
}