package de.robingrether.idisguise.disguise;

import org.bukkit.Location;
import de.robingrether.idisguise.management.ProfileUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;

/**
 * Represents a disguise as a player.
 * 
 * @since 2.1.3
 * @author Robingrether
 */
public class PlayerDisguise extends Disguise {
	
	private static final long serialVersionUID = 6444092928664929232L;
	private final String name;
	
	/**
	 * Creates an instance which can be used in the API.
	 * 
	 * @since 2.1.3
	 * @param name the player's name to disguise as
	 * @deprecated new constructor
	 */
	@Deprecated
	public PlayerDisguise(String name) {
		this(name, false);
	}
	
	/**
	 * Creates a new instance that can be used in the API.
	 * 
	 * @since 2.3.1
	 * @param name the player's name to disguise as
	 * @param ghost whether the disguise should be a ghost or not
	 */
	public PlayerDisguise(String name, boolean ghost) {
		super(ghost ? DisguiseType.GHOST : DisguiseType.PLAYER);
		this.name = ProfileUtil.getCaseCorrectedName(name);
	}
	
	/**
	 * Returns the name.
	 * 
	 * @since 2.1.3
	 * @return the player's name to disguise as
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks whether this disguise is a ghost.
	 * 
	 * @since 2.3.1
	 * @return <code>true</code> if this is a ghost
	 */
	public boolean isGhost() {
		return type == DisguiseType.GHOST;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public PlayerDisguise clone() {
		return new PlayerDisguise(new String(name), isGhost());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof PlayerDisguise && ((PlayerDisguise)object).name.equalsIgnoreCase(name);
	}
	
	/**
	 * {@inheritDoc}
	 * <br />
	 * This method is never used and always returns <code>null</code>.
	 * 
	 * @return <code>null</code>
	 */
	public Entity getEntity(World world, Location location, int id) {
		return null;
	}
	
}