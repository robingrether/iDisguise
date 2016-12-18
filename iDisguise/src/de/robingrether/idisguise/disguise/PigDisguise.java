package de.robingrether.idisguise.disguise;

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
		return super.equals(object) && ((PigDisguise)object).saddled == saddled;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + (saddled ? "saddled" : "not-saddled");
	}
	
	static {
		Subtypes.registerSubtype(PigDisguise.class, "setSaddled", true, "saddled");
		Subtypes.registerSubtype(PigDisguise.class, "setSaddled", false, "not-saddled");
	}
	
}