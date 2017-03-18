package de.robingrether.idisguise.disguise;

import java.io.Serializable;

import de.robingrether.util.StringUtil;

/**
 * Represents a disguise.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public abstract class Disguise implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 3699593353745149494L;
	protected final DisguiseType type;
	
	protected Disguise(DisguiseType type) {
		if(!type.isAvailable()) {
			throw new OutdatedServerException();
		}
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
	public final Disguise clone() {
		return fromString(toString());
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if this object is the same as the <code>object</code> argument; <code>false</code> otherwise
	 */
	public final boolean equals(Object object) {
		return object != null && object.getClass().equals(getClass()) && toString().equals(object.toString());
	}
	
	public final int hashCode() {
		return toString().hashCode();
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @since 5.3.1
	 * @return a string representation of the object
	 */
	public String toString() {
		return type.toString();
	}
	
	/**
	 * Recreates a disguise from its string representation.
	 * 
	 * @since 5.5.1
	 * @param string the exact string representation of the disguise
	 * @throws IllegalArgumentException in case the given string cannot be evaluated to a valid disguise
	 * @throws OutdatedServerException in case the Minecraft server does not support the disguise type
	 */
	public static Disguise fromString(String string) throws IllegalArgumentException, OutdatedServerException {
		String[] args = string.split("; ");
		DisguiseType type = DisguiseType.Matcher.match(args[0]);
		if(type == null) {
			if(StringUtil.equals(args[0], "player", "ghost") && args.length == 3) {
				return new PlayerDisguise(args[1], args[2], args[0].equals("ghost"));
			}
		} else {
			Disguise disguise = type.newInstance();
			for(int i = 1; i < args.length; i++) {
				Subtypes.applySubtype(disguise, args[i]);
			}
			return disguise;
		}
		throw new IllegalArgumentException();
	}
	
}