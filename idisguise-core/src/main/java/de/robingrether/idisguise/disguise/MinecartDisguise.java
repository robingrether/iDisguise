package de.robingrether.idisguise.disguise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import de.robingrether.idisguise.iDisguise;
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
	
	public void setDisplayedBlock(Object paramDisplayedBlock) {
		Material localDisplayedBlock;
		Object localBlockData;
		if(paramDisplayedBlock == null) {
			paramDisplayedBlock = Material.AIR;
		}
		
		if(paramDisplayedBlock instanceof Material) {
			localDisplayedBlock = (Material)paramDisplayedBlock;
			if(VersionHelper.require1_13()) {
				localBlockData = localDisplayedBlock.createBlockData();
			} else {
				localBlockData = 0;
			}
		} else if(VersionHelper.require1_13() && paramDisplayedBlock instanceof BlockData) {
			localDisplayedBlock = ((BlockData)paramDisplayedBlock).getMaterial();
			localBlockData = paramDisplayedBlock;
		} else {
			throw new IllegalArgumentException("Displayed block must be a Material, BlockData or null.");
		}
		
		if(!localDisplayedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		if(localDisplayedBlock.name().startsWith("LEGACY")) {
			throw new IllegalArgumentException("Legacy materials are invalid!");
		}
		if(INVALID_MATERIALS.contains(localDisplayedBlock)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		
		this.displayedBlock = localDisplayedBlock;
		this.blockData = localBlockData;
	}
	
	public void setDisplayedBlock(String displayedBlock) {
		Material material = Material.matchMaterial(displayedBlock.replace('-', '_'));
		if(material != null) {
			setDisplayedBlock((Object)material);
			return;
		} else if(VersionHelper.require1_13()) {
			BlockData displayedBlock2 = null;
			try {
				displayedBlock2 = Bukkit.createBlockData(displayedBlock);
			} catch(IllegalArgumentException e) { // fail silently
			}
			if(displayedBlock2 != null) {
				setDisplayedBlock(displayedBlock2);
				return;
			}
		} else if(displayedBlock.contains(":")) {
			String[] s = displayedBlock.split(":", 2);
			material = Material.matchMaterial(s[0].replace('-', '_'));
			if(material != null) {
				try {
					Short data = Short.parseShort(s[1]);
					setDisplayedBlock((Object)material);
					setDisplayedBlockData(data);
					return;
				} catch(NumberFormatException e) {
					throw new IllegalArgumentException("Invalid data value!");
				}
			}
		}
		throw new IllegalArgumentException("Unknown argument!");
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
			return String.format("%s; displayed-block=%s", super.toString(), ((BlockData)blockData).getAsString());
		} else {
			return String.format("%s; displayed-block=%s:%s", super.toString(), displayedBlock.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), blockData);
		}
	}
	
	/**
	 * A set containing all invalid materials.<br>
	 * These materials are <em>invalid</em> because the associated disguise would be invisible.
	 * 
	 * @since 5.7.1
	 */
	public static final Set<Material> INVALID_MATERIALS;
	
	static {
		Set<Material> tempSet = new HashSet<Material>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(MinecartDisguise.class.getResourceAsStream("minecart.txt")));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("*")) {
					if(VersionHelper.require1_13()) continue;
					line = line.substring(1);
				}
				Material material = Material.getMaterial(line);
				if(material != null) tempSet.add(material);
			}
			reader.close();
		} catch(IOException e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "An unexpected exception occured.", e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) { // fail silently
				}
			}
		}
		INVALID_MATERIALS = Collections.unmodifiableSet(tempSet);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Material material : Material.values()) {
			if(material.isBlock() && !INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		
		Subtypes.registerParameterizedSubtype(MinecartDisguise.class, (disguise, parameter) -> disguise.setDisplayedBlock(parameter), "displayed-block", parameterSuggestions);
	}
	
}