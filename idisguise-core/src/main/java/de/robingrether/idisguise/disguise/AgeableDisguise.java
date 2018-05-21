package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise that can be both an adult or a baby.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class AgeableDisguise extends MobDisguise {
	
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
	public String toString() {
		return String.format("%s; %s", super.toString(), adult ? "adult" : "baby");
	}
	
	static {
		Subtypes.registerSubtype(AgeableDisguise.class, "setAdult", true, "adult");
		Subtypes.registerSubtype(AgeableDisguise.class, "setAdult", false, "baby");
	}
	
}