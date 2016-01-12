package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a pig.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class PigDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 8040481135931636309L;
	private boolean saddled;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public PigDisguise() {
		this(true, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param adult should the disguise be an adult
	 * @param saddled should the disguise be saddled
	 */
	public PigDisguise(boolean adult, boolean saddled) {
		super(DisguiseType.PIG, adult);
		this.saddled = saddled;
	}
	
	/**
	 * Gets whether the pig is saddled.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the pig is saddled
	 */
	public boolean isSaddled() {
		return saddled;
	}
	
	/**
	 * Sets whether the pig is saddled.
	 * 
	 * @since 3.0.1
	 * @param saddled should the pig be saddled
	 */
	public void setSaddled(boolean saddled) {
		this.saddled = saddled;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PigDisguise clone() {
		PigDisguise clone = new PigDisguise(adult, saddled);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof PigDisguise && ((PigDisguise)object).saddled == saddled;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			switch(argument.replace('-', '_').toLowerCase(Locale.ENGLISH)) {
				case "saddled":
				case "saddle":
					setSaddled(true);
					return true;
				case "not_saddled":
				case "notsaddled":
				case "no_saddle":
				case "nosaddle":
					setSaddled(false);
					return true;
				default:
					return false;
			}
		}
	}
	
}