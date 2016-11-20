package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.entity.Villager.Profession;

import de.robingrether.util.StringUtil;

public class ZombieVillagerDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 5411908005613164697L;
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
		for(Profession profession : Profession.values()) {
			if(!StringUtil.equals(profession.name(), "NORMAL", "HUSK")) {
				Subtypes.registerSubtype(ZombieVillagerDisguise.class, "setProfession", profession, profession.name().toLowerCase(Locale.ENGLISH));
			}
		}
	}
	
}