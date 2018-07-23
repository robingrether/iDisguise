package de.robingrether.idisguise.disguise;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Particle;

/**
 * Represents a disguise as an area effect cloud.
 * 
 * @since 5.7.1
 * @author RobinGrether
 */
public class AreaEffectCloudDisguise extends ObjectDisguise {
	
	private float radius;
	private Color color;
	private Particle particle;
	
	/**
	 * Creates an instance.<br>
	 * The defaults are: radius <code>0.5<code>, {@link Color#GRAY}, {@link Particle#SPELL_MOB}.
	 * 
	 * @since 5.7.1
	 */
	public AreaEffectCloudDisguise() {
		this(0.5f, Color.GRAY, Particle.SPELL_MOB);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @param radius Radius must be positive and may not be larger than 20!
	 * @since 5.7.1
	 */
	public AreaEffectCloudDisguise(float radius, Color color, Particle particle) {
		super(DisguiseType.AREA_EFFECT_CLOUD);
		if(radius <= 0) throw new IllegalArgumentException("Radius must be positive!");
		if(radius > 20) throw new IllegalArgumentException("Radius may not be larger than 20!");
		this.radius = radius;
		this.color = color;
		this.particle = particle;
		
	}
	
	/**
	 * @since 5.7.1
	 */
	public float getRadius() {
		return radius;
	}
	
	/**
	 * @param radius Radius must be positive and may not be larger than 20!
	 * @since 5.7.1
	 */
	public void setRadius(float radius) {
		if(radius <= 0) throw new IllegalArgumentException("Radius must be positive!");
		if(radius > 20) throw new IllegalArgumentException("Radius may not be larger than 20!");
		this.radius = radius;
	}
	
	/**
	 * @since 5.7.1
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setColor(int rgb) {
		this.color = Color.fromRGB(rgb);
	}
	
	/**
	 * Match a default color from {@linkplain org.bukkit.Color} or parse the given string to an integer (RGB value).
	 * 
	 * @since 5.8.1
	 * @return <code>true</code>, if a matching color (default or RGB) has been found and applied, <code>false</code> otherwise
	 */
	public boolean setColor(String color) {
		if(defaultColors.containsKey(color.toLowerCase(Locale.ENGLISH))) {
			setColor(defaultColors.get(color.toLowerCase(Locale.ENGLISH)));
			return true;
		} else {
			try {
				int rgb = Integer.parseInt(color);
				setColor(rgb);
				return true;
			} catch(NumberFormatException e) {
			}
		}
		return false;
	}
	
	/**
	 * This map holds the default values of {@linkplain org.bukkit.Color} mapped to their field names (lower case).
	 * 
	 * @since 5.8.1
	 */
	public static final Map<String, Color> defaultColors;
	
	/**
	 * @since 5.7.1
	 */
	public Particle getParticle() {
		return particle;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; radius=%s; color=%s; particle=%s", super.toString(), radius, color.asRGB(), particle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	static {
		Map<String, Color> defaultColors2 = new HashMap<String, Color>();
		try {
			for(Field field : Color.class.getFields()) {
				if(Modifier.isStatic(field.getModifiers()) && field.getType().equals(Color.class)) {
					defaultColors2.put(field.getName().toLowerCase(Locale.ENGLISH), (Color)field.get(null));
				}
			}
		} catch(IllegalAccessException e) { // fail silently
		}
		defaultColors = Collections.unmodifiableMap(defaultColors2);
		
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setRadius", "radius", float.class, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("0.1", "0.5", "1.0", "2.0", "5.0", "10.0", "20.0"))));
		
		Set<String> colors = new HashSet<String>(defaultColors.keySet());
		colors.addAll(Arrays.asList("16711680", "65280", "255"));
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setColor", "color", String.class, colors);
		
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Particle particle : Particle.values()) {
			parameterSuggestions.add(particle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setParticle", "particle", Particle.class, parameterSuggestions);
	}
	
}