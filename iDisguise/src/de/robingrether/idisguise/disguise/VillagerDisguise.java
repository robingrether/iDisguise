package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.entity.Villager.Profession;

/**
 * Represents a disguise as a villager.
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class VillagerDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 4811148064924974891L;
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
	@SuppressWarnings("deprecation")
	public EntityVillager getEntity(World world, Location location, int id) {
		EntityVillager entity = (EntityVillager)super.getEntity(world, location, id);
		entity.setProfession(profession.getId());
		return entity;
	}
	
}