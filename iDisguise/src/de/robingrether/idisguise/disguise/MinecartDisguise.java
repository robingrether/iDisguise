package de.robingrether.idisguise.disguise;

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
	
	public MinecartDisguise() {
		this(Material.AIR);
	}
	
	public MinecartDisguise(Material displayedBlock) {
		this(displayedBlock, 0);
	}
	
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
	
	public Material getDisplayedBlock() {
		return displayedBlock;
	}
	
	public void setDisplayedBlock(Material displayedBlock) {
		if(!displayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		this.displayedBlock = displayedBlock;
	}
	
	public int getDisplayedBlockData() {
		return displayedBlockData;
	}
	
	public void setDisplayedBlockData(int displayedBlockData) {
		if(displayedBlockData < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.displayedBlockData = displayedBlockData;
	}
	
	public MinecartDisguise clone() {
		return new MinecartDisguise(displayedBlock, displayedBlockData);
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof MinecartDisguise && ((MinecartDisguise)object).displayedBlock.equals(displayedBlock) && ((MinecartDisguise)object).displayedBlockData == displayedBlockData;
	}
	
}