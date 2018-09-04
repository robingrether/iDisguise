package de.robingrether.idisguise.disguise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as an item stack.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class ItemDisguise extends ObjectDisguise {
	
	private ItemStack itemStack;
	
	/**
	 * Creates an instance.<br>
	 * The default item stack is one stone.
	 * 
	 * @since 5.1.1
	 */
	public ItemDisguise() {
		this(new ItemStack(Material.STONE, 1));
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param itemStack the displayed item stack
	 * @throws IllegalArgumentException if the item stack is <code>null</code>
	 */
	public ItemDisguise(ItemStack itemStack) {
		super(DisguiseType.ITEM);
		setItemStack(itemStack);
	}
	
	/**
	 * Gets the displayed item stack.
	 * 
	 * @since 5.1.1
	 * @return a copy of the displayed item stack
	 */
	public ItemStack getItemStack() {
		return itemStack.clone();
	}
	
	/**
	 * Sets the displayed item stack.
	 * 
	 * @since 5.1.1
	 * @param itemStack the displayed item stack
	 * @throws IllegalArgumentException if the item stack is null
	 */
	public void setItemStack(ItemStack itemStack) {
		if(itemStack == null) {
			throw new IllegalArgumentException("Item stack must not be null");
		}
		if(INVALID_MATERIALS.contains(itemStack.getType())) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		this.itemStack = itemStack;
	}
	
	/**
	 * Gets the material.
	 * 
	 * @since 5.3.1
	 * @return the material
	 */
	public Material getMaterial() {
		return itemStack.getType();
	}
	
	/**
	 * Sets the material.<br>
	 * This also resets the data.
	 * 
	 * @since 5.3.1
	 * @param material the material
	 */
	public void setMaterial(Material material) {
		if(INVALID_MATERIALS.contains(material)) {
			throw new IllegalArgumentException("Material is invalid! Disguise would be invisible.");
		}
		if(material.name().startsWith("LEGACY")) {
			throw new IllegalArgumentException("Legacy materials are invalid!");
		}
		itemStack.setType(material);
		itemStack.setDurability((short)0);
	}
	
	/**
	 * @since 5.3.1
	 */
	@Deprecated
	public int getData() {
		return itemStack.getDurability();
	}
	
	/**
	 * @since 5.3.1
	 */
	@Deprecated
	public void setData(int data) {
		itemStack.setDurability((short)data);
	}
	
	public short getDurability() {
		return itemStack.getDurability();
	}
	
	public void setDurability(short durability) {
		itemStack.setDurability(durability);
	}
	
	/**
	 * Gets the amount.
	 * 
	 * @since 5.3.1
	 * @return the amount
	 */
	public int getAmount() {
		return itemStack.getAmount();
	}
	
	/**
	 * Sets the amount.
	 * 
	 * @since 5.3.1
	 * @param amount the amount
	 */
	public void setAmount(int amount) {
		if(amount < 1 || amount > 64) {
			throw new IllegalArgumentException("Invalid item stack amount");
		}
		itemStack.setAmount(amount);
	}
	
	/**
	 * Indicates whether the associated item stack of this disguise contains any enchantments.
	 * 
	 * @since 5.6.4
	 * @return <code>true</code>, if and only if the item stack contains any enchantments
	 */
	public boolean isEnchanted() {
		return !itemStack.getEnchantments().isEmpty();
	}
	
	/**
	 * Sets whether this item disguise appears enchanted.
	 * 
	 * @since 5.6.4
	 * @param enchanted <code>true</code> means enchanted
	 */
	public void setEnchanted(boolean enchanted) {
		if(enchanted) {
			itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		} else {
			for(Enchantment enchantment : Collections.unmodifiableMap(itemStack.getEnchantments()).keySet()) {
				itemStack.removeEnchantment(enchantment);
			}
		}
	}
	
	/**
	 * @since 5.8.1
	 */
	public boolean isUnbreakable() {
		return VersionHelper.require1_11() ? itemStack.getItemMeta().isUnbreakable() : false;
	}
	
	/**
	 * @since 5.8.1
	 */
	public void setUnbreakable(boolean unbreakable) {
		if(!VersionHelper.require1_11()) return;
		ItemMeta meta = itemStack.getItemMeta();
		meta.setUnbreakable(unbreakable);
		itemStack.setItemMeta(meta);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if(VersionHelper.require1_11()) {
			return String.format("%s; material=%s; material-data=%s; amount=%s; %s; %s", super.toString(),
					itemStack.getType().name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
					itemStack.getDurability(), itemStack.getAmount(),
					isEnchanted() ? "enchanted" : "not-enchanted",
					isUnbreakable() ? "unbreakable" : "not-unbreakable");
		} else {
			return String.format("%s; material=%s; material-data=%s; amount=%s; %s", super.toString(),
					itemStack.getType().name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
					itemStack.getDurability(), itemStack.getAmount(),
					isEnchanted() ? "enchanted" : "not-enchanted");
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
			reader = new BufferedReader(new InputStreamReader(EndermanDisguise.class.getResourceAsStream("item.txt")));
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
			if(!INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, (disguise, parameter) -> disguise.setMaterial(Material.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "material", parameterSuggestions);
		
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, (disguise, parameter) -> disguise.setDurability(Short.valueOf(parameter)), "durability");
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, (disguise, parameter) -> disguise.setAmount(Integer.valueOf(parameter)), "amount", new HashSet<String>(Arrays.asList("1", "2", "64")));
		
		Subtypes.registerSimpleSubtype(ItemDisguise.class, disguise -> disguise.setEnchanted(true), "enchanted");
		Subtypes.registerSimpleSubtype(ItemDisguise.class, disguise -> disguise.setEnchanted(false), "not-enchanted");
		
		if(VersionHelper.require1_11()) {
			Subtypes.registerSimpleSubtype(ItemDisguise.class, disguise -> disguise.setUnbreakable(true), "unbreakable");
			Subtypes.registerSimpleSubtype(ItemDisguise.class, disguise -> disguise.setUnbreakable(false), "not-unbreakable");
		}
	}
	
}