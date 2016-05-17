package de.robingrether.util;

import java.util.Locale;

/**
 * Some utility methods for strings.
 * 
 * @author RobinGrether
 */
public class StringUtil {
	
	/**
	 * Tests whether a string contains any of the others.
	 * 
	 * @param string the string
	 * @param strings the other strings
	 * @return <code>true</code> if the string contains any of the others
	 */
	public static boolean contains(String string, String... strings) {
		for(String s : strings) {
			if(string.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tests whether a string equals any of the others.
	 * 
	 * @param string the string
	 * @param strings the other strings
	 * @return <code>true</code> if the string equals any of the others
	 */
	public static boolean equals(String string, String... strings) {
		for(String s : strings) {
			if(string.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tests whether a string equals any of the others, ignoring case considerations.
	 * 
	 * @param string the string
	 * @param strings the other strings
	 * @return <code>true</code> if the string equals any of the others, ignoring case considerations
	 */
	public static boolean equalsIgnoreCase(String string, String... strings) {
		for(String s : strings) {
			if(string.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Joins an array of strings with a specified seperator.
	 * 
	 * @param seperator the seperator
	 * @param strings the strings
	 * @return the joined strings
	 */
	public static String join(char seperator, String... strings) {
		return join(String.valueOf(seperator), strings);
	}
	
	/**
	 * Joins an array of strings with a specified seperator.
	 * 
	 * @param seperator the seperator
	 * @param strings the strings
	 * @return the joined strings
	 */
	public static String join(String seperator, String... strings) {
		StringBuilder builder = new StringBuilder(strings[0]);
		for(int i = 1; i < strings.length; i++) {
			builder.append(seperator);
			builder.append(strings[i]);
		}
		return builder.toString();
	}
	
	/**
	 * Capitalizes the first letter of every word of the given string.<br>
	 * e.g. "This is a STRING!" -> "This Is A STRING!"
	 * 
	 * @param string string to capitalize
	 * @return the capitalized string
	 */
	public static String capitalize(String string) {
		String[] words = string.split(" ");
		for(int i = 0; i < words.length; i++) {
			if(words[i].length() > 0) {
				words[i] = Character.toString(words[i].charAt(0)).toUpperCase(Locale.ENGLISH) + words[i].substring(1);
			}
		}
		return join(' ', words);
	}
	
	/**
	 * Capitalizes the first letter of every word of the given string and decapitalizes all other letters.<br>
	 * e.g. "This is a STRING!" -> "This Is A String!"
	 * 
	 * @param string string to capitalize
	 * @return the capitalized string
	 */
	public static String capitalizeFully(String string) {
		String[] words = string.split(" ");
		for(int i = 0; i < words.length; i++) {
			if(words[i].length() > 0) {
				words[i] = Character.toString(words[i].charAt(0)).toUpperCase(Locale.ENGLISH) + words[i].substring(1).toLowerCase(Locale.ENGLISH);
			}
		}
		return join(' ', words);
	}
	
}