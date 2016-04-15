package de.robingrether.idisguise.disguise;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

public class ArmorStandDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = 2192535300050398947L;
	private Armor helmet;
	private Armor chestplate;
	private Armor leggings;
	private Armor boots;
	private ItemStack itemInHand;
	
	public ArmorStandDisguise() {
		this(Armor.NONE, Armor.NONE, Armor.NONE, Armor.NONE, null);
	}
	
	public ArmorStandDisguise(Armor helmet, Armor chestplate, Armor leggings, Armor boots, ItemStack itemInHand) {
		super(DisguiseType.ARMOR_STAND);
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.itemInHand = itemInHand;
	}
	
	public Armor getHelmet() {
		return helmet;
	}
	
	public void setHelmet(Armor helmet) {
		this.helmet = helmet;
	}
	
	public Armor getChestplate() {
		return chestplate;
	}
	
	public void setChestplate(Armor chestplate) {
		this.chestplate = chestplate;
	}
	
	public Armor getLeggings() {
		return leggings;
	}
	
	public void setLeggings(Armor leggings) {
		this.leggings = leggings;
	}
	
	public Armor getBoots() {
		return boots;
	}
	
	public void setBoots(Armor boots) {
		this.boots = boots;
	}
	
	public ItemStack getItemInHand() {
		return itemInHand;
	}
	
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
		Matcher matcher = argumentPattern.matcher(argument);
		if(matcher.matches()) {
			String type = matcher.group(1);
			String position = matcher.group(2);
			Armor armor;
			
		}
		return false;
	}
	
	private static final Pattern argumentPattern = Pattern.compile("(no|leather|chainmail|iron|gold|diamond)[-_]?(helmet|chestplate|leggings|boots)");
	
	public enum Armor {
		
		NONE,
		LEATHER,
		CHAINMAIL,
		IRON,
		GOLD,
		DIAMOND;
		
	}
	
}