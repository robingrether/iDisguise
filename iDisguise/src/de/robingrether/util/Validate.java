package de.robingrether.util;

import java.util.regex.Pattern;

/**
 * Some utility methods to validate objects.
 * 
 * @author Robingrether
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
	
	public static boolean minecraftUsername(String username) {
		return Pattern.matches("[A-Za-z0-9_]{1,16}", username);
	}
	
}