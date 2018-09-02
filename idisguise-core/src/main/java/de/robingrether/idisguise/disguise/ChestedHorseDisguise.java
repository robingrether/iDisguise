package de.robingrether.idisguise.disguise;

import de.robingrether.idisguise.disguise.HorseDisguise.Armor;
import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a chested horse (donkey or mule).
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class ChestedHorseDisguise extends HorseDisguise {
	
	private boolean hasChest;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @param type the disguise type (must be either {@linkplain DisguiseType#DONKEY} or {@linkplain DisguiseType#MULE})
	 */
	public ChestedHorseDisguise(DisguiseType type) {
		this(type, true, false, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @param type the disguise type (must be either {@linkplain DisguiseType#DONKEY} or {@linkplain DisguiseType#MULE})
	 * 
	 * @deprecated Only {@linkplain StyledHorseDisguise} supports {@linkplain Armor}.
	 */
	@Deprecated
	public ChestedHorseDisguise(DisguiseType type, boolean adult, boolean hasChest, boolean saddled, Armor armor) {
		this(type, adult, hasChest, saddled);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.8.1
	 * @param type the disguise type (must be either {@linkplain DisguiseType#DONKEY} or {@linkplain DisguiseType#MULE})
	 */
	public ChestedHorseDisguise(DisguiseType type, boolean adult, boolean hasChest, boolean saddled) {
		super(type, adult, saddled);
		if(!ObjectUtil.equals(type, DisguiseType.DONKEY, DisguiseType.MULE)) {
			throw new IllegalArgumentException();
		}
		this.hasChest = hasChest;
	}
	
	/**
	 * Indicates whether the horse carries a chest.
	 * 
	 * @since 5.5.1
	 * @return <code>true</code> in case the horse carries a chest
	 */
	public boolean hasChest() {
		return hasChest;
	}
	
	/**
	 * Sets whether the horse carries a chest.
	 * 
	 * @since 5.5.1
	 * @param hasChest <code>true</code> in case the horse shall carry a chest
	 */
	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s", super.toString(), hasChest ? "chest" : "no-chest");
	}
	
	static {
		Subtypes.registerSimpleSubtype(ChestedHorseDisguise.class, disguise -> disguise.setHasChest(true), "chest");
		Subtypes.registerSimpleSubtype(ChestedHorseDisguise.class, disguise -> disguise.setHasChest(false), "no-chest");
	}
	
}