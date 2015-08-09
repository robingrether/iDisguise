package de.robingrether.util;

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
	
}