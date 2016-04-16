package de.robingrether.idisguise.disguise;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a disguise as an armor stand.
 * 
 * @since 5.2.2
 * @author RobinGrether
 */
public class ArmorStandDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = 2192535300050398947L;
	private Armor helmet;
	private Armor chestplate;
	private Armor leggings;
	private Armor boots;
	private ItemStack itemInHand;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 */
	public ArmorStandDisguise() {
		this(Armor.NONE, Armor.NONE, Armor.NONE, Armor.NONE, null);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.2.2
	 * @param helmet helmet armor type
	 * @param chestplate chestplate armor type
	 * @param leggings leggings armor type
	 * @param boots boots armor type
	 * @param itemInHand item shown in hand
	 */
	public ArmorStandDisguise(Armor helmet, Armor chestplate, Armor leggings, Armor boots, ItemStack itemInHand) {
		super(DisguiseType.ARMOR_STAND);
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.itemInHand = itemInHand;
	}
	
	/**
	 * Get the helmet armor type.
	 * 
	 * @since 5.2.2
	 * @return helmet armor type
	 */
	public Armor getHelmet() {
		return helmet;
	}
	
	/**
	 * Set the helmet armor type.
	 * 
	 * @since 5.2.2
	 * @param helmet helmet armor type
	 */
	public void setHelmet(Armor helmet) {
		this.helmet = helmet;
	}
	
	/**
	 * Get the chestplate armor type.
	 * 
	 * @since 5.2.2
	 * @return chestplate armor type
	 */
	public Armor getChestplate() {
		return chestplate;
	}
	
	/**
	 * Set the chestplate armor type.
	 * 
	 * @since 5.2.2
	 * @param chestplate chestplate armor type
	 */
	public void setChestplate(Armor chestplate) {
		this.chestplate = chestplate;
	}
	
	/**
	 * Get the leggings armor type.
	 * 
	 * @since 5.2.2
	 * @return leggings armor type
	 */
	public Armor getLeggings() {
		return leggings;
	}
	
	/**
	 * Set the leggings armor type.
	 * 
	 * @since 5.2.2
	 * @param leggings leggings armor type
	 */
	public void setLeggings(Armor leggings) {
		this.leggings = leggings;
	}
	
	/**
	 * Get the boots armor type.
	 * 
	 * @since 5.2.2
	 * @return boots armor type
	 */
	public Armor getBoots() {
		return boots;
	}
	
	/**
	 * Set the boots armor type.
	 * 
	 * @since 5.2.2
	 * @param boots boots armor type
	 */
	public void setBoots(Armor boots) {
		this.boots = boots;
	}
	
	/**
	 * Get the item shown in hand.
	 * 
	 * @since 5.2.2
	 * @return item shown in hand
	 */
	public ItemStack getItemInHand() {
		return itemInHand;
	}
	
	/**
	 * Set the item shown in hand.
	 * 
	 * @since 5.2.2
	 * @param itemInHand item shown in hand
	 */
	public void setItemInHand(ItemStack itemInHand) {
		this.itemInHand = itemInHand;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ArmorStandDisguise clone() {
		return new ArmorStandDisguise(helmet, chestplate, leggings, boots, itemInHand.clone());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ArmorStandDisguise && ((ArmorStandDisguise)object).helmet.equals(helmet) && ((ArmorStandDisguise)object).chestplate.equals(chestplate) && ((ArmorStandDisguise)object).leggings.equals(leggings) && ((ArmorStandDisguise)object).boots.equals(boots) && ((ArmorStandDisguise)object).itemInHand.equals(itemInHand);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		Matcher matcher = argumentPattern.matcher(argument.toLowerCase(Locale.ENGLISH));
		if(matcher.matches()) {
			String type = matcher.group(1);
			String position = matcher.group(2);
			Armor armor = Armor.valueOf(type.replace("no", "none").toUpperCase(Locale.ENGLISH));
			switch(position) {
				case "helmet":
					setHelmet(armor);
					break;
				case "chestplate":
					setChestplate(armor);
					break;
				case "leggings":
					setLeggings(armor);
					break;
				case "boots":
					setBoots(armor);
					break;
			}
			return true;
		}
		return false;
	}
	
	private static final Pattern argumentPattern = Pattern.compile("(no|leather|chainmail|iron|gold|diamond)[-_]?(helmet|chestplate|leggings|boots)");
	
	/**
	 * Represents the different materials armor may be made out of.<br>
	 * {@linkplain Armor#NONE} means the slot is empty.
	 * 
	 * @since 5.2.2
	 * @author RobinGrether
	 */
	public enum Armor {
		
		NONE,
		LEATHER,
		CHAINMAIL,
		IRON,
		GOLD,
		DIAMOND;
		
	}
	
}