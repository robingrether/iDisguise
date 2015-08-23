package de.robingrether.idisguise.disguise;

import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;

/**
 * Represents a disguise as a creeper.
 * 
 * @since 4.0.1
 * @author Robingrether
 */
public class CreeperDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -5787233589068911050L;
	private boolean powered;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public CreeperDisguise() {
		this(false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param powered whether the creeper should be powered
	 */
	public CreeperDisguise(boolean powered) {
		super(DisguiseType.CREEPER, true);
		this.powered = powered;
	}
	
	/**
	 * Indicates whether the creeper is powered.
	 * 
	 * @since 4.0.1
	 * @return <code>true</code>, if the creeper is powered
	 */
	public boolean isPowered() {
		return powered;
	}
	
	/**
	 * Sets whether the creeper is powered.
	 * 
	 * @since 4.0.1
	 * @param powered <code>true</code>, if the creeper should be powered
	 */
	public void setPowered(boolean powered) {
		this.powered = powered;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CreeperDisguise clone() {
		CreeperDisguise clone = new CreeperDisguise(powered);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof CreeperDisguise && ((CreeperDisguise)object).powered == powered;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityCreeper getEntity(World world, Location location, int id) {
		EntityCreeper creeper = (EntityCreeper)super.getEntity(world, location, id);
		creeper.setPowered(powered);
		return creeper;
	}
	
}