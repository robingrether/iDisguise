package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as a minecart.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class MinecartDisguise extends ObjectDisguise {
	
	private Material displayedBlock;
	
	/**
	 * This will be a {@linkplain BlockData} for 1.13+
	 * and an non-negative integer value for 1.12 and earlier.
	 */
	private Object blockData;
	
	/**
	 * Creates an instance.<br>
	 * The default block inside the cart is {@linkplain Material#AIR}.
	 * 
	 * @since 5.1.1
	 */
	public MinecartDisguise() {
		this(Material.AIR);
	}
	
	/**
	 * @since 5.1.1
	 * @throws IllegalArgumentException Material is not valid.
	 */
	public MinecartDisguise(Material displayedBlock) {
		this((Object)displayedBlock);
	}
	
	/**
	 * @since 5.1.1
	 * @throws IllegalArgumentException Material or data is not valid.
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public MinecartDisguise(Material displayedBlock, int displayedBlockData) {
		this(displayedBlock);
		setDisplayedBlockData(displayedBlockData);
	}
	
	public MinecartDisguise(Object displayedBlock) {
		super(DisguiseType.MINECART);
		setDisplayedBlock(displayedBlock);
	}
	
	/**
	 * @since 5.1.1
	 */
	public Material getDisplayedBlock() {
		return displayedBlock;
	}
	
	public Object getBlockData() {
		return blockData;
	}
	
	public void setDisplayedBlock(Object displayedBlock) {
		if(displayedBlock == null) {
			displayedBlock = Material.AIR;
		}
		if(displayedBlock instanceof Material) {
			this.displayedBlock = (Material)displayedBlock;
			if(VersionHelper.require1_13()) {
				this.blockData = this.displayedBlock.createBlockData();
			} else {
				this.blockData = 0;
			}
		} else if(VersionHelper.require1_13() && displayedBlock instanceof BlockData) {
			this.displayedBlock = ((BlockData)displayedBlock).getMaterial();
			this.blockData = displayedBlock;
		} else {
			throw new IllegalArgumentException("Displayed block must be a Material, BlockData or null.");
		}
		
		if(!this.displayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		if(INVALID_MATERIALS.contains(this.displayedBlock)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
	}
	
	public boolean setDisplayedBlock(String displayedBlock) {
		Material material = Material.matchMaterial(displayedBlock);
		if(material != null) {
			setDisplayedBlock((Object)material);
			return true;
		} else if(VersionHelper.require1_13()) {
			Object displayedBlock2 = null;
			try {
				displayedBlock2 = Bukkit.createBlockData(displayedBlock);
			} catch(IllegalArgumentException e) {
			}
			if(displayedBlock2 != null) {
				setDisplayedBlock(displayedBlock2);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @since 5.1.1
	 * 
	 * @deprecated Replaced by {@linkplain MinecartDisguise#setDisplayedBlock(Object)}
	 */
	@Deprecated
	public void setDisplayedBlock(Material displayedBlock) {
		setDisplayedBlock((Object)displayedBlock);
	}
	
	/**
	 * @since 5.1.1
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public int getDisplayedBlockData() {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		return (Integer)blockData;
	}
	
	/**
	 * @since 5.1.1
	 * @throws IllegalArgumentException Data is not valid.
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public void setDisplayedBlockData(int displayedBlockData) {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		if(displayedBlockData < 0) {
			throw new IllegalArgumentException("Data must not be negative!");
		}
		this.blockData = displayedBlockData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if(VersionHelper.require1_13()) {
			return String.format("%s; displayed-block=%s", super.toString(), blockData);
		} else {
			return String.format("%s; block=%s; block-data=%s", super.toString(), displayedBlock.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), blockData);
		}
	}
	
	/**
	 * A set containing all invalid materials.<br>
	 * These materials are <em>invalid</em> because the associated disguise would be invisible.
	 * 
	 * @since 5.7.1
	 */
	public static final Set<Material> INVALID_MATERIALS;
	// TODO
	
	static {
		Set<Material> tempSet = new HashSet<Material>(/*Arrays.asList(Material.BARRIER, Material.BED_BLOCK, Material.COBBLE_WALL, Material.ENDER_PORTAL, Material.LAVA, Material.MELON_STEM,
				Material.PISTON_MOVING_PIECE, Material.PUMPKIN_STEM, Material.SIGN_POST, Material.SKULL, Material.STANDING_BANNER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER,
				Material.WALL_BANNER, Material.WALL_SIGN, Material.WATER));
		if(VersionHelper.require1_9()) {
			tempSet.add(Material.END_GATEWAY);
		}
		if(VersionHelper.require1_10()) {
			tempSet.add(Material.STRUCTURE_VOID*/);
		//}
		INVALID_MATERIALS = Collections.unmodifiableSet(tempSet);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Material material : Material.values()) {
			if(material.isBlock() && !INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		
		if(VersionHelper.require1_13()) {
			Subtypes.registerParameterizedSubtype(MinecartDisguise.class, "setDisplayedBlock", "displayed-block", String.class, parameterSuggestions);
		} else {
			Subtypes.registerParameterizedSubtype(MinecartDisguise.class, "setDisplayedBlock", "block", Material.class, parameterSuggestions);
			Subtypes.registerParameterizedSubtype(MinecartDisguise.class, "setDisplayedBlockData", "block-data", int.class);
		}
	}
	
}