package de.robingrether.idisguise.disguise;

import org.bukkit.entity.Ocelot.Type;

/**
 * Represents a disguise as an ocelot.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class OcelotDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -3590935781972579223L;
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
	
}