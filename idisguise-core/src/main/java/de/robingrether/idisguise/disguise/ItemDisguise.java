package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		this(new ItemStack(Material.STONE, 1, (short)0));
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
	// TODO
	
	static {
		Set<Material> tempSet = new HashSet<Material>(/*Arrays.asList(Material.ACACIA_DOOR, Material.AIR, Material.BED_BLOCK, Material.BIRCH_DOOR, Material.BREWING_STAND,
				Material.BURNING_FURNACE, Material.CAKE_BLOCK, Material.CARROT, Material.CAULDRON, Material.COCOA, Material.CROPS, Material.DARK_OAK_DOOR, Material.DAYLIGHT_DETECTOR_INVERTED,
				Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.DOUBLE_STEP, Material.DOUBLE_STONE_SLAB2, Material.ENDER_PORTAL, Material.FIRE, Material.FLOWER_POT,
				Material.GLOWING_REDSTONE_ORE, Material.IRON_DOOR_BLOCK, Material.JUNGLE_DOOR, Material.LAVA, Material.MELON_STEM, Material.MONSTER_EGGS, Material.NETHER_WARTS,
				Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.PORTAL, Material.POTATO, Material.PUMPKIN_STEM, Material.REDSTONE_COMPARATOR_ON,
				Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_LAMP_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_WIRE, Material.SIGN_POST, Material.SKULL, Material.SPRUCE_DOOR,
				Material.STANDING_BANNER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER, Material.SUGAR_CANE_BLOCK, Material.TRIPWIRE, Material.WALL_BANNER, Material.WALL_SIGN, Material.WATER,
				Material.WOOD_DOUBLE_STEP, Material.WOODEN_DOOR));
		if(VersionHelper.require1_9()) {
			tempSet.addAll(Arrays.asList(Material.BEETROOT_BLOCK, Material.END_GATEWAY, Material.FROSTED_ICE, Material.PURPUR_DOUBLE_SLAB)*/);
		//}
		INVALID_MATERIALS = Collections.unmodifiableSet(tempSet);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Material material : Material.values()) {
			if(!INVALID_MATERIALS.contains(material)) {
				parameterSuggestions.add(material.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
		}
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, "setMaterial", "material", Material.class, Collections.unmodifiableSet(parameterSuggestions));
		
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, "setDurability", "durability", short.class);
		Subtypes.registerParameterizedSubtype(ItemDisguise.class, "setAmount", "amount", int.class, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("1", "2", "64"))));
		
		Subtypes.registerSubtype(ItemDisguise.class, "setEnchanted", true, "enchanted");
		Subtypes.registerSubtype(ItemDisguise.class, "setEnchanted", false, "not-enchanted");
		
		if(VersionHelper.require1_11()) {
			Subtypes.registerSubtype(ItemDisguise.class, "setUnbreakable", true, "unbreakable");
			Subtypes.registerSubtype(ItemDisguise.class, "setUnbreakable", false, "not-unbreakable");
		}
	}
	
}