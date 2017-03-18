package de.robingrether.idisguise.disguise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.entity.Player;
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
	private Visibility visibility = Visibility.EVERYONE;
	private final List<String> visibilityParameter = new ArrayList<String>();
	
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
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
		visibilityParameter.clear();
	}
	
	public String[] getVisibilityParameter() {
		return visibilityParameter.toArray(new String[0]);
	}
	
	public void setVisibilityParameter(String... visibilityParameter) {
		this.visibilityParameter.clear();
		for(String parameter : visibilityParameter) {
			this.visibilityParameter.add(parameter.toLowerCase(Locale.ENGLISH));
		}
	}
	
	public boolean isVisibleTo(Player player) {
		switch(visibility) {
			case EVERYONE:
				return true;
			case ONLY_LIST:
				return visibilityParameter.contains(player.getName().toLowerCase(Locale.ENGLISH));
			case NOT_LIST:
				return !visibilityParameter.contains(player.getName().toLowerCase(Locale.ENGLISH));
			case ONLY_PERMISSION:
				return player.hasPermission(visibilityParameter.get(0));
			case NOT_PERMISSION:
				return !player.hasPermission(visibilityParameter.get(0));
			default:
				return false;
		}
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
		return type.toString() + "; visibility=" + visibility.name().toLowerCase(Locale.ENGLISH) + "; visibility-param=" + String.join(",", visibilityParameter.toArray(new String[0]));
	}
	
	static {
		Subtypes.registerParameterizedSubtype(Disguise.class, "setVisibility", "visibility", Visibility.class);
		Subtypes.registerParameterizedSubtype(Disguise.class, "setVisibilityParameter", "visibility-param", String[].class);
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
			if(StringUtil.equals(args[0], "player", "ghost") && args.length == 5) {
				Disguise disguise = new PlayerDisguise(args[3], args[4], args[0].equals("ghost"));
				Subtypes.applySubtype(disguise, args[1]);
				Subtypes.applySubtype(disguise, args[2]);
				return disguise;
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
	
	public enum Visibility {
		
		EVERYONE,
		ONLY_LIST,
		NOT_LIST,
		ONLY_PERMISSION,
		NOT_PERMISSION;
		
	}
	
}