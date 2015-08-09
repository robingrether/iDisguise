package de.robingrether.idisguise.disguise;

import java.io.Serializable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;

/**
 * Represents a disguise.
 * 
 * @since 2.1.3
 * @author Robingrether
 */
public abstract class Disguise implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 3699593353745149494L;
	protected DisguiseType type;
	
	protected Disguise(DisguiseType type) {
		this.type = type;
	}
	
	/**
	 * Returns the disguise type
	 * 
	 * @since 2.1.3
	 * @return the disguise type
	 */
	public DisguiseType getType() {
		return this.type;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @since 3.0.1
	 * @return a clone of this instance
	 */
	public abstract Disguise clone();
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if this object is the same as the <code>object</code> argument; <code>false</code> otherwise
	 */
	public boolean equals(Object object) {
		return object instanceof Disguise && ((Disguise)object).getType().equals(type);
	}
	
	/**
	 * Gets the entity object the player turns into.<br />
	 * This is just for internal handling.
	 * 
	 * @since 3.0.1
	 * @param world the player's world
	 * @param location the player's location
	 * @param id the player's entity id
	 * @return the entity object the player turns into
	 */
	public abstract Entity getEntity(World world, Location location, int id);
	
}