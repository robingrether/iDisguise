package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise as an object.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class ObjectDisguise extends Disguise {
	
	private static final long serialVersionUID = 7999903708957650848L;
	private final int typeId;
	protected String customName = "";
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @throws IllegalArgumentException if the given {@linkplain DisguiseType} is not an object
	 * @param type the disguise type
	 */
	public ObjectDisguise(DisguiseType type) {
		super(type);
		if(!type.isObject()) {
			throw new IllegalArgumentException("DisguiseType must be an object");
		}
		typeId = getTypeId(type);
	}
	
	/**
	 * Gets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 5.6.1
	 * @return the custom name
	 */
	public String getCustomName() {
		return customName;
	}
	
	/**
	 * Sets the custom name of this entity.<br>
	 * The default value is <code>""</code>.
	 * 
	 * @since 5.6.1
	 * @param customName the custom name
	 */
	public void setCustomName(String customName) {
		if(customName == null) {
			customName = "";
		} else if(customName.length() > 64) {
			customName = customName.substring(0, 64);
		}
		this.customName = customName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ObjectDisguise clone() {
		ObjectDisguise clone = new ObjectDisguise(type);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && ((ObjectDisguise)object).customName.equals(customName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; custom-name=" + customName;
	}
	
	/**
	 * Gets the mob type id.<br>
	 * This id is used in the client/server communication.
	 * 
	 * @since 5.1.1
	 * @return the type id for this disguise
	 */
	public int getTypeId() {
		return typeId;
	}
	
	/**
	 * Gets the mob type id for a given {@linkplain DisguiseType}.<br>
	 * This id is used in the client/server communication.
	 * 
	 * @since 5.1.1
	 * @throws IllegalArgumentException if the given {@linkplain DisguiseType} is not an object
	 * @param type the disguise type
	 * @return the type id for the given disguise type
	 */
	public static int getTypeId(DisguiseType type) {
		if(!type.isObject()) {
			throw new IllegalArgumentException("DisguiseType must be an object");
		}
		switch(type) {
			case ARMOR_STAND:
				return 78;
			case BOAT:
				return 1;
			case ENDER_CRYSTAL:
				return 51;
			case FALLING_BLOCK:
				return 70;
			case ITEM:
				return 2;
			case MINECART:
				return 10;
			default:
				return 0;
		}
	}
	
	static {
		Subtypes.registerParameterizedSubtype(ObjectDisguise.class, "setCustomName", "custom-name", String.class);
	}
	
}