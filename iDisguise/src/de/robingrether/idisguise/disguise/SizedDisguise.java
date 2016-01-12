package de.robingrether.idisguise.disguise;

import java.util.Locale;

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
	public boolean applySubtype(String argument) {
		switch(argument.toLowerCase(Locale.ENGLISH)) {
			case "tiny":
			case "small":
				setSize(1);
				return true;
			case "normal":
			case "medium":
				setSize(2);
				return true;
			case "big":
			case "tall":
			case "massive":
				setSize(4);
				return true;
		}
		try {
			int size = Integer.parseInt(argument);
			if(size <= 100 && size > 0) {
				setSize(size);
				return true;
			}
		} catch(NumberFormatException e) {
		}
		return false;
	}
	
}