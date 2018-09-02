package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise as an armor stand.
 * 
 * @since 5.2.2
 * @author RobinGrether
 */
public class ArmorStandDisguise extends ObjectDisguise {
	
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
	public String toString() {
		return String.format("%s; %s", super.toString(), showArms ? "show-arms" : "hide-arms");
	}
	
	static {
		Subtypes.registerSimpleSubtype(ArmorStandDisguise.class, disguise -> disguise.setShowArms(true), "show-arms");
		Subtypes.registerSimpleSubtype(ArmorStandDisguise.class, disguise -> disguise.setShowArms(false), "hide-arms");
	}
	
}