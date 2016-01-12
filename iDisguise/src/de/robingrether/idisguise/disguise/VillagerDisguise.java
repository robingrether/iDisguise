package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.entity.Villager.Profession;

/**
 * Represents a disguise as a villager.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class VillagerDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -8324476173444500691L;
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
	 * @since 3.0.1
	 * @param adult should the disguise be an adult
	 * @param profession the profession
	 */
	public VillagerDisguise(boolean adult, Profession profession) {
		super(DisguiseType.VILLAGER, adult);
		this.profession = profession;
	}
	
	/**
	 * Gets the profession.
	 * 
	 * @since 3.0.1
	 * @return the profession
	 */
	public Profession getProfession() {
		return profession;
	}
	
	/**
	 * Sets the profession.
	 * 
	 * @since 3.0.1
	 * @param profession the profession
	 */
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
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			try {
				Profession profession = Profession.valueOf(argument.toUpperCase(Locale.ENGLISH));
				setProfession(profession);
				return true;
			} catch(IllegalArgumentException e) {
				return false;
			}
		}
	}
	
}