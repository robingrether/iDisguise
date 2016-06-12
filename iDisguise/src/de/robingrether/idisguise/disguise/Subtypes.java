package de.robingrether.idisguise.disguise;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

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
	 * Applies a subtype to a given disguise based on the given argument.
	 * 
	 * @since 5.3.1
	 * @param disguise the disguise
	 * @param argument the argument to match
	 * @return <code>true</code>, if and only if a matching subtype has been found <strong>AND</strong> successfully applied
	 */
	public static boolean applySubtype(Disguise disguise, String argument) {
		Class<?> clazz = disguise.getClass();
		List<Class<? extends Disguise>> classes = new ArrayList<Class<? extends Disguise>>();
		while(clazz != Disguise.class) {
			classes.add((Class<? extends Disguise>)clazz);
			clazz = clazz.getSuperclass();
		}
		for(Class<? extends Disguise> disguiseClass : classes) {
			Map<String, Subtype> registeredSubtypes = registeredClasses.get(disguiseClass);
			if(registeredSubtypes != null) {
				Subtype subtype = registeredSubtypes.get(argument.toLowerCase(Locale.ENGLISH).replace('_', '-'));
				if(subtype != null) {
					try {
						subtype.apply(disguise);
						return true;
					} catch(Exception e) {
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a list containing all registered subtype arguments for the given disguise.
	 * 
	 * @since 5.3.1
	 * @param disguise the disguise
	 * @return a list containing all registered subtype arguments for the given disguise
	 */
	public static List<String> listSubtypeArguments(Disguise disguise) {
		Class<?> clazz = disguise.getClass();
		Stack<Class<? extends Disguise>> classes = new Stack<Class<? extends Disguise>>();
		while(clazz != Disguise.class) {
			classes.add((Class<? extends Disguise>)clazz);
			clazz = clazz.getSuperclass();
		}
		List<String> subtypeArguments = new ArrayList<String>();
		while(!classes.isEmpty()) {
			Map<String, Subtype> registeredSubtypes = registeredClasses.get(classes.pop());
			if(registeredSubtypes != null) {
				subtypeArguments.addAll(registeredSubtypes.keySet());
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
	
}