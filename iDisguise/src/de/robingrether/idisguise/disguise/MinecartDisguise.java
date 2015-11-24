package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.Material;

/**
 * Represents a disguise as a minecart.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class MinecartDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = -2064613105255090886L;
	private Material displayedBlock;
	private int displayedBlockData;
	
	/**
	 * Creates an instance.<br>
	 * The default block inside the cart is {@link Material.AIR}
	 * 
	 * @since 5.1.1
	 */
	public MinecartDisguise() {
		this(Material.AIR);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param displayedBlock the block to display inside the cart
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public MinecartDisguise(Material displayedBlock) {
		this(displayedBlock, 0);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param displayedBlock the block to display inside the cart
	 * @param displayedBlockData the data of the block inside
	 * @throws IllegalArgumentException if the material is not a block, or if data is negative integer
	 */
	public MinecartDisguise(Material displayedBlock, int displayedBlockData) {
		super(DisguiseType.MINECART);
		if(displayedBlock == null) {
			displayedBlock = Material.AIR;
		}
		if(!displayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.displayedBlock = displayedBlock;
		if(displayedBlockData < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.displayedBlockData = displayedBlockData;
	}
	
	/**
	 * Gets the block displayed inside the cart.
	 * 
	 * @since 5.1.1
	 * @return the block displayed inside the cart
	 */
	public Material getDisplayedBlock() {
		return displayedBlock;
	}
	
	/**
	 * Sets the block to display inside the cart.
	 * 
	 * @since 5.1.1
	 * @param displayedBlock the block to display inside the cart
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public void setDisplayedBlock(Material displayedBlock) {
		if(!displayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.displayedBlock = displayedBlock;
	}
	
	/**
	 * Gets the data of the block inside the cart.
	 * 
	 * @since 5.1.1
	 * @return the data value
	 */
	public int getDisplayedBlockData() {
		return displayedBlockData;
	}
	
	/**
	 * Sets the data of the block inside the cart.
	 * 
	 * @since 5.1.1
	 * @param displayedBlockData the data value
	 * @throws IllegalArgumentException if the given data value is negative
	 */
	public void setDisplayedBlockData(int displayedBlockData) {
		if(displayedBlockData < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.displayedBlockData = displayedBlockData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MinecartDisguise clone() {
		return new MinecartDisguise(displayedBlock, displayedBlockData);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof MinecartDisguise && ((MinecartDisguise)object).displayedBlock.equals(displayedBlock) && ((MinecartDisguise)object).displayedBlockData == displayedBlockData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		try {
			Material displayedBlock = Material.valueOf(argument.replace('-', '_').toUpperCase(Locale.ENGLISH));
			if(displayedBlock.isBlock()) {
				setDisplayedBlock(displayedBlock);
				return true;
			}
		} catch(IllegalArgumentException e) {
		}
		try {
			short displayedBlockData = Short.parseShort(argument);
			if(displayedBlockData >= 0 && displayedBlockData < 256) {
				setDisplayedBlockData(displayedBlockData);
				return true;
			}
		} catch(NumberFormatException e) {
		}
		return false;
	}
	
}