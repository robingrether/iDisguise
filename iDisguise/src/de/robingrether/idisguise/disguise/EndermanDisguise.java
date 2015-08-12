package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.Material;

public class EndermanDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -4717245165572013853L;
	private Material blockInHand;
	private int blockInHandData;
	
	public EndermanDisguise() {
		this(Material.AIR);
	}
	
	public EndermanDisguise(Material blockInHand) {
		this(blockInHand, 0);
	}
	
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
	
	public Material getBlockInHand() {
		return blockInHand;
	}
	
	public void setBlockInHand(Material blockInHand) {
		if(!blockInHand.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		this.blockInHand = blockInHand;
	}
	
	public int getBlockInHandData() {
		return blockInHandData;
	}
	
	public void setBlockInHandData(int blockInHandData) {
		if(blockInHandData < 0) {
			throw new IllegalArgumentException("Data must be positive!");
		}
		this.blockInHandData = blockInHandData;
	}
	
	public EndermanDisguise clone() {
		EndermanDisguise clone = new EndermanDisguise(blockInHand, blockInHandData);
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof EndermanDisguise && ((EndermanDisguise)object).blockInHand.equals(blockInHand) && ((EndermanDisguise)object).blockInHandData == blockInHandData;
	}
	
	@SuppressWarnings("deprecation")
	public EntityEnderman getEntity(World world, Location location, int id) {
		EntityEnderman entity = (EntityEnderman)super.getEntity(world, location, id);
		entity.setCarried(Block.getById(blockInHand.getId()).fromLegacyData(blockInHandData));
		return entity;
	}
	
}