package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.entity.Villager.Profession;

import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.util.StringUtil;

/**
 * Represents a disguise as a zombie.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class ZombieDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -181897537633036406L;
	private Profession villagerType;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public ZombieDisguise() {
		this(true);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param adult whether the zombie should be an adult
	 */
	public ZombieDisguise(boolean adult) {
		this(adult, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param adult whether the zombie should be an adult
	 * @param isVillager whether the zombie should be an infected villager
	 */
	public ZombieDisguise(boolean adult, boolean isVillager) {
		super(DisguiseType.ZOMBIE, adult);
		this.villagerType = isVillager ? Profession.FARMER : VersionHelper.require1_10() ? Profession.valueOf("NORMAL") : null;
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.3.1
	 * @param adult whether the zombie should be an adult
	 * @param villagerType the villager type of the zombie
	 */
	public ZombieDisguise(boolean adult, Profession villagerType) {
		super(DisguiseType.ZOMBIE, adult);
		this.villagerType = villagerType;
	}
	
	/**
	 * Indicates whether the zombie is a husk.
	 * 
	 * @since 5.3.3
	 * @return <code>true</code> if and only if this zombie is a husk
	 */
	public boolean isHusk() {
		return villagerType != null && villagerType.name().equals("HUSK");
	}
	
	/**
	 * Returns whether the zombie is an infected villager.
	 * 
	 * @since 4.0.1
	 * @return <code>true</code>, if the zombie is an infected villager
	 */
	public boolean isVillager() {
		return villagerType != null && !StringUtil.equals(villagerType.name(), "NORMAL", "HUSK");
	}
	
	/**
	 * Sets whether the zombie should be an infected villager.
	 * 
	 * @since 4.0.1
	 * @param isVillager <code>true</code>, if the zombie should be an infected villager
	 */
	public void setVillager(boolean isVillager) {
		if(isVillager) {
			villagerType = Profession.FARMER;
		} else {
			if(VersionHelper.require1_10()) {
				villagerType = Profession.valueOf("NORMAL");
			} else {
				villagerType = null;
			}
		}
	}
	
	/**
	 * Gets the villager type of this zombie disguise.
	 * 
	 * @since 5.3.1
	 * @return the villager type
	 */
	public Profession getVillagerType() {
		return villagerType;
	}
	
	/**
	 * Sets the villager type of this zombie disguise.
	 * 
	 * @since 5.3.1
	 * @param villagerType the villager type
	 */
	public void setVillagerType(Profession villagerType) {
		this.villagerType = villagerType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ZombieDisguise clone() {
		ZombieDisguise clone = new ZombieDisguise(adult, villagerType);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ZombieDisguise && ((ZombieDisguise)object).villagerType == villagerType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		if(VersionHelper.require1_10()) {
			return super.toString() + "; " + villagerType.name().toLowerCase(Locale.ENGLISH);
		} else if(VersionHelper.require1_9()) {
			return super.toString() + "; " + villagerType != null ? villagerType.name().toLowerCase(Locale.ENGLISH) : "normal";
		} else {
			return super.toString() + "; " + (isVillager() ? "infected" : "normal");
		}
	}
	
	static {
		if(VersionHelper.require1_10()) {
			for(Profession villagerType : Profession.values()) {
				Subtypes.registerSubtype(ZombieDisguise.class, "setVillagerType", villagerType, villagerType.name().toLowerCase(Locale.ENGLISH));
			}
		} else if(VersionHelper.require1_9()) {
			for(Profession villagerType : Profession.values()) {
				Subtypes.registerSubtype(ZombieDisguise.class, "setVillagerType", villagerType, villagerType.name().toLowerCase(Locale.ENGLISH));
			}
			Subtypes.registerSubtype(ZombieDisguise.class, "setVillager", false, "normal");
		} else {
			Subtypes.registerSubtype(ZombieDisguise.class, "setVillagerType", Profession.FARMER, "infected");
			Subtypes.registerSubtype(ZombieDisguise.class, "setVillager", false, "normal");
		}
	}
	
}