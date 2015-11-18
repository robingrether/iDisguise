package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a zombie.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class ZombieDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 3233813531511391233L;
	private boolean isVillager;
	
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
		this.isVillager = isVillager;
	}
	
	/**
	 * Returns whether the zombie is an infected villager.
	 * 
	 * @since 4.0.1
	 * @return <code>true</code>, if the zombie is an infected villager
	 */
	public boolean isVillager() {
		return isVillager;
	}
	
	/**
	 * Sets whether the zombie should be an infected villager.
	 * 
	 * @since 4.0.1
	 * @param isVillager <code>true</code>, if the zombie should be an infected villager
	 */
	public void setVillager(boolean isVillager) {
		this.isVillager = isVillager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ZombieDisguise clone() {
		ZombieDisguise clone = new ZombieDisguise(adult, isVillager);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ZombieDisguise && ((ZombieDisguise)object).isVillager == isVillager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			switch(argument.toLowerCase(Locale.ENGLISH)) {
				case "normal":
					setVillager(false);
					return true;
				case "infected":
					setVillager(true);
					return true;
				default:
					return false;
			}
		}
	}
	
}