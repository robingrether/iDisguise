package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise that can be both an adult or a baby.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class AgeableDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -2234799840705344148L;
	protected boolean adult;
	
	/**
	 * Create an instance.
	 * 
	 * @since 5.1.1
	 * @param type the type to disguise as
	 */
	public AgeableDisguise(DisguiseType type) {
		this(type, true);
	}
	
	/**
	 * Create an instance.
	 * 
	 * @since 5.1.1
	 * @param type the type to disguise as
	 * @param adult whether the disguise should appear as an adult or as a baby
	 */
	public AgeableDisguise(DisguiseType type, boolean adult) {
		super(type);
		this.adult = adult;
	}
	
	/**
	 * Indicate whether this disguise appears as an adult or as a baby.
	 * 
	 * @since 5.1.1
	 * @return <code>true</code>, if it appears as an adult, <code>false</code> otherwise
	 */
	public boolean isAdult() {
		return this.adult;
	}
	
	/**
	 * Change whether this disguise appears as an adult or as a baby.
	 * 
	 * @since 5.1.1
	 * @param adult whether the disguise should appear as an adult or as a baby
	 */
	public void setAdult(boolean adult) {
		this.adult = adult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public AgeableDisguise clone() {
		AgeableDisguise clone = new AgeableDisguise(type, adult);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof AgeableDisguise && ((AgeableDisguise)object).adult == adult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		switch(argument.toLowerCase(Locale.ENGLISH)) {
			case "adult":
			case "senior":
				setAdult(true);
				return true;
			case "baby":
			case "child":
			case "kid":
			case "junior":
				setAdult(false);
				return true;
			default:
				return false;
		}
	}
	
}