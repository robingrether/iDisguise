package de.robingrether.idisguise.disguise;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;

/**
 * This class provides the ability to apply subtypes to a disguise based on command arguments.
 * 
 * @since 5.3.1
 * @author RobinGrether
 */
public class Subtypes {
	
	private static Map<Class<? extends Disguise>, Map<String, Subtype>> registeredClasses = new ConcurrentHashMap<Class<? extends Disguise>, Map<String, Subtype>>();
	private static Map<Class<? extends Disguise>, Map<String, ParameterizedSubtype>> registeredClasses2 = new ConcurrentHashMap<Class<? extends Disguise>, Map<String, ParameterizedSubtype>>();
	
	/**
	 * Registers a new subtype.
	 * 
	 * @since 5.3.1
	 * @param disguiseClass the disguise class
	 * @param methodName the method to call
	 * @param parameter the parameter to pass to the method
	 * @param argument the command argument to bind this to
	 */
	public static void registerSubtype(Class<? extends Disguise> disguiseClass, String methodName, Object parameter, String argument) {
		if(!registeredClasses.containsKey(disguiseClass)) {
			registeredClasses.put(disguiseClass, new LinkedHashMap<String, Subtype>());
		}
		Map<String, Subtype> registeredSubtypes = registeredClasses.get(disguiseClass);
		try {
			registeredSubtypes.put(argument, new Subtype(disguiseClass, methodName, parameter));
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot register the given subtype: " + disguiseClass.getSimpleName() + "/" + argument, e);
			}
		}
	}
	
	/**
	 * Registers a new subtype.
	 * 
	 * @since 5.3.1
	 * @param disguiseClass the disguise class
	 * @param methodName the method to call
	 * @param parameter the parameter to pass to the method
	 * @param argument the command argument to bind this to
	 */
	public static void registerSubtype(Class<? extends Disguise> disguiseClass, String methodName, boolean parameter, String argument) {
		if(!registeredClasses.containsKey(disguiseClass)) {
			registeredClasses.put(disguiseClass, new LinkedHashMap<String, Subtype>());
		}
		Map<String, Subtype> registeredSubtypes = registeredClasses.get(disguiseClass);
		try {
			registeredSubtypes.put(argument, new Subtype(disguiseClass, methodName, parameter));
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot register the given subtype: " + disguiseClass.getSimpleName() + "/" + argument, e);
			}
		}
	}
	
	/**
	 * Registers a new subtype.
	 * 
	 * @since 5.3.1
	 * @param disguiseClass the disguise class
	 * @param methodName the method to call
	 * @param parameter the parameter to pass to the method
	 * @param argument the command argument to bind this to
	 */
	public static void registerSubtype(Class<? extends Disguise> disguiseClass, String methodName, int parameter, String argument) {
		if(!registeredClasses.containsKey(disguiseClass)) {
			registeredClasses.put(disguiseClass, new LinkedHashMap<String, Subtype>());
		}
		Map<String, Subtype> registeredSubtypes = registeredClasses.get(disguiseClass);
		try {
			registeredSubtypes.put(argument, new Subtype(disguiseClass, methodName, parameter));
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot register the given subtype: " + disguiseClass.getSimpleName() + "/" + argument, e);
			}
		}
	}
	
	/**
	 * Registers a new parameterized subtype.
	 * 
	 * @since 5.6.1
	 * @param disguiseClass the disguise class
	 * @param methodName the method to call
	 * @param argument the command argument to bind this to
	 * @param parameterType the parameter type to pass to the method (<code>int.class</code>, <code>float.class</code>, <code>String.class</code>, <code>String[].class</code> and enum classes are supported)
	 */
	public static void registerParameterizedSubtype(Class<? extends Disguise> disguiseClass, String methodName, String argument, Class<?> parameterType) {
		if(!registeredClasses2.containsKey(disguiseClass)) {
			registeredClasses2.put(disguiseClass, new LinkedHashMap<String, ParameterizedSubtype>());
		}
		Map<String, ParameterizedSubtype> registeredSubtypes = registeredClasses2.get(disguiseClass);
		try {
			registeredSubtypes.put(argument, new ParameterizedSubtype(disguiseClass, methodName, parameterType));
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot register the given subtype: " + disguiseClass.getSimpleName() + "/" + argument, e);
			}
		}
	}
	
	/**
	 * Registers a new parameterized subtype.
	 * 
	 * @since 5.7.1
	 * @see Subtypes#registerParameterizedSubtype(Class, String, String, Class)
	 */
	public static void registerParameterizedSubtype(Class<? extends Disguise> disguiseClass, String methodName, String argument, Class<?> parameterType, Set<String> parameterSuggestions) {
		if(!registeredClasses2.containsKey(disguiseClass)) {
			registeredClasses2.put(disguiseClass, new LinkedHashMap<String, ParameterizedSubtype>());
		}
		Map<String, ParameterizedSubtype> registeredSubtypes = registeredClasses2.get(disguiseClass);
		try {
			registeredSubtypes.put(argument, new ParameterizedSubtype(disguiseClass, methodName, parameterType, parameterSuggestions));
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot register the given subtype: " + disguiseClass.getSimpleName() + "/" + argument, e);
			}
		}
	}
	
	/**
	 * Applies a subtype to a given disguise based on the given argument.
	 * 
	 * @since 5.3.1
	 * @param disguise the disguise
	 * @param argument the argument to match
	 * @return <code>true</code>, if and only if a matching subtype has been found <strong>AND</strong> successfully applied
	 */
	public static boolean applySubtype(Disguise disguise, String argument) {
		if(argument.contains(";")) return false;
		Class<?> clazz = disguise.getClass();
		Set<Class<? extends Disguise>> classes = new HashSet<Class<? extends Disguise>>();
		while(clazz != Object.class) {
			classes.add((Class<? extends Disguise>)clazz);
			clazz = clazz.getSuperclass();
		}
		if(argument.contains("=")) {
			String parameter = ChatColor.translateAlternateColorCodes('&', argument.substring(argument.indexOf("=") + 1).replace("\\s", " "));
			argument = argument.substring(0, argument.indexOf("="));
			for(Class<? extends Disguise> disguiseClass : classes) {
				Map<String, ParameterizedSubtype> registeredSubtypes = registeredClasses2.get(disguiseClass);
				if(registeredSubtypes != null) {
					ParameterizedSubtype subtype = registeredSubtypes.get(argument.toLowerCase(Locale.ENGLISH).replace('_', '-'));
					if(subtype != null) {
						try {
							subtype.apply(disguise, parameter);
							return true;
						} catch(Exception e) {
							return false;
						}
					}
				}
			}
		} else {
			for(Class<? extends Disguise> disguiseClass : classes) {
				Map<String, Subtype> registeredSubtypes = registeredClasses.get(disguiseClass);
				if(registeredSubtypes != null) {
					Subtype subtype = registeredSubtypes.get(argument.toLowerCase(Locale.ENGLISH).replace('_', '-'));
					if(subtype != null) {
						try {
							subtype.apply(disguise);
							return true;
						} catch(Exception e) {
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a set containing all registered subtype arguments for the given disguise.
	 * 
	 * @since 5.7.1
	 */
	public static Set<String> listSubtypeArguments(Disguise disguise, boolean includeParameterSuggestions) {
		Class<?> clazz = disguise.getClass();
		Stack<Class<? extends Disguise>> classes = new Stack<Class<? extends Disguise>>();
		while(clazz != Object.class) {
			classes.add((Class<? extends Disguise>)clazz);
			clazz = clazz.getSuperclass();
		}
		Set<String> subtypeArguments = new HashSet<String>();
		while(!classes.isEmpty()) {
			Class<? extends Disguise> disguiseClass = classes.pop();
			if(registeredClasses.containsKey(disguiseClass)) {
				subtypeArguments.addAll(registeredClasses.get(disguiseClass).keySet());
			}
			if(registeredClasses2.containsKey(disguiseClass)) {
				for(String subtypeArgument : registeredClasses2.get(disguiseClass).keySet()) {
					subtypeArguments.add(subtypeArgument + "=");
					if(includeParameterSuggestions) {
						for(String parameterSuggestion : registeredClasses2.get(disguiseClass).get(subtypeArgument).getParameterSuggestions()) {
							subtypeArguments.add(subtypeArgument + "=" + parameterSuggestion);
						}
					}
				}
			}
		}
		return subtypeArguments;
	}
	
	private static class Subtype {
		
		private Method method;
		private Object parameter;
		
		private Subtype(Class<? extends Disguise> disguiseClass, String methodName, Object parameter) throws NoSuchMethodException {
			this.method = disguiseClass.getDeclaredMethod(methodName, parameter.getClass());
			this.parameter = parameter;
		}
		
		private Subtype(Class<? extends Disguise> disguiseClass, String methodName, boolean parameter) throws NoSuchMethodException {
			this.method = disguiseClass.getDeclaredMethod(methodName, boolean.class);
			this.parameter = parameter;
		}
		
		private Subtype(Class<? extends Disguise> disguiseClass, String methodName, int parameter) throws NoSuchMethodException {
			this.method = disguiseClass.getDeclaredMethod(methodName, int.class);
			this.parameter = parameter;
		}
		
		private void apply(Disguise disguise) throws InvocationTargetException, IllegalAccessException {
			method.invoke(disguise, parameter);
		}
		
	}
	
	private static class ParameterizedSubtype {
		
		private Method method;
		private Class<?> parameterType;
		private Set<String> parameterSuggestions;
		
		private ParameterizedSubtype(Class<? extends Disguise> disguiseClass, String methodName, Class<?> parameterType) throws NoSuchMethodException {
			this.method = disguiseClass.getDeclaredMethod(methodName, parameterType);
			this.parameterType = parameterType;
			this.parameterSuggestions = Collections.emptySet();
		}
		
		private ParameterizedSubtype(Class<? extends Disguise> disguiseClass, String methodName, Class<?> parameterType, Set<String> parameterSuggestions) throws NoSuchMethodException {
			this.method = disguiseClass.getDeclaredMethod(methodName, parameterType);
			this.parameterType = parameterType;
			this.parameterSuggestions = parameterSuggestions;
		}
		
		private void apply(Disguise disguise, String parameter) throws InvocationTargetException, IllegalAccessException {
			if(parameterType == int.class) {
				method.invoke(disguise, Integer.parseInt(parameter));
			} else if(parameterType == float.class) {
				method.invoke(disguise, Float.parseFloat(parameter));
			} else if(parameterType == String.class) {
				method.invoke(disguise, parameter);
			} else if(parameterType == String[].class) {
				method.invoke(disguise, (Object)parameter.split(","));
			} else if(Enum.class.isAssignableFrom(parameterType)) {
				method.invoke(disguise, Enum.valueOf((Class<? extends Enum>)parameterType, parameter.toUpperCase(Locale.ENGLISH).replace('-', '_')));
			}
		}
		
		private Set<String> getParameterSuggestions() {
			return parameterSuggestions;
		}
		
	}
	
}