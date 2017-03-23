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
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public VillagerDisguise(boolean adult, Profession profession) {
		super(DisguiseType.VILLAGER, adult);
		this.profession = profession;
	}
	
	/**
	 * @since 5.5.1
	 */
	public Profession getProfession() {
		return profession;
	}
	
	/**
	 * @since 5.5.1
	 */
	public void setProfession(Profession profession) {
		this.profession = profession;
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