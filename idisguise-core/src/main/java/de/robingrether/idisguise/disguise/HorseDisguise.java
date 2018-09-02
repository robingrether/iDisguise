package de.robingrether.idisguise.disguise;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.robingrether.idisguise.management.VersionHelper;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a horse.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class HorseDisguise extends AgeableDisguise {
	
	private boolean saddled;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @param type the disguise type to use
	 * @throws IllegalArgumentException the given disguise type is not some sort of horse
	 */
	public HorseDisguise(DisguiseType type) {
		this(type, true, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @throws IllegalArgumentException the given disguise type is not some sort of horse
	 * 
	 * @deprecated Only {@linkplain StyledHorseDisguise} supports {@linkplain Armor}.
	 */
	@Deprecated
	public HorseDisguise(DisguiseType type, boolean adult, boolean saddled, Armor armor) {
		this(type, adult, saddled);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.8.1
	 * @throws IllegalArgumentException the given disguise type is not some sort of horse
	 */
	public HorseDisguise(DisguiseType type, boolean adult, boolean saddled) {
		super(type, adult);
		if(!ObjectUtil.equals(type, DisguiseType.DONKEY, DisguiseType.HORSE, DisguiseType.MULE, DisguiseType.SKELETAL_HORSE, DisguiseType.UNDEAD_HORSE)) {
			throw new IllegalArgumentException();
		}
		this.saddled = saddled;
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
	 * @since 3.0.3
	 * 
	 * @deprecated Only {@linkplain StyledHorseDisguise} supports {@linkplain Armor}.
	 */
	@Deprecated
	public Armor getArmor() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @since 3.0.3
	 * 
	 * @deprecated Only {@linkplain StyledHorseDisguise} supports {@linkplain Armor}.
	 */
	@Deprecated
	public void setArmor(Armor armor) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @since 5.5.1
	 */
	public int getVariant() {
		switch(type) {
			case HORSE: return 0;
			case DONKEY: return 1;
			case MULE: return 2;
			case UNDEAD_HORSE: return 3;
			case SKELETAL_HORSE: return 4;
			default: return -1;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s", super.toString(), saddled ? "saddled" : "not-saddled");
	}
	
	static {
		Subtypes.registerSimpleSubtype(HorseDisguise.class, disguise -> disguise.setSaddled(true), "saddled");
		Subtypes.registerSimpleSubtype(HorseDisguise.class, disguise -> disguise.setSaddled(false), "not-saddled");
	}
	
	/**
	 * Represents armor for a horse.
	 * 
	 * @since 3.0.3
	 * @author RobinGrether
	 */
	public enum Armor {
		
		IRON(VersionHelper.require1_13() ? "IRON_HORSE_ARMOR" : "IRON_BARDING"),
		GOLD(VersionHelper.require1_13() ? "GOLDEN_HORSE_ARMOR" : "GOLD_BARDING"),
		DIAMOND(VersionHelper.require1_13() ? "DIAMOND_HORSE_ARMOR" : "DIAMOND_BARDING"),
		NONE("AIR");
		
		private Material item;
		
		private Armor(String item) {
			this.item = Material.getMaterial(item);
		}
		
		/**
		 * Gets the associated Bukkit item stack.
		 * 
		 * @since 3.0.3
		 * @return the associated item stack
		 */
		public ItemStack getItem() {
			return new ItemStack(item, 1);
		}
		
	}
	
}