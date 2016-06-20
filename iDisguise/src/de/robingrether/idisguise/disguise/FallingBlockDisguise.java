package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.Material;

/**
 * Represents a disguise as a falling block.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class FallingBlockDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = -5052831499921781174L;
	private Material material;
	private int data;
	
	/**
	 * Creates an instance.<br>
	 * The default material is {@link Material.STONE}
	 * 
	 * @since 5.1.1
	 */
	public FallingBlockDisguise() {
		this(Material.STONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param material the material
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public FallingBlockDisguise(Material material) {
		this(material, 0);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @param material the material
	 * @param data the block data
	 * @throws IllegalArgumentException if the material is not a block, or if the data is negative
	 */
	public FallingBlockDisguise(Material material, int data) {
		super(DisguiseType.FALLING_BLOCK);
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.material = material;
		this.data = data;
	}
	
	/**
	 * Gets the material.
	 * 
	 * @since 5.1.1
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Sets the material.<br>
	 * This also resets the data to 0.
	 * 
	 * @since 5.1.1
	 * @param material the material
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public void setMaterial(Material material) {
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.material = material;
		this.data = 0;
	}
	
	/**
	 * Gets the block data.
	 * 
	 * @since 5.2.2
	 * @return the block data
	 */
	public int getData() {
		return data;
	}
	
	/**
	 * Sets the block data.
	 * 
	 * @since 5.2.2
	 * @param data the block data
	 */
	public void setData(int data) {
		this.data = data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public FallingBlockDisguise clone() {
		return new FallingBlockDisguise(material, data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof FallingBlockDisguise && ((FallingBlockDisguise)object).material.equals(material) && ((FallingBlockDisguise)object).data == data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + material.name().toLowerCase(Locale.ENGLISH).replace('_', '-') + "; " + data;
	}
	
	static {
		for(Material material : Material.values()) {
			if(material.isBlock()) {
				Subtypes.registerSubtype(FallingBlockDisguise.class, "setMaterial", material, material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		for(int i = 0; i < 256; i++) {
			Subtypes.registerSubtype(FallingBlockDisguise.class, "setData", i, Integer.toString(i));
		}
	}
	
}