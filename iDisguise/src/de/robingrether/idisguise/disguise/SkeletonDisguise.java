package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.entity.Skeleton.SkeletonType;

/**
 * Represents a disguise as a skeleton.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class SkeletonDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -5133866912715315405L;
	private SkeletonType skeletonType;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public SkeletonDisguise() {
		this(SkeletonType.NORMAL);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param skeletonType the skeleton type
	 */
	public SkeletonDisguise(SkeletonType skeletonType) {
		super(DisguiseType.SKELETON);
		this.skeletonType = skeletonType;
	}
	
	/**
	 * Returns the skeleton type.
	 * 
	 * @since 4.0.1
	 * @return the skeleton type
	 */
	public SkeletonType getSkeletonType() {
		return skeletonType;
	}
	
	/**
	 * Sets the skeleton type.
	 * 
	 * @since 4.0.1
	 * @param skeletonType the skeleton type
	 */
	public void setSkeletonType(SkeletonType skeletonType) {
		this.skeletonType = skeletonType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SkeletonDisguise clone() {
		SkeletonDisguise clone = new SkeletonDisguise(skeletonType);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof SkeletonDisguise && ((SkeletonDisguise)object).skeletonType == skeletonType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + skeletonType.name().toLowerCase(Locale.ENGLISH);
	}
	
	static {
		for(SkeletonType skeletonType : SkeletonType.values()) {
			Subtypes.registerSubtype(SkeletonDisguise.class, "setSkeletonType", skeletonType, skeletonType.name().toLowerCase(Locale.ENGLISH));
		}
	}
	
}