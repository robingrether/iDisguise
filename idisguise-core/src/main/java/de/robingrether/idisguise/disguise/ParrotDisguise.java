package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a parrot.
 * 
 * @since 5.6.3
 * @author RobinGrether
 */
public class ParrotDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -6569588312342758585L;
	private Variant variant;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.6.3
	 */
	public ParrotDisguise() {
		this(true, Variant.RED);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.6.3
	 */
	public ParrotDisguise(boolean adult, Variant variant) {
		super(DisguiseType.PARROT, adult);
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
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + variant.name().toLowerCase(Locale.ENGLISH);
	}
	
	static {
		for(Variant variant : Variant.values()) {
			Subtypes.registerSubtype(ParrotDisguise.class, "setVariant", variant, variant.name().toLowerCase(Locale.ENGLISH));
		}
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