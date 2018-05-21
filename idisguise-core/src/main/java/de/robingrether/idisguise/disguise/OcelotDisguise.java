package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.entity.Ocelot.Type;

/**
 * Represents a disguise as an ocelot.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class OcelotDisguise extends AgeableDisguise {
	
	private Type catType;
	private boolean sitting;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public OcelotDisguise() {
		this(Type.WILD_OCELOT, true);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param catType the cat type
	 * @param adult should the disguise be an adult
	 */
	public OcelotDisguise(Type catType, boolean adult) {
		super(DisguiseType.OCELOT, adult);
		this.catType = catType;
	}
	
	/**
	 * Gets the cat type.
	 * 
	 * @since 3.0.1
	 * @return the cat type
	 */
	public Type getCatType() {
		return catType;
	}
	
	/**
	 * Sets the cat type.
	 * 
	 * @since 3.0.1
	 * @param catType the cat type
	 */
	public void setCatType(Type catType) {
		this.catType = catType;
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
		return String.format("%s; %s; %s", super.toString(), catType.name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""), sitting ? "sitting" : "not-sitting");
	}
	
	static {
		for(Type catType : Type.values()) {
			Subtypes.registerSubtype(OcelotDisguise.class, "setCatType", catType, catType.name().toLowerCase(Locale.ENGLISH).replaceAll("_.*", ""));
		}
		Subtypes.registerSubtype(OcelotDisguise.class, "setSitting", true, "sitting");
		Subtypes.registerSubtype(OcelotDisguise.class, "setSitting", false, "not-sitting");
	}
	
}