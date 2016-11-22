package de.robingrether.idisguise.disguise;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a sized mob (e.g. slime, magma slime).
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class SizedDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 6370692880641733105L;
	private int size;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the disguise type (should be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
	 */
	public SizedDisguise(DisguiseType type) {
		this(type, 2);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param type the disguise type (should be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
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
	 * @param size the size (must not be negative)
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SizedDisguise clone() {
		SizedDisguise clone = new SizedDisguise(type, size);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof SizedDisguise && ((SizedDisguise)object).size == size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + size + (size == 1 ? " (tiny)" : size == 2 ? " (normal)" : size == 4 ? " (big)" : "");
	}
	
	static {
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 1, "tiny");
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 2, "normal");
		Subtypes.registerSubtype(SizedDisguise.class, "setSize", 4, "big");
		for(int i = 1; i <= 100; i++) {
			Subtypes.registerSubtype(SizedDisguise.class, "setSize", i, Integer.toString(i));
		}
	}
	
}