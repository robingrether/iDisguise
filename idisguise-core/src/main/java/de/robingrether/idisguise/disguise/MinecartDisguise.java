package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;

import de.robingrether.idisguise.management.VersionHelper;

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
	 * The default block inside the cart is {@link Material#AIR}
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
		if(INVALID_MATERIALS.contains(displayedBlock)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
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
	 * Sets the block to display inside the cart.<br>
	 * This also resets the block data to 0.
	 * 
	 * @since 5.1.1
	 * @param displayedBlock the block to display inside the cart
	 * @throws IllegalArgumentException if the material is not a block
	 */
	public void setDisplayedBlock(Material displayedBlock) {
		if(displayedBlock == null) {
			displayedBlock = Material.AIR;
		}
		if(!displayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(INVALID_MATERIALS.contains(displayedBlock)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		this.displayedBlock = displayedBlock;
		this.displayedBlockData = 0;
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
	public String toString() {
		return String.format("%s; block=%s; block-data=%s", super.toString(), displayedBlock.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), displayedBlockData);
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
		Subtypes.registerParameterizedSubtype(MinecartDisguise.class, "setDisplayedBlock", "block", Material.class, parameterSuggestions);
//		for(int i = 0; i < 256; i++) {
//			Subtypes.registerSubtype(MinecartDisguise.class, "setDisplayedBlockData", i, Integer.toString(i));
//		}
		Subtypes.registerParameterizedSubtype(MinecartDisguise.class, "setDisplayedBlockData", "block-data", int.class);
	}
	
}