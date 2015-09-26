package de.robingrether.util;

/**
 * Some utility methods for objects.
 * 
 * @author RobinGrether
 */
public class ObjectUtil {
	
	/**
	 * Tests whether an object equals any of the others.
	 * 
	 * @param object the object
	 * @param objects the other objects
	 * @return <code>true</code> if the object equals any of the others
	 */
	public static boolean equals(Object object, Object... objects) {
		for(Object obj : objects) {
			if(object.equals(obj)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tests whether an object is an instance of any of the given classes.
	 * 
	 * @param object the object to test
	 * @param classes the classes to test
	 * @return <code>true</code> if the object is an instance of at least one of the given classes
	 */
	public static boolean instanceOf(Object object, Class<?>... classes) {
		for(Class<?> clazz : classes) {
			if(clazz.isInstance(object)) {
				return true;
			}
		}
		return false;
	}
	
}