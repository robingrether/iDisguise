package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a mob.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class MobDisguise extends Disguise {
	
	private static final long serialVersionUID = -8536172774722123370L;
	protected boolean adult;
	protected String customName = null;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the type to disguise as
	 */
	public MobDisguise(DisguiseType type) {
		this(type, true);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.1.3
	 * @param type the type to disguise as
	 * @param adult should the disguise be an adult
	 * @throws IllegalArgumentException DisguiseType is not a mob.
	 */
	public MobDisguise(DisguiseType type, boolean adult) {
		super(type);
		if(!type.isMob()) {
			throw new IllegalArgumentException("DisguiseType must be a mob");
		}
		this.adult = adult;
	}
	
	/**
	 * Checks whether the disguise is an adult.
	 * 
	 * @since 2.1.3
	 * @return true if it's an adult, false if not
	 */
	public boolean isAdult() {
		return this.adult;
	}
	
	/**
	 * Sets if the disguise is an adult.
	 * 
	 * @since 2.1.3
	 * @param adult should the disguise be an adult
	 */
	public void setAdult(boolean adult) {
		this.adult = adult;
	}
	
	/**
	 * Gets the custom name of this entity.<br>
	 * The default value is <code>null</code>.
	 * 
	 * @since 3.0.1
	 * @return the custom name
	 */
	public String getCustomName() {
		return customName;
	}
	
	/**
	 * Sets the custom name of this entity.<br>
	 * The default value is <code>null</code>.
	 * 
	 * @since 3.0.1
	 * @param customName the custom name
	 */
	public void setCustomName(String customName) {
		if(customName != null && customName.length() > 64) {
			customName = customName.substring(0, 64);
		}
		this.customName = customName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MobDisguise clone() {
		MobDisguise clone = new MobDisguise(type, adult);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof MobDisguise && ((MobDisguise)object).adult == adult;
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