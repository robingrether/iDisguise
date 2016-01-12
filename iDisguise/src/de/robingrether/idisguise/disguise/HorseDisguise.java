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
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			switch(argument.replace('-', '_').toLowerCase(Locale.ENGLISH)) {
				case "donkey":
					setVariant(Variant.DONKEY);
					return true;
				case "normal":
					setVariant(Variant.HORSE);
					return true;
				case "mule":
					setVariant(Variant.MULE);
					return true;
				case "skeleton":
				case "skeletal":
					setVariant(Variant.SKELETON_HORSE);
					return true;
				case "undead":
				case "zombie":
					setVariant(Variant.UNDEAD_HORSE);
					return true;
				case "blackdots":
				case "black_dots":
					setStyle(Style.BLACK_DOTS);
					return true;
				case "nomarkings":
				case "no_markings":
					setStyle(Style.NONE);
					return true;
				case "whitestripes":
				case "white_stripes":
					setStyle(Style.WHITE);
					return true;
				case "whitedots":
				case "white_dots":
					setStyle(Style.WHITE_DOTS);
					return true;
				case "whitefield":
					setStyle(Style.WHITEFIELD);
					return true;
				case "black":
					setColor(Color.BLACK);
					return true;
				case "brown":
					setColor(Color.BROWN);
					return true;
				case "chestnut":
					setColor(Color.CHESTNUT);
					return true;
				case "creamy":
				case "cream":
					setColor(Color.CREAMY);
					return true;
				case "darkbrown":
				case "dark_brown":
					setColor(Color.DARK_BROWN);
					return true;
				case "gray":
				case "grey":
					setColor(Color.GRAY);
					return true;
				case "white":
					setColor(Color.WHITE);
					return true;
				case "saddled":
				case "saddle":
					setSaddled(true);
					return true;
				case "notsattled":
				case "not_saddled":
				case "nosaddle":
				case "no_saddle":
					setSaddled(false);
					return true;
				case "chest":
					setHasChest(true);
					return true;
				case "nochest":
				case "no_chest":
					setHasChest(false);
					return true;
				case "noarmor":
				case "no_armor":
					setArmor(Armor.NONE);
					return true;
				case "iron":
					setArmor(Armor.IRON);
					return true;
				case "gold":
					setArmor(Armor.GOLD);
					return true;
				case "diamond":
					setArmor(Armor.DIAMOND);
					return true;
				default:
					return false;
			}
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