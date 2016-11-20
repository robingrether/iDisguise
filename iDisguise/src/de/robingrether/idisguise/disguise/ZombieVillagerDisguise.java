package de.robingrether.idisguise.disguise;

import java.util.Locale;

import de.robingrether.idisguise.disguise.VillagerDisguise.Profession;
import de.robingrether.idisguise.management.VersionHelper;

public class ZombieVillagerDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 3798088734739067588L;
	private Profession profession;
	
	public ZombieVillagerDisguise() {
		this(true, Profession.FARMER);
	}
	
	public ZombieVillagerDisguise(boolean adult, Profession profession) {
		super(DisguiseType.ZOMBIE_VILLAGER, adult);
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
	public ZombieVillagerDisguise clone() {
		ZombieVillagerDisguise clone = new ZombieVillagerDisguise(adult, profession);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ZombieVillagerDisguise && ((ZombieVillagerDisguise)object).profession.equals(profession);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + profession.name().toLowerCase(Locale.ENGLISH);
	}
	
	static {
		if(VersionHelper.require1_9()) {
			for(Profession profession : Profession.values()) {
				Subtypes.registerSubtype(ZombieVillagerDisguise.class, "setProfession", profession, profession.name().toLowerCase(Locale.ENGLISH));
			}
		}
	}
	
}