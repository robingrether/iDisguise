package de.robingrether.idisguise.disguise;

import org.bukkit.Location;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;

/**
 * Represents a disguise as a sized mob (e.g. slime, magma slime).
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class SizedDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 6370692880641733105L;
	private int size;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the disguise type (should be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
	 */
	public SizedDisguise(DisguiseType type) {
		this(type, 2);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param type the disguise type (should be either {@link DisguiseType#SLIME} or {@link DisguiseType#MAGMA_CUBE})
	 * @param size the size (must not be negative)
	 */
	public SizedDisguise(DisguiseType type, int size) {
		super(type, true);
		this.size = size;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @since 3.0.1
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Sets the size.
	 * 
	 * @since 3.0.1
	 * @param size the size (must not be negative)
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SizedDisguise clone() {
		SizedDisguise clone = new SizedDisguise(type, size);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof SizedDisguise && ((SizedDisguise)object).size == size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityInsentient getEntity(World world, Location location, int id) {
		EntityInsentient entity = super.getEntity(world, location, id);
		if(entity instanceof EntitySlime) {
			((EntitySlime)entity).setSize(size);
		}
		return entity;
	}
	
}