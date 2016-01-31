package de.robingrether.idisguise.disguise;

import java.util.Locale;

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
		if(itemStack == null) {
			throw new IllegalArgumentException("Item stack must not be null");
		}
		this.itemStack = itemStack;
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
		this.itemStack = itemStack;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ItemDisguise clone() {
		return new ItemDisguise(itemStack.clone());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ItemDisguise && ((ItemDisguise)object).itemStack.equals(itemStack);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		try {
			Material material = Material.valueOf(argument.replace('-', '_').toUpperCase(Locale.ENGLISH));
			itemStack.setType(material);
			return true;
		} catch(IllegalArgumentException e) {
		}
		try {
			short damage = Short.parseShort(argument);
			if(damage >= 0 && damage < 256) {
				itemStack.setDurability(damage);
				return true;
			}
		} catch(NumberFormatException e) {
		}
		switch(argument.toLowerCase(Locale.ENGLISH)) {
			case "single":
				itemStack.setAmount(1); //   1     1
				return true;
			case "double":
				itemStack.setAmount(2); //   2     2
				return true;
			case "triple":
				itemStack.setAmount(17); //  17    16
				return true;
			case "quadruple":
				itemStack.setAmount(33); // 33    33
				return true;
			case "quintuple":
				itemStack.setAmount(49); // 49    --
				return true;
			default:
				return false;
		}
	}
	
}