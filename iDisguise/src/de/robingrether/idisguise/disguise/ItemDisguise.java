package de.robingrether.idisguise.disguise;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a disguise as an item stack.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class ItemDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = -4607689524782164382L;
	private ItemStack itemStack;
	
	public ItemDisguise() {
		this(new ItemStack(Material.STONE, 1, (short)0));
	}
	
	public ItemDisguise(ItemStack itemStack) {
		super(DisguiseType.ITEM);
		if(itemStack == null) {
			throw new IllegalArgumentException("Item stack must not be null");
		}
		this.itemStack = itemStack;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public void setItemStack(ItemStack itemStack) {
		if(itemStack == null) {
			throw new IllegalArgumentException("Item stack must not be null");
		}
		this.itemStack = itemStack;
	}
	
}