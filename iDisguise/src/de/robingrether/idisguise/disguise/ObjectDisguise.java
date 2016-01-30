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
	 * {@inheritDoc}
	 */
	public ObjectDisguise clone() {
		return new ObjectDisguise(type);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ObjectDisguise;
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
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		return false;
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
	
}