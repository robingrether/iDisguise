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
		super(DisguiseType.FALLING_BLOCK);
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.material = material;
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
	 * Sets the material.
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
	}
	
	/**
	 * {@inheritDoc}
	 */
	public FallingBlockDisguise clone() {
		return new FallingBlockDisguise(material);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof FallingBlockDisguise && ((FallingBlockDisguise)object).material.equals(material);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		try {
			Material material = Material.valueOf(argument.replace('-', '_').toUpperCase(Locale.ENGLISH));
			if(material.isBlock()) {
				setMaterial(material);
				return true;
			}
		} catch(IllegalArgumentException e) {
		}
		return false;
	}
	
}