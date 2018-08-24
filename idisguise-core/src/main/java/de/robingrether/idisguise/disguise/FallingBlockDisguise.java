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
 * Represents a disguise as a falling block.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class FallingBlockDisguise extends ObjectDisguise {
	
	private Material material;
	
	/**
	 * This will be a {@linkplain BlockData} for 1.13+
	 * and an non-negative integer value for 1.12 and earlier.
	 */
	private Object materialData;
	
	private boolean onlyBlockCoordinates;
	
	/**
	 * Creates an instance.<br>
	 * The default material is {@linkplain Material#STONE}.
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
	 * @throws IllegalArgumentException Material is not valid.
	 */
	public FallingBlockDisguise(Material material) {
		this(material, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @throws IllegalArgumentException Material or data is not valid.
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public FallingBlockDisguise(Material material, int data) {
		this(material, data, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.4.1
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public FallingBlockDisguise(Material material, int data, boolean onlyBlockCoordinates) {
		this(material, onlyBlockCoordinates);
		setData(data);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.8.1
	 * @param materialData Must be a valid {@linkplain Material} or {@linkplain BlockData}.
	 * @param onlyBlockCoordinates makes the disguise appear on block coordinates only, so it looks like an actual block that you can't target
	 * @throws IllegalArgumentException Material data is not valid.
	 */
	public FallingBlockDisguise(Object materialData, boolean onlyBlockCoordinates) {
		super(DisguiseType.FALLING_BLOCK);
		setMaterialData(materialData);
		this.onlyBlockCoordinates = onlyBlockCoordinates;
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
	
	public Object getMaterialData() {
		return materialData;
	}
	
	public void setMaterialData(Object paramMaterialData) {
		Material localMaterial;
		Object localMaterialData;
		
		if(paramMaterialData instanceof Material) {
			localMaterial = (Material)paramMaterialData;
			if(VersionHelper.require1_13()) {
				localMaterialData = localMaterial.createBlockData();
			} else {
				localMaterialData = 0;
			}
		} else if(VersionHelper.require1_13() && paramMaterialData instanceof BlockData) {
			localMaterial = ((BlockData)paramMaterialData).getMaterial();
			localMaterialData = paramMaterialData;
		} else {
			throw new IllegalArgumentException("Data must be a Material or BlockData.");
		}
		
		if(!localMaterial.isBlock()) {
			throw new IllegalArgumentException("Material must be a block");
		}
		if(localMaterial.name().startsWith("LEGACY")) {
			throw new IllegalArgumentException("Legacy materials are invalid!");
		}
		if(INVALID_MATERIALS.contains(localMaterial)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		
		this.material = localMaterial;
		this.materialData = localMaterialData;
	}
	
	public boolean setMaterialData(String materialData) {
		try {
			Material material = Material.matchMaterial(materialData.replace('-', '_'));
			if(material != null) {
				setMaterialData(material);
				return true;
			} else if(VersionHelper.require1_13()) {
				Object materialData2 = null;
				materialData2 = Bukkit.createBlockData(materialData);
				if(materialData2 != null) {
					setMaterialData(materialData2);
					return true;
				}
			}
		} catch(IllegalArgumentException e) {
		}
		return false;
	}
	
	/**
	 * Sets the material.<br>
	 * This also resets the data to 0.
	 * 
	 * @since 5.1.1
	 * @throws IllegalArgumentException Material is not valid.
	 * 
	 * @deprecated Replaced by {@linkplain FallingBlockDisguise#setMaterialData(Object)}
	 */
	@Deprecated
	public void setMaterial(Material material) {
		setMaterialData(material);
	}
	
	/**
	 * @since 5.2.2
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public int getData() {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		return (Integer)materialData;
	}
	
	/**
	 * @since 5.2.2
	 * 
	 * @deprecated Numerical block data values should not be used anymore.
	 */
	@Deprecated
	public void setData(int data) {
		if(VersionHelper.require1_13()) {
			throw new UnsupportedOperationException("Numerical block data values are not supported anymore.");
		}
		if(data < 0) {
			throw new IllegalArgumentException("Data must be positive");
		}
		this.materialData = data;
	}
	
	/**
	 * Indicates whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @return <code>true</code>, if this disguise may appear only on block coordinates
	 */
	public boolean onlyBlockCoordinates() {
		return onlyBlockCoordinates;
	}
	
	/**
	 * Sets whether this disguise may appear only on block coordinates.
	 * 
	 * @since 5.4.1
	 * @param onlyBlockCoordinates makes this disguise appear on block coordinates only
	 */
	public void setOnlyBlockCoordinates(boolean onlyBlockCoordinates) {
		this.onlyBlockCoordinates = onlyBlockCoordinates;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if(VersionHelper.require1_13()) {
			return String.format("%s; material=%s; %s", super.toString(),
					((BlockData)materialData).getAsString(),
					onlyBlockCoordinates ? "block-coordinates" : "all-coordinates");
		} else {
			return String.format("%s; material=%s; material-data=%s; %s", super.toString(),
					material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
					materialData,
					onlyBlockCoordinates ? "block-coordinates" : "all-coordinates");
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
			reader = new BufferedReader(new InputStreamReader(FallingBlockDisguise.class.getResourceAsStream("falling_block.txt")));
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
		
		if(VersionHelper.require1_13()) {
			Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setMaterialData", "material", String.class, parameterSuggestions);
		} else {
			Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setMaterial", "material", Material.class, Collections.unmodifiableSet(parameterSuggestions));
			Subtypes.registerParameterizedSubtype(FallingBlockDisguise.class, "setData", "material-data", int.class);
		}
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", true, "block-coordinates");
		Subtypes.registerSubtype(FallingBlockDisguise.class, "setOnlyBlockCoordinates", false, "all-coordinates");
	}
	
}