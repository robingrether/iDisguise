package de.robingrether.idisguise.disguise;

import org.bukkit.Material;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a disguise as a horse.
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class HorseDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 858102950254299362L;
	private Variant variant;
	private Style style;
	private Color color;
	private boolean saddled;
	private boolean hasChest;
	private Armor armor;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public HorseDisguise() {
		this(true, Variant.HORSE, Style.NONE, Color.BROWN, false, false, Armor.NONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.3
	 * @param adult should the disguise be an adult
	 * @param variant the variant of the horse
	 * @param style the style of the horse
	 * @param color the color of the horse
	 * @param saddled should the horse be saddled
	 * @param hasChest should the horse carry a chest
	 * @param armor the armor of the horse
	 */
	public HorseDisguise(boolean adult, Variant variant, Style style, Color color, boolean saddled, boolean hasChest, Armor armor) {
		super(DisguiseType.HORSE, adult);
		this.variant = variant;
		this.style = style;
		this.color = color;
		this.saddled = saddled;
		this.hasChest = hasChest;
		this.armor = armor;
	}
	
	/**
	 * Gets the color.
	 * 
	 * @since 3.0.1
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @since 3.0.1
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Gets the style.
	 * 
	 * @since 3.0.1
	 * @return the style
	 */
	public Style getStyle() {
		return style;
	}
	
	/**
	 * Sets the style.
	 * 
	 * @since 3.0.1
	 * @param style the style
	 */
	public void setStyle(Style style) {
		this.style = style;
	}
	
	/**
	 * Gets the variant.
	 * 
	 * @since 3.0.1
	 * @return the variant
	 */
	public Variant getVariant() {
		return variant;
	}
	
	/**
	 * Sets the variant.
	 * 
	 * @since 3.0.1
	 * @param variant the variant
	 */
	public void setVariant(Variant variant) {
		this.variant = variant;
	}
	
	/**
	 * Gets whether the horse is saddled.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if it is saddled
	 */
	public boolean isSaddled() {
		return saddled;
	}
	
	/**
	 * Sets whether the horse is saddled.
	 * 
	 * @since 3.0.1
	 * @param saddled should the horse be saddled
	 */
	public void setSaddled(boolean saddled) {
		this.saddled = saddled;
	}
	
	/**
	 * Gets whether the horse carries a chest.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the horse carries a chest
	 */
	public boolean hasChest() {
		return hasChest;
	}
	
	/**
	 * Sets whether the horse carries a chest.
	 * 
	 * @since 3.0.1
	 * @param hasChest should the horse carry a chest
	 */
	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}
	
	/**
	 * Gets the armor.
	 * 
	 * @since 3.0.3
	 * @return the armor
	 */
	public Armor getArmor() {
		return armor;
	}
	
	/**
	 * Sets the armor.
	 * 
	 * @since 3.0.3
	 * @param armor the armor
	 */
	public void setArmor(Armor armor) {
		this.armor = armor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public HorseDisguise clone() {
		HorseDisguise clone = new HorseDisguise(adult, variant, style, color, saddled, hasChest, armor);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof HorseDisguise && ((HorseDisguise)object).color.equals(color) && ((HorseDisguise)object).style.equals(style) && ((HorseDisguise)object).variant.equals(variant) && ((HorseDisguise)object).saddled == saddled && ((HorseDisguise)object).hasChest == hasChest && ((HorseDisguise)object).armor == armor;
	}
	
	/**
	 * Represents armor for a horse.
	 * 
	 * @since 3.0.3
	 * @author Robingrether
	 */
	public enum Armor {
		
		IRON(new ItemStack(Material.IRON_BARDING)),
		GOLD(new ItemStack(Material.GOLD_BARDING)),
		DIAMOND(new ItemStack(Material.DIAMOND_BARDING)),
		NONE(null);
		
		private ItemStack item;
		
		private Armor(ItemStack item) {
			this.item = item;
		}
		
		/**
		 * Gets the associated Bukkit item stack.
		 * 
		 * @since 3.0.3
		 * @return the associated item stack
		 */
		public ItemStack getItem() {
			return item;
		}
		
	}
	
}