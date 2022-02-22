package de.robingrether.idisguise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public class Reflection {
	
	public static String OBC = Bukkit.getServer().getClass().getPackageName();
	public static String NMS = "net.minecraft.server." + Bukkit.getServer().getClass().getPackageName().split("\\.")[3];
	
	/* org.bukkit.craftbukkit stuff */
	public static Class<?> CraftLivingEntity;
	public static Method CraftLivingEntity_getHandle;
	
	public static Class<?> CraftPlayer;
	public static Method CraftPlayer_getHandle;
	
	/* net.minecraft.server stuff */
	public static Class<?> Entity;
	public static Field Entity_world;
	
	public static Class<?> EntityTracker;
	public static Field EntityTracker_trackedEntities;
	
	public static Class<?> EntityTrackerEntry;
	public static Method EntityTrackerEntry_clear;
	
	public static Class<?> IntHashMap;
	public static Method IntHashMap_get;
	
	public static Class<?> WorldServer;
	public static Field WorldServer_entityTracker;
	
	private static final Pattern basicPattern = Pattern.compile("([A-Za-z0-9_]+)->(C|F|M|N)(.+)");
	private static final Pattern fieldPattern = Pattern.compile("([A-Za-z0-9_\\.{}]+)\\$(.+)");
	private static final Pattern methodPattern = Pattern.compile("([A-Za-z0-9_\\.{}]+)\\$([^\\(\\)]+)\\(([^\\(\\)]*)\\)");
	private static final Pattern newPattern = Pattern.compile("([A-Za-z0-9_\\.{}]+)\\(([^\\(\\)]*)\\)");
	
	public static void load(String file, String nms, String obc) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Reflection.class.getResourceAsStream(file)));
			String line;
			while((line = reader.readLine()) != null) {
				Matcher basicMatcher = basicPattern.matcher(line);
				if(basicMatcher.matches()) {
					try {
						Field field = Reflection.class.getDeclaredField(basicMatcher.group(1));
						char type = basicMatcher.group(2).charAt(0);
						String argument = basicMatcher.group(3);
						if(type == 'C') {
							Class<?> clazz = parseClass(argument, nms, obc);
							field.set(null, clazz);
						} else if(type == 'F') {
							Matcher fieldMatcher = fieldPattern.matcher(argument);
							if(fieldMatcher.matches()) {
								Class<?> clazz = parseClass(fieldMatcher.group(1), nms, obc);
								String name = fieldMatcher.group(2);
								field.set(null, clazz.getDeclaredField(name));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else if(type == 'M') {
							Matcher methodMatcher = methodPattern.matcher(argument);
							if(methodMatcher.matches()) {
								Class<?> clazz = parseClass(methodMatcher.group(1), nms, obc);
								String name = methodMatcher.group(2);
								String[] parameters = methodMatcher.group(3).length() > 0 ? methodMatcher.group(3).split(",") : new String[0];
								Class<?>[] parameterTypes = new Class<?>[parameters.length];
								for(int i = 0; i < parameters.length; i++) {
									parameterTypes[i] = parseClass(parameters[i], nms, obc);
								}
								field.set(null, clazz.getDeclaredMethod(name, parameterTypes));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else if(type == 'N') {
							Matcher newMatcher = newPattern.matcher(argument);
							if(newMatcher.matches()) {
								Class<?> clazz = parseClass(newMatcher.group(1), nms, obc);
								String[] parameters = newMatcher.group(2).length() > 0 ? newMatcher.group(2).split(",") : new String[0];
								Class<?>[] parameterTypes = new Class<?>[parameters.length];
								for(int i = 0; i < parameters.length; i++) {
									parameterTypes[i] = parseClass(parameters[i], nms, obc);
								}
								field.set(null, clazz.getConstructor(parameterTypes));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot parse line: " + line);
						}
					} catch(Exception e) {
						iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot parse line: " + line, e);
					}
				}
			}
		} catch(IOException e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required reflection configuration.", e);
		}
	}
	
	public static Class<?> parseClass(String clazz, String nms, String obc) {
		switch(clazz) {
			case "boolean":
				return boolean.class;
			case "boolean[]":
				return boolean[].class;
			case "byte":
				return byte.class;
			case "byte[]":
				return byte[].class;
			case "short":
				return short.class;
			case "short[]":
				return short[].class;
			case "int":
				return int.class;
			case "int[]":
				return int[].class;
			case "long":
				return long.class;
			case "long[]":
				return long[].class;
			case "float":
				return float.class;
			case "float[]":
				return float[].class;
			case "double":
				return double.class;
			case "double[]":
				return double[].class;
		}
		if(clazz.endsWith("[]")) {
			clazz = "[L" + clazz.substring(0, clazz.length() - 2) + ";";
		}
		try {
			return Class.forName(clazz.replace("{nms}", nms).replace("{obc}", obc));
		} catch(ClassNotFoundException e) {
			try {
				Field field = Reflection.class.getDeclaredField(clazz);
				if(field.getType().equals(Class.class)) {
					return (Class<?>)field.get(null);
				}
			} catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e2) {
			}
			if(iDisguise.getInstance().debugMode()) {
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot find the given class file.", e);
			}
		}
		return null;
	}
	
	static {
		load("reflect.txt", NMS, OBC);
	}
	
}
