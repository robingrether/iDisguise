package de.robingrether.idisguise.disguise;

public class ChestedHorseDisguise extends HorseDisguise {
	
	private static final long serialVersionUID = -5787356640489884627L;
	private boolean hasChest;
	
	public ChestedHorseDisguise(DisguiseType type) {
		this(type, true, false, false, Armor.NONE);
	}
	
	public ChestedHorseDisguise(DisguiseType type, boolean adult, boolean hasChest, boolean saddled, Armor armor) {
		super(type, adult, saddled, armor);
		this.hasChest = hasChest;
	}
	
	public boolean hasChest() {
		return hasChest;
	}
	
	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}
	
	public ChestedHorseDisguise clone() {
		ChestedHorseDisguise clone = new ChestedHorseDisguise(type, hasChest, hasChest, isSaddled(), getArmor());
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ChestedHorseDisguise && ((ChestedHorseDisguise)object).hasChest == hasChest;
	}
	
	public String toString() {
		return super.toString() + "; " + (hasChest ? "chest" : "no-chest");
	}
	
	static {
		Subtypes.registerSubtype(ChestedHorseDisguise.class, "setHasChest", true, "chest");
		Subtypes.registerSubtype(ChestedHorseDisguise.class, "setHasChest", false, "no-chest");
	}
	
}