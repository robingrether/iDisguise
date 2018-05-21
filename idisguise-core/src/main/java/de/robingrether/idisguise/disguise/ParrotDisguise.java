package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a parrot.
 * 
 * @since 5.6.3
 * @author RobinGrether
 */
public class ParrotDisguise extends MobDisguise {
	
	private Variant variant;
	private boolean sitting;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.6.3
	 */
	public ParrotDisguise() {
		this(Variant.RED);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.6.3
	 */
	public ParrotDisguise(Variant variant) {
		super(DisguiseType.PARROT);
		this.variant = variant;
	}
	
	/**
	 * Returns the variant.
	 * 
	 * @since 5.6.3
	 * @return the variant
	 */
	public Variant getVariant() {
		return variant;
	}
	
	/**
	 * Sets the variant.
	 * 
	 * @since 5.6.3
	 * @param variant the variant
	 */
	public void setVariant(Variant variant) {
		this.variant = variant;
	}
	
	/**
	 * @since 5.7.1
	 */
	public boolean isSitting() {
		return sitting;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setSitting(boolean sitting) {
		this.sitting = sitting;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s", super.toString(), variant.name().toLowerCase(Locale.ENGLISH), sitting ? "sitting" : "not-sitting");
	}
	
	static {
		for(Variant variant : Variant.values()) {
			Subtypes.registerSubtype(ParrotDisguise.class, "setVariant", variant, variant.name().toLowerCase(Locale.ENGLISH));
		}
		Subtypes.registerSubtype(ParrotDisguise.class, "setSitting", true, "sitting");
		Subtypes.registerSubtype(ParrotDisguise.class, "setSitting", false, "not-sitting");
	}
	
	/**
	 * Different variants a parrot can be.
	 * 
	 * @since 5.6.3
	 * @author RobinGrether
	 */
	public enum Variant {
		
		RED,
		BLUE,
		GREEN,
		CYAN,
		GRAY;
		
	}
	
}