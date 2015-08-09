package de.robingrether.idisguise.disguise;

import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;

public class CreeperDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -5787233589068911050L;
	private boolean powered;
	
	public CreeperDisguise() {
		this(false);
	}
	
	public CreeperDisguise(boolean powered) {
		super(DisguiseType.CREEPER, true);
		this.powered = powered;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public void setPowered(boolean powered) {
		this.powered = powered;
	}
	
	public CreeperDisguise clone() {
		CreeperDisguise clone = new CreeperDisguise(powered);
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof CreeperDisguise && ((CreeperDisguise)object).powered == powered;
	}
	
	public EntityCreeper getEntity(World world, Location location, int id) {
		EntityCreeper creeper = (EntityCreeper)super.getEntity(world, location, id);
		creeper.setPowered(powered);
		return creeper;
	}
	
}