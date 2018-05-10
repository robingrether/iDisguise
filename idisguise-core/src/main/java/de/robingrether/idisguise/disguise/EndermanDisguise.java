package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as an enderman.
 * 
 * @since 4.0.1
 * @author RobinGrether
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
		super(DisguiseType.ENDERMAN);
		if(blockInHand == null) {
			blockInHand = Material.AIR;
		}
		if(!blockInHand.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		if(INVALID_MATERIALS.contains(blockInHand)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
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
	 * Sets the carried block.<br>
	 * This also resets the block data to 0.
	 * 
	 * @since 4.0.1
	 * @param blockInHand the carried block
	 * @throws IllegalArgumentException Material is not a block.
	 */
	public void setBlockInHand(Material blockInHand) {
		if(blockInHand == null) {
			blockInHand = Material.AIR;
		}
		if(!blockInHand.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		if(INVALID_MATERIALS.contains(blockInHand)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		this.blockInHand = blockInHand;
		this.blockInHandData = 0;
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
	public String toString() {
		return String.format("%s; block=%s; block-data=%s", super.toString(), blockInHand.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), blockInHandData);
	}
	
	/**
	 * A set containing all invalid materials.<br>
	 * These materials are <em>invalid</em> because the associated disguise would be invisible.
	 * 
	 * @since 5.7.1
	 */
	public static final Set<Material> INVALID_MATERIALS;
	
	static {
		Set<Material> tempSet = new HashSet<Material>(Arrays.asList(Material.BARRIER, Material.BED_BLOCK, Material.COBBLE_WALL, Material.ENDER_PORTAL, Material.LAVA, Material.MELON_STEM,
				Material.PISTON_MOVING_PIECE, Material.PUMPKIN_STEM, Material.SIGN_POST, Material.SKULL, Material.STANDING_BANNER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER,
				Material.WALL_BANNER, Material.WALL_SIGN, Material.WATER));
		if(VersionHelper.require1_9()) {
			tempSet.add(Material.END_GATEWAY);
		}
		if(VersionHelper.require1_10()) {
			tempSet.add(Material.STRUCTURE_VOID);
		}
		INVALID_MATERIALS = Collections.unmodifiableSet(tempSet);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Material material : Material.values()) {
			if(material.isBlock() && !INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		Subtypes.registerParameterizedSubtype(EndermanDisguise.class, "setBlockInHand", "block", Material.class, parameterSuggestions);
//		for(int i = 0; i < 256; i++) {
//			Subtypes.registerSubtype(EndermanDisguise.class, "setBlockInHandData", i, Integer.toString(i));
//		}
		Subtypes.registerParameterizedSubtype(EndermanDisguise.class, "setBlockInHandData", "block-data", int.class);
	}
	
}