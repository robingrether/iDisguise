package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Represents a disguise as an enderman.
 * 
 * @since 4.0.1
 * @author Robingrether
 */
public class EndermanDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -4717245165572013853L;
	private Material blockInHand;
	private int blockInHandData;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public EndermanDisguise() {
		this(Material.AIR);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param blockInHand the carried block
	 * @throws IllegalArgumentException Material is not a block.
	 */
	public EndermanDisguise(Material blockInHand) {
		this(blockInHand, 0);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param blockInHand the carried block
	 * @param blockInHandData the carried block's data value
	 * @throws IllegalArgumentException Material is not a block or data value is negative.
	 */
	public EndermanDisguise(Material blockInHand, int blockInHandData) {
		super(DisguiseType.ENDERMAN, true);
		if(!blockInHand.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		this.blockInHand = blockInHand;
		if(blockInHandData < 0) {
			throw new IllegalArgumentException("Data must be positive!");
		}
		this.blockInHandData = blockInHandData;
	}
	
	/**
	 * Returns the carried block.
	 * 
	 * @since 4.0.1
	 * @return the carried block
	 */
	public Material getBlockInHand() {
		return blockInHand;
	}
	
	/**
	 * Sets the carried block.
	 * 
	 * @since 4.0.1
	 * @param blockInHand the carried block
	 * @throws IllegalArgumentException Material is not a block.
	 */
	public void setBlockInHand(Material blockInHand) {
		if(!blockInHand.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		this.blockInHand = blockInHand;
	}
	
	/**
	 * Returns the carried block's data value.
	 * 
	 * @since 4.0.1
	 * @return the carried block's data value
	 */
	public int getBlockInHandData() {
		return blockInHandData;
	}
	
	/**
	 * Sets the carried block's data value.
	 * 
	 * @since 4.0.1
	 * @param blockInHandData the carried block's data value
	 * @throws IllegalArgumentException Data value is negative.
	 */
	public void setBlockInHandData(int blockInHandData) {
		if(blockInHandData < 0) {
			throw new IllegalArgumentException("Data must be positive!");
		}
		this.blockInHandData = blockInHandData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EndermanDisguise clone() {
		EndermanDisguise clone = new EndermanDisguise(blockInHand, blockInHandData);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof EndermanDisguise && ((EndermanDisguise)object).blockInHand.equals(blockInHand) && ((EndermanDisguise)object).blockInHandData == blockInHandData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityEnderman getEntity(World world, Location location, int id) {
		EntityEnderman entity = (EntityEnderman)super.getEntity(world, location, id);
		entity.setCarried(Block.getById(blockInHand.getId()).fromLegacyData(blockInHandData));
		return entity;
	}
	
}