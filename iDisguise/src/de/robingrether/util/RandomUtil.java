package de.robingrether.util;

import java.util.Random;

/**
 * This utility class provides the some methods from {@link java.util.Random}, but they are static here.
 * 
 * @author RobinGrether
 */
public class RandomUtil {
	
	/**
	 * This is the {@link java.util.Random} object that is used to create random numbers.<br>
	 * You can modify this variable.
	 */
	public static Random random = new Random();
	
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}
	
	public static int nextInt() {
		return random.nextInt();
	}
	
	public static int nextInt(int n) {
		return random.nextInt(n);
	}
	
	/**
	 * Gets a random enum constant from the given enum type.
	 * 
	 * @param <T> the enum type
	 * @param enumType the enum type
	 * @return a random enum constant
	 */
	public static <T extends Enum<T>> T nextEnumValue(Class<T> enumType) {
		return enumType.getEnumConstants()[nextInt(enumType.getEnumConstants().length)];
	}
	
}