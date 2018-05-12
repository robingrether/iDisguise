package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents a disguise as a mob.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public class MobDisguise extends Disguise {
	
	private static final long serialVersionUID = 7587147403290078928L;
	private String customName = "";
	private boolean customNameVisible = true;
	
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
	 * Gets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 3.0.1
	 * @return the custom name
	 */
	public String getCustomName() {
		return customName;
	}
	
	/**
	 * Sets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 3.0.1
	 * @param customName the custom name
	 */
	public void setCustomName(String customName) {
		if(customName == null) {
			customName = "";
		} else if(customName.length() > 64) {
			customName = customName.substring(0, 64);
		}
		this.customName = customName;
	}
	
	/**
	 * Indicates whether the custom name of this entity is visible all the time.<br>
	 * The default value is <code>true</code>.
	 * 
	 * @since 5.6.3
	 * @return <code>true</code>, if the custom name is visible all the time
	 */
	public boolean isCustomNameVisible() {
		return customNameVisible;
	}
	
	/**
	 * Sets whether the custom name of this entity is visible all the time.<br>
	 * The default value is <code>true</code>.<br>
	 * This value has no effect if the custom name is empty.
	 * 
	 * @since 5.6.3
	 * @param customNameVisible <code>true</code>, if the custom name shall be visible all the time
	 */
	public void setCustomNameVisible(boolean customNameVisible) {
		this.customNameVisible = customNameVisible;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; custom-name=%s; %s", super.toString(), customName, customNameVisible ? "custom-name-visible" : "custom-name-invisible");
	}
	
	static {
		Subtypes.registerParameterizedSubtype(MobDisguise.class, "setCustomName", "custom-name", String.class, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("Hello\\sWorld!", "Notch", "I'm\\syour\\sfather"))));
		Subtypes.registerSubtype(MobDisguise.class, "setCustomNameVisible", true, "custom-name-visible");
		Subtypes.registerSubtype(MobDisguise.class, "setCustomNameVisible", false, "custom-name-invisible");
	}
	
}