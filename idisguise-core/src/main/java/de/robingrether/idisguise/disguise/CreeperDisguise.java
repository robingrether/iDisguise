package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise as a creeper.
 * 
 * @since 4.0.1
 * @author RobinGrether
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
		super(DisguiseType.CREEPER);
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
	public String toString() {
		return super.toString() + "; " + (powered ? "powered" : "not-powered");
	}
	
	static {
		Subtypes.registerSubtype(CreeperDisguise.class, "setPowered", true, "powered");
		Subtypes.registerSubtype(CreeperDisguise.class, "setPowered", false, "not-powered");
	}
	
}