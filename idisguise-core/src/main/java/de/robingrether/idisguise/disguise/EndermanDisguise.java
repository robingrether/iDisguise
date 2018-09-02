package de.robingrether.idisguise.disguise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as an enderman.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class EndermanDisguise extends MobDisguise {
	
	private Material carriedBlock;
	
	/**
	 * This will be a {@linkplain BlockData} for 1.13+
	 * and an non-negative integer value for 1.12 and earlier.
	 */
	private Object blockData;
	
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
	 * @throws IllegalArgumentException Material is not a block.
	 */
	public EndermanDisguise(Material carriedBlock) {
		this((Object)carriedBlock);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @throws IllegalArgumentException Material is not a block or data value is negative.
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public EndermanDisguise(Material carriedBlock, int blockData) {
		this(carriedBlock);
		setBlockInHandData(blockData);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.8.1
	 * @param carriedBlock Must be a valid {@linkplain Material}, {@linkplain BlockData} or <code>null</code>.
	 * @throws IllegalArgumentException Carried block is not valid.
	 */
	public EndermanDisguise(Object carriedBlock) {
		super(DisguiseType.ENDERMAN);
		setCarriedBlock(carriedBlock);
	}
	
	public Material getCarriedBlock() {
		return carriedBlock;
	}
	
	public Object getBlockData() {
		return blockData;
	}
	
	/**
	 * Sets the carried block.
	 * 
	 * @since 5.8.1
	 * @param paramCarriedBlock Must be a valid {@linkplain Material}, {@linkplain BlockData} or <code>null</code>.
	 * @throws IllegalArgumentException Carried block is not valid.
	 */
	public void setCarriedBlock(Object paramCarriedBlock) {
		Material localCarriedBlock;
		Object localBlockData;
		if(paramCarriedBlock == null) {
			paramCarriedBlock = Material.AIR;
		}
		
		if(paramCarriedBlock instanceof Material) {
			localCarriedBlock = (Material)paramCarriedBlock;
			if(VersionHelper.require1_13()) {
				localBlockData = localCarriedBlock.createBlockData();
			} else {
				localBlockData = 0;
			}
		} else if(VersionHelper.require1_13() && paramCarriedBlock instanceof BlockData) {
			localCarriedBlock = ((BlockData)paramCarriedBlock).getMaterial();
			localBlockData = paramCarriedBlock;
		} else {
			throw new IllegalArgumentException("Carried block must be a Material, BlockData or null.");
		}
		
		if(!localCarriedBlock.isBlock()) {
			throw new IllegalArgumentException("Material must be a block!");
		}
		if(localCarriedBlock.name().startsWith("LEGACY")) {
			throw new IllegalArgumentException("Legacy materials are invalid!");
		}
		if(INVALID_MATERIALS.contains(localCarriedBlock)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		
		this.carriedBlock = localCarriedBlock;
		this.blockData = localBlockData;
	}
	
	/**
	 * Sets the carried block.<br>
	 * Attempts to parse a {@linkplain Material} or a {@linkplain BlockData} from the given string.
	 * 
	 * @since 5.8.1
	 * @throws IllegalArgumentException The parsed value is not valid or no value could be parsed.
	 */
	public void setCarriedBlock(String carriedBlock) {
		Material material = Material.matchMaterial(carriedBlock.replace('-', '_'));
		if(material != null) {
			setCarriedBlock(material);
			return;
		} else if(VersionHelper.require1_13()) {
			BlockData carriedBlock2 = null;
			try {
				carriedBlock2 = Bukkit.createBlockData(carriedBlock);
			} catch(IllegalArgumentException e) { // fail silently
			}
			if(carriedBlock2 != null) {
				setCarriedBlock(carriedBlock2);
				return;
			}
		} else if(carriedBlock.contains(":")) {
			String[] s = carriedBlock.split(":", 2);
			material = Material.matchMaterial(s[0].replace('-', '_'));
			if(material != null) {
				try {
					Short data = Short.parseShort(s[1]);
					setCarriedBlock(material);
					setBlockInHandData(data);
					return;
				} catch(NumberFormatException e) {
					throw new IllegalArgumentException("Invalid data value!");
				}
			}
		}
		throw new IllegalArgumentException("Unknown argument!");
	}
	
	/**
	 * @since 4.0.1
	 * 
	 * @deprecated Renamed to {@linkplain EndermanDisguise#getCarriedBlock()}
	 */
	@Deprecated
	public Material getBlockInHand() {
		return carriedBlock;
	}
	
	/**
	 * @since 4.0.1
	 * 
	 * @deprecated Replaced by {@linkplain EndermanDisguise#setCarriedBlock(Object)}
	 */
	@Deprecated
	public void setBlockInHand(Material blockInHand) {
		setCarriedBlock(blockInHand);
	}
	
	/**
	 * @since 4.0.1
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public int getBlockInHandData() {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		return (Integer)blockData;
	}
	
	/**
	 * @since 4.0.1
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public void setBlockInHandData(int blockInHandData) {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		if(blockInHandData < 0) {
			throw new IllegalArgumentException("Data must not be negative!");
		}
		this.blockData = blockInHandData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if(VersionHelper.require1_13()) {
			return String.format("%s; carried-block=%s", super.toString(), ((BlockData)blockData).getAsString());
		} else {
			return String.format("%s; carried-block=%s:%s", super.toString(), carriedBlock.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), blockData);
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
			reader = new BufferedReader(new InputStreamReader(EndermanDisguise.class.getResourceAsStream("enderman.txt")));
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
		} catch(IOException e) { // fail silently
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
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
		
		Subtypes.registerParameterizedSubtype(EndermanDisguise.class, (disguise, parameter) -> disguise.setCarriedBlock(parameter), "carried-block", parameterSuggestions);
	}
	
}