package de.robingrether.idisguise.disguise;

import java.util.Locale;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as a boat.
 * 
 * @since 5.7.1
 * @author RobinGrether
 */
public class BoatDisguise extends ObjectDisguise {
	
	private BoatType boatType;
	
	/**
	 * @since 5.7.1
	 */
	public BoatDisguise() {
		this(BoatType.OAK);
	}
	
	/**
	 * @since 5.7.1
	 * @param boatType change the wood type of the boat (works in Minecraft 1.9+ only)
	 */
	public BoatDisguise(BoatType boatType) {
		super(DisguiseType.BOAT);
		this.boatType = boatType;
	}
	
	/**
	 * @since 5.7.1
	 */
	public BoatType getBoatType() {
		return boatType;
	}
	
	/**
	 * @since 5.7.1
	 * @param boatType change the wood type of the boat (works in Minecraft 1.9+ only)
	 */
	public void setBoatType(BoatType boatType) {
		this.boatType = boatType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s", super.toString(), boatType.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	/**
	 * Different types of wood a boat can be made out of.
	 * 
	 * @since 5.7.1
	 * @author RobinGrether
	 */
	public enum BoatType {
		OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK;
	}
	
	static {
		if(VersionHelper.require1_9()) {
			for(BoatType boatType : BoatType.values()) {
				Subtypes.registerSubtype(BoatDisguise.class, "setBoatType", boatType, boatType.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
	}
	
}