package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a horse.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class HorseDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 3739344872858787012L;
	private boolean saddled;
	private Armor armor;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public HorseDisguise(DisguiseType type) {
		this(type, true, false, Armor.NONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public HorseDisguise(DisguiseType type, boolean adult, boolean saddled, Armor armor) {
		super(type, adult);
		if(!ObjectUtil.equals(type, DisguiseType.DONKEY, DisguiseType.HORSE, DisguiseType.MULE, DisguiseType.SKELETAL_HORSE, DisguiseType.UNDEAD_HORSE)) {
			throw new IllegalArgumentException();
		}
		this.saddled = saddled;
		this.armor = armor;
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
	 * @since 5.5.1
	 */
	public int getVariant() {
		switch(type) {
			case HORSE: return 0;
			case DONKEY: return 1;
			case MULE: return 2;
			case SKELETAL_HORSE: return 3;
			case UNDEAD_HORSE: return 4;
			default: return -1;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public HorseDisguise clone() {
		HorseDisguise clone = new HorseDisguise(type, adult, saddled, armor);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof HorseDisguise && ((HorseDisguise)object).saddled == saddled && ((HorseDisguise)object).armor == armor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + (saddled ? "saddled" : "not-saddled") + "; " + armor.name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor");
	}
	
	static {
		Subtypes.registerSubtype(HorseDisguise.class, "setSaddled", true, "saddled");
		Subtypes.registerSubtype(HorseDisguise.class, "setSaddled", false, "not-saddled");
		for(Armor armor : Armor.values()) {
			Subtypes.registerSubtype(HorseDisguise.class, "setArmor", armor, armor.name().toLowerCase(Locale.ENGLISH).replace("none", "no-armor"));
		}
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