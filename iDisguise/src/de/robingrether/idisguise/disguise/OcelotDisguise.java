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
	
	private static final long serialVersionUID = -1849874936924669239L;
	private Type catType;
	
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
	 * {@inheritDoc}
	 */
	public OcelotDisguise clone() {
		OcelotDisguise clone = new OcelotDisguise(catType, adult);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof OcelotDisguise && ((OcelotDisguise)object).catType.equals(catType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			switch(argument.toLowerCase(Locale.ENGLISH)) {
				case "black":
					setCatType(Type.BLACK_CAT);
					return true;
				case "red":
					setCatType(Type.RED_CAT);
					return true;
				case "siamese":
					setCatType(Type.SIAMESE_CAT);
					return true;
				case "wild":
					setCatType(Type.WILD_OCELOT);
					return true;
				default:
					return false;
			}
		}
	}
	
}