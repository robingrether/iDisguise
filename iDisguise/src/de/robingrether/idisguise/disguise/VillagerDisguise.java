package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a villager.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class VillagerDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 8847244844878844913L;
	private Profession profession;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public VillagerDisguise() {
		this(true, Profession.FARMER);
	}
	
	public VillagerDisguise(boolean adult, Profession profession) {
		super(DisguiseType.VILLAGER, adult);
		this.profession = profession;
	}
	
	public Profession getProfession() {
		return profession;
	}
	
	public void setProfession(Profession profession) {
		this.profession = profession;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public VillagerDisguise clone() {
		VillagerDisguise clone = new VillagerDisguise(adult, profession);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof VillagerDisguise && ((VillagerDisguise)object).profession.equals(profession);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + profession.name().toLowerCase(Locale.ENGLISH);
	}
	
	static {
		for(Profession profession : Profession.values()) {
			Subtypes.registerSubtype(VillagerDisguise.class, "setProfession", profession, profession.name().toLowerCase(Locale.ENGLISH));
		}
	}
	
	public enum Profession {
		
		FARMER,
		LIBRARIAN,
		PRIEST,
		BLACKSMITH,
		BUTCHER,
		NITWIT;
		
	}
	
}