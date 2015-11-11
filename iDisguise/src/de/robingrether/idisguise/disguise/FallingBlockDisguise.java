package de.robingrether.idisguise.disguise;

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
	
	public FallingBlockDisguise() {
		this(Material.STONE);
	}
	
	public FallingBlockDisguise(Material material) {
		super(DisguiseType.FALLING_BLOCK);
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		if(!material.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.material = material;
	}
	
	public FallingBlockDisguise clone() {
		return new FallingBlockDisguise(material);
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof FallingBlockDisguise && ((FallingBlockDisguise)object).material.equals(material);
	}
	
}