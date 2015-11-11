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
	
	public ObjectDisguise(DisguiseType type) {
		super(type);
		if(!type.isObject()) {
			throw new IllegalArgumentException("DisguiseType must be an object");
		}
		typeId = getTypeId(type);
	}
	
	public ObjectDisguise clone() {
		return new ObjectDisguise(type);
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ObjectDisguise;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
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