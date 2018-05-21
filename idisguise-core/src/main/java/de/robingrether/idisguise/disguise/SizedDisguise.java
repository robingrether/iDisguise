package de.robingrether.idisguise.disguise;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a sized mob (e.g. slime, magma slime).
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class SizedDisguise extends MobDisguise {
	
	private int size;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the disguise type (must be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
	 */
	public SizedDisguise(DisguiseType type) {
		this(type, 2);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param type the disguise type (must be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
	 * @param size the size (must not be negative)
	 */
	public SizedDisguise(DisguiseType type, int size) {
		super(type);
		if(!ObjectUtil.equals(type, DisguiseType.SLIME, DisguiseType.MAGMA_CUBE)) {
			throw new IllegalArgumentException();
		}
		this.size = size;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @since 3.0.1
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Sets the size.
	 * 
	 * @since 3.0.1
	 * @param size the size (must be between 1 and 100)
	 */
	public void setSize(int size) {
		if(size < 1 || size > 100) {
			throw new IllegalArgumentException("Invalid size");
		}
		this.size = size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; size=%s", super.toString(), size);
	}
	
	static {
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 1, "tiny");
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 2, "normal");
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 4, "big");
//		for(int i = 1; i <= 100; i++) {
//			Subtypes.registerSubtype(SizedDisguise.class, "setSize", i, Integer.toString(i));
//		}
		Subtypes.registerParameterizedSubtype(SizedDisguise.class, "setSize", "size", int.class);
	}
	
}