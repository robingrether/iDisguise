package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise as a mob.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class MobDisguise extends Disguise {
	
	private static final long serialVersionUID = 7587147403290078928L;
	protected String customName = null;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the type to disguise as
	 */
	public MobDisguise(DisguiseType type) {
		super(type);
		if(!type.isMob()) {
			throw new IllegalArgumentException("DisguiseType must be a mob");
		}
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @deprecated Functionality moved to {@linkplain AgeableDisguise}.
	 * @since 2.1.3
	 * @param type the type to disguise as
	 * @param adult should the disguise be an adult
	 * @throws IllegalArgumentException DisguiseType is not a mob.
	 */
	@Deprecated
	public MobDisguise(DisguiseType type, boolean adult) {
		this(type);
	}
	
	/**
	 * Checks whether the disguise is an adult.
	 * 
	 * @deprecated Functionality moved to {@linkplain AgeableDisguise}.
	 * @since 2.1.3
	 * @return true if it's an adult, false if not
	 */
	@Deprecated
	public boolean isAdult() {
		if(this instanceof AgeableDisguise) {
			return ((AgeableDisguise)this).isAdult();
		}
		return true;
	}
	
	/**
	 * Sets if the disguise is an adult.
	 * 
	 * @deprecated Functionality moved to {@linkplain AgeableDisguise}.
	 * @since 2.1.3
	 * @param adult should the disguise be an adult
	 */
	@Deprecated
	public void setAdult(boolean adult) {
		if(this instanceof AgeableDisguise) {
			((AgeableDisguise)this).setAdult(adult);
		}
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
		MobDisguise clone = new MobDisguise(type);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof MobDisguise;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		return false;
	}
	
}