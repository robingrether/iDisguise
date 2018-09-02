package de.robingrether.idisguise.disguise;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.robingrether.util.StringUtil;

/**
 * This class provides the ability to apply subtypes to a disguise based on command arguments.
 * 
 * @since 5.3.1
 * @author RobinGrether
 */
public class Subtypes {
	
	private static Map<String, Consumer<Disguise>> registrySimpleSubtypes = new HashMap<String, Consumer<Disguise>>();
	private static Map<String, BiConsumer<Disguise, String>> registryParameterizedSubtypes = new HashMap<String, BiConsumer<Disguise, String>>();
	private static Map<String, Set<String>> registryParameterizedSubtypesSuggestions = new HashMap<String, Set<String>>();
	
	/**
	 * Registers a simple subtype for a given disguise class.
	 * 
	 * @since 5.8.1
	 */
	public static <T extends Disguise> void registerSimpleSubtype(Class<T> disguiseClass, Consumer<T> consumer, String argument) {
		argument = argument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		registrySimpleSubtypes.put(disguiseClass.getSimpleName() + ":" + argument, disguise -> consumer.accept((T)disguise));
	}
	
	/**
	 * Registers a parameterized subtype for a given disguise class.
	 * 
	 * @since 5.8.1
	 */
	public static <T extends Disguise> void registerParameterizedSubtype(Class<T> disguiseClass, BiConsumer<T, String> biConsumer, String argument) {
		argument = argument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		registryParameterizedSubtypes.put(disguiseClass.getSimpleName() + ":" + argument, (disguise, parameter) -> biConsumer.accept((T)disguise, parameter));
	}
	
	/**
	 * Registers a parameterized subtype for a given disguise class.
	 * 
	 * @since 5.8.1
	 */
	public static <T extends Disguise> void registerParameterizedSubtype(Class<T> disguiseClass, BiConsumer<T, String> biConsumer, String argument, Set<String> parameterSuggestions) {
		argument = argument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		registryParameterizedSubtypes.put(disguiseClass.getSimpleName() + ":" + argument, (disguise, parameter) -> biConsumer.accept((T)disguise, parameter));
		registryParameterizedSubtypesSuggestions.put(disguiseClass.getSimpleName() + ":" + argument, Collections.unmodifiableSet(parameterSuggestions));
	}
	
	/**
	 * Applies a subtype to a given disguise based on the given argument.
	 * 
	 * @since 5.8.1
	 * @param disguise the disguise
	 * @param argument the argument to match
	 * @return <code>true</code>, if and only if a matching subtype has been found <strong>AND</strong> successfully applied; <code>false</code>, if no matching subtype has been found; an informative message in case a matching subtype has been found but could not be applied successfully
	 */
	public static Object applySubtype(Disguise disguise, String argument) {
		if(argument.contains(";")) return false;
		
		if(argument.contains("=")) {
			String[] s = argument.split("=", 2);
			s[0] = s[0].toLowerCase(Locale.ENGLISH).replace('_', '-');
			Class<?> clazz = disguise.getClass();
			BiConsumer<Disguise, String> biConsumer;
			
			while((biConsumer = registryParameterizedSubtypes.get(clazz.getSimpleName() + ":" + s[0])) == null) {
				clazz = clazz.getSuperclass();
				if(!Disguise.class.isAssignableFrom(clazz)) return false;
			}
			
			try {
				biConsumer.accept(disguise, s[1]);
				return true;
			} catch(NumberFormatException e) {
				for(StackTraceElement element : e.getStackTrace()) {
					if(StringUtil.contains(element.toString(), "Float", "Double")) {
						return "Invalid number format: expected a floating point number";
					} else if(StringUtil.contains(element.toString(), "Integer", "Short")) {
						return "Invalid number format: expected an integer";
					}
				}
				return "Invalid number format";
			} catch(IllegalArgumentException e) {
				return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
			}
		} else {
			argument = argument.toLowerCase(Locale.ENGLISH).replace('_', '-');
			Class<?> clazz = disguise.getClass();
			Consumer<Disguise> consumer;
			
			while((consumer = registrySimpleSubtypes.get(clazz.getSimpleName() + ":" + argument)) == null) {
				clazz = clazz.getSuperclass();
				if(!Disguise.class.isAssignableFrom(clazz)) return false;
			}
			
			try {
				consumer.accept(disguise);
				return true;
			} catch(NumberFormatException e) {
				for(StackTraceElement element : e.getStackTrace()) {
					if(StringUtil.contains(element.toString(), "Float", "Double")) {
						return "Invalid number format: expected a floating point number";
					} else if(StringUtil.contains(element.toString(), "Integer", "Short")) {
						return "Invalid number format: expected an integer";
					}
				}
				return "Invalid number format";
			} catch(IllegalArgumentException e) {
				return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
			}
		}
	}
	
	/**
	 * Returns a set containing all registered subtype arguments for the given disguise.
	 * 
	 * @since 5.7.1
	 */
	public static Set<String> listSubtypeArguments(Disguise disguise, boolean includeParameterSuggestions) {
		Set<String> classes = new HashSet<String>();
		Class<?> clazz = disguise.getClass();
		
		while(Disguise.class.isAssignableFrom(clazz)) {
			classes.add(clazz.getSimpleName() + ":");
			clazz = clazz.getSuperclass();
		}
		
		final String[] classes2 = classes.toArray(new String[0]);
		Set<String> subtypeArguments = new HashSet<String>();
		
		for(String simpleSubtype : registrySimpleSubtypes.keySet()) {
			if(StringUtil.startsWith(simpleSubtype, classes2)) {
				subtypeArguments.add(simpleSubtype.substring(simpleSubtype.indexOf(":") + 1));
			}
		}
		
		for(String parameterizedSubtype : registryParameterizedSubtypes.keySet()) {
			if(StringUtil.startsWith(parameterizedSubtype, classes2)) {
				String subtypeName = parameterizedSubtype.substring(parameterizedSubtype.indexOf(":") + 1) + "=";
				subtypeArguments.add(subtypeName);
				if(includeParameterSuggestions && registryParameterizedSubtypesSuggestions.containsKey(parameterizedSubtype)) {
					for(String parameterSuggestion : registryParameterizedSubtypesSuggestions.get(parameterizedSubtype)) {
						subtypeArguments.add(subtypeName + parameterSuggestion);
					}
				}
			}
		}
		
		return subtypeArguments;
	}
	
}