package de.robingrether.idisguise.disguise;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a sized mob (slime, magma cube, phatom).
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class SizedDisguise extends MobDisguise {
	
	private int size;
	private final int minSize, maxSize = 100;
	
	/**
	 * Creates an instance.<br>
	 * Default size is 2 for slimes and magma cubes, 0 for phantoms.
	 * 
	 * @since 4.0.1
	 * @param type the disguise type (may be {@linkplain DisguiseType#SLIME}, {@linkplain DisguiseType#MAGMA_CUBE} or {@linkplain DisguiseType#PHANTOM}.
	 * @throws IllegalArgumentException The given disguise type is invalid.
	 */
	public SizedDisguise(DisguiseType type) {
		this(type, type.equals(DisguiseType.PHANTOM) ? 0 : 2);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param type the disguise type (may be {@linkplain DisguiseType#SLIME}, {@linkplain DisguiseType#MAGMA_CUBE} or {@linkplain DisguiseType#PHANTOM}.
	 * @param size Size of the resulting disguise. See {@linkplain SizedDisguise#setSize(int)} for valid values.
	 * @throws IllegalArgumentException The given disguise type or size is invalid.
	 */
	public SizedDisguise(DisguiseType type, int size) {
		super(type);
		if(!ObjectUtil.equals(type, DisguiseType.SLIME, DisguiseType.MAGMA_CUBE, DisguiseType.PHANTOM)) {
			throw new IllegalArgumentException();
		}
		this.minSize = type.equals(DisguiseType.PHANTOM) ? 0 : 1;
		setSize(size);
	}
	
	/**
	 * Gets the size.
	 * 
	 * @since 3.0.1
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Sets the size.<br>
	 * Size must be &gt;0 and &le;100 for {@linkplain DisguiseType#SLIME} and {@linkplain DisguiseType#MAGMA_CUBE}<br>
	 * respectively &ge;0 and &le;100 for {@linkplain DisguiseType#PHANTOM}.
	 * 
	 * @since 3.0.1
	 * @throws IllegalArgumentException Invalid size.
	 */
	public void setSize(int size) {
		if(size < minSize || size > maxSize) {
			throw new IllegalArgumentException();
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