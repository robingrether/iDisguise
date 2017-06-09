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
	
	private static final long serialVersionUID = -7935017310299797038L;
	private Material material;
	private int data;
	private boolean onlyBlockCoordinates;
	
	/**
	 * Creates an instance.<br>
	 * The default material is {@link Material#STONE}
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
		this.onlyBlockCoordinates = false;
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.4.1
	 * @param material the material
	 * @param data the block data
	 * @param onlyBlockCoordinates makes the disguise appear on block coordinates only, so it looks like an actual block that you can't target
	 * @throws IllegalArgumentException if the material is not a block, or if the data is negative
	 */
	public FallingBlockDisguise(Material material, int data, boolean onlyBlockCoordinates) {
		super(DisguiseType.FALLING_BLOCK);
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.material = material;
		this.data = data;
		this.onlyBlockCoordinates = onlyBlockCoordinates;
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
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.data = data;
	}
	
	/**
	 * Indicates whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @return <code>true</code>, if this disguise may appear only on block coordinates
	 */
	public boolean onlyBlockCoordinates() {
		return onlyBlockCoordinates;
	}
	
	/**
	 * Sets whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @param onlyBlockCoordinates makes this disguise appear on block coordinates only
	 */
	public void setOnlyBlockCoordinates(boolean onlyBlockCoordinates) {
		this.onlyBlockCoordinates = onlyBlockCoordinates;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; material=" + material.name().toLowerCase(Locale.ENGLISH).replace('_', '-') + "; material-data=" + data + "; " + (onlyBlockCoordinates ? "block-coordinates" : "all-coordinates");
	}
	
	static {
//		for(Material material : Material.values()) {
//			if(material.isBlock()) {
//				Subtypes.registerSubtype(FallingBlockDisguise.class, "setMaterial", material, material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
//			}
//		}
		Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setMaterial", "material", Material.class);
//		for(int i = 0; i < 256; i++) {
//			Subtypes.registerSubtype(FallingBlockDisguise.class, "setData", i, Integer.toString(i));
//		}
		Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setData", "material-data", int.class);
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", true, "block-coordinates");
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", false, "all-coordinates");
	}
	
}