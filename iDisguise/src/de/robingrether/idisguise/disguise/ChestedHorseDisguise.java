package de.robingrether.idisguise.disguise;

import de.robingrether.util.ObjectUtil;

/**
 * Represents a disguise as a chested horse (donkey or mule).
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class ChestedHorseDisguise extends HorseDisguise {
	
	private static final long serialVersionUID = -5787356640489884627L;
	private boolean hasChest;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @param type the disguise type (must be either {@linkplain DisguiseType#DONKEY} or {@linkplain DisguiseType#MULE})
	 */
	public ChestedHorseDisguise(DisguiseType type) {
		this(type, true, false, false, Armor.NONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 * @param type the disguise type (must be either {@linkplain DisguiseType#DONKEY} or {@linkplain DisguiseType#MULE})
	 * @param adult whether the disguise should appear as an adult or as a baby
	 * @param hasChest whether the disguise should have a chest or not
	 * @param saddled whether the disguise should be saddled or not
	 * @param armor the type of armor the disguise should carry
	 */
	public ChestedHorseDisguise(DisguiseType type, boolean adult, boolean hasChest, boolean saddled, Armor armor) {
		super(type, adult, saddled, armor);
		if(!ObjectUtil.equals(type, DisguiseType.DONKEY, DisguiseType.MULE)) {
			throw new IllegalArgumentException();
		}
		this.hasChest = hasChest;
	}
	
	/**
	 * @since 5.5.1
	 */
	public boolean hasChest() {
		return hasChest;
	}
	
	/**
	 * @since 5.5.1
	 */
	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ChestedHorseDisguise clone() {
		ChestedHorseDisguise clone = new ChestedHorseDisguise(type, adult, hasChest, isSaddled(), getArmor());
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ChestedHorseDisguise && ((ChestedHorseDisguise)object).hasChest == hasChest;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + (hasChest ? "chest" : "no-chest");
	}
	
	static {
		Subtypes.registerSubtype(ChestedHorseDisguise.class, "setHasChest", true, "chest");
		Subtypes.registerSubtype(ChestedHorseDisguise.class, "setHasChest", false, "no-chest");
	}
	
}