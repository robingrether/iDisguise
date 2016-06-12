package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a disguise as a horse.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class HorseDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -7022070447102161970L;
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
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + variant.name().toLowerCase(Locale.ENGLISH).replace("_horse", "").replace("horse", "normal").replace("skeleton", "skeletal") + "; " + style.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings") + "; " + color.name().toLowerCase(Locale.ENGLISH).replace('_', '-') + "; " + (saddled ? "saddled" : "not-saddled") + "; " + (hasChest ? "chest" : "no-chest") + "; " + armor.name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor");
	}
	
	static {
		for(Variant variant : Variant.values()) {
			Subtypes.registerSubtype(HorseDisguise.class, "setVariant", variant, variant.name().toLowerCase(Locale.ENGLISH).replace("_horse", "").replace("horse", "normal").replace("skeleton", "skeletal"));
		}
		for(Style style : Style.values()) {
			Subtypes.registerSubtype(HorseDisguise.class, "setStyle", style, style.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings"));
		}
		for(Color color : Color.values()) {
			Subtypes.registerSubtype(HorseDisguise.class, "setColor", color, color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerSubtype(HorseDisguise.class, "setSaddled", true, "saddled");
		Subtypes.registerSubtype(HorseDisguise.class, "setSaddled", false, "not-saddled");
		Subtypes.registerSubtype(HorseDisguise.class, "setHasChest", true, "chest");
		Subtypes.registerSubtype(HorseDisguise.class, "setHasChest", false, "no-chest");
		for(Armor armor : Armor.values()) {
			Subtypes.registerSubtype(HorseDisguise.class, "setArmor", armor, armor.name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor"));
		}
	}
	
	/**
	 * Represents the different horse variants.
	 * 
	 * @since 5.0.1
	 * @author RobinGrether
	 */
	public enum Variant {
		
		HORSE,
		DONKEY,
		MULE,
		UNDEAD_HORSE,
		SKELETON_HORSE;
		
	}
	
	/**
	 * Represents the different hide styles of a horse.
	 * 
	 * @since 5.0.1
	 * @author RobinGrether
	 */
	public enum Style {
		
		NONE,
		WHITE,
		WHITEFIELD,
		WHITE_DOTS,
		BLACK_DOTS;
		
	}
	
	/**
	 * Represents the differnt hide colors of a horse.
	 * 
	 * @since 5.0.1
	 * @author RobinGrether
	 */
	public enum Color {
		
		WHITE,
		CREAMY,
		CHESTNUT,
		BROWN,
		BLACK,
		GRAY,
		DARK_BROWN;
		
	}
	
	/**
	 * Represents armor for a horse.
	 * 
	 * @since 3.0.3
	 * @author RobinGrether
	 */
	public enum Armor {
		
		IRON("IRON_BARDING"),
		GOLD("GOLD_BARDING"),
		DIAMOND("DIAMOND_BARDING"),
		NONE(null);
		
		private String item;
		
		private Armor(String item) {
			this.item = item;
		}
		
		/**
		 * Gets the associated Bukkit item stack.
		 * 
		 * @since 3.0.3
		 * @return the associated item stack
		 */
		public ItemStack getItem() {
			return Material.getMaterial(item) == null ? null : new ItemStack(Material.getMaterial(item), 1);
		}
		
	}
	
}