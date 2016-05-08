package de.robingrether.util;

import java.util.regex.Pattern;

/**
 * Some utility methods to validate objects.
 * 
 * @author RobinGrether
 */
public class Validate {
	
	public static void notNull(Object object, String message) {
		if(object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNull(Object object) {
		if(object == null) {
			throw new IllegalArgumentException("The object must not null");
		}
	}
	
	/**
	 * Indicates whether a given minecraft username is valid. <br>
	 * Minecraft usernames may not be longer than 16 characters and they may not contain any characters except letters, numbers and underscores.
	 * 
	 * @param username the username to be validated
	 * @return <code>true</code> if the given username is valid, <code>false</code> otherwise
	 */
	public static boolean minecraftUsername(String username) {
		return Pattern.matches("[A-Za-z0-9_]{1,16}", username);
	}
	
}