package de.robingrether.idisguise.disguise;

import org.bukkit.Location;

import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;

public class ZombieDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 3233813531511391233L;
	private boolean isVillager;
	
	public ZombieDisguise() {
		this(true);
	}
	
	public ZombieDisguise(boolean adult) {
		this(adult, false);
	}
	
	public ZombieDisguise(boolean adult, boolean isVillager) {
		super(DisguiseType.ZOMBIE, adult);
		this.isVillager = isVillager;
	}
	
	public boolean isVillager() {
		return isVillager;
	}
	
	public void setVillager(boolean isVillager) {
		this.isVillager = isVillager;
	}
	
	public ZombieDisguise clone() {
		ZombieDisguise clone = new ZombieDisguise(adult, isVillager);
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ZombieDisguise && ((ZombieDisguise)object).isVillager == isVillager;
	}
	
	public EntityZombie getEntity(World world, Location location, int id) {
		EntityZombie zombie = (EntityZombie)super.getEntity(world, location, id);
		zombie.setVillager(isVillager);
		return zombie;
	}
	
}