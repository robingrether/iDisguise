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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as an area effect cloud.
 * 
 * @since 5.7.1
 * @author RobinGrether
 */
@SuppressWarnings("deprecation")
public class AreaEffectCloudDisguise extends ObjectDisguise {
	
	public static final Particle DEFAULT_PARTICLE = Particle.SPELL_MOB;
	public static final Map<Particle, Object> DEFAULT_PARAMETERS;
	
	private float radius;
	private Particle particle;
	private Object parameter;
	
	/**
	 * Creates an instance.<br>
	 * The defaults are: radius <code>0.5</code>, {@linkplain Particle#SPELL_MOB}, {@linkplain Color#GRAY}.
	 * 
	 * @since 5.7.1
	 */
	public AreaEffectCloudDisguise() {
		this(0.5f, DEFAULT_PARTICLE);
	}
	
	/**
	 * @since 5.7.1
	 * 
	 * @deprecated Replaced by {@linkplain #AreaEffectCloudDisguise(float, Particle, Object)}.
	 */
	@Deprecated
	public AreaEffectCloudDisguise(float radius, Color color, Particle particle) {
		this(radius, particle, color);
	}
	
	/**
	 * @since 5.8.1
	 */
	public AreaEffectCloudDisguise(float radius, Particle particle) {
		this(radius, particle, null);
	}
	
	/**
	 * @since 5.8.1
	 * @throws IllegalArgumentException if the given parameter is not valid
	 */
	public AreaEffectCloudDisguise(float radius, Particle particle, Object parameter) {
		super(DisguiseType.AREA_EFFECT_CLOUD);
		setRadius(radius);
		setParticle(particle, parameter);
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
	 * 
	 * @deprecated Replaced by {@linkplain AreaEffectCloudDisguise#getParameter(Object)}.
	 */
	@Deprecated
	public Color getColor() {
		return parameter instanceof Color ? (Color)parameter : null;
	}
	
	/**
	 * @since 5.7.1
	 * 
	 * @deprecated Replaced by {@linkplain AreaEffectCloudDisguise#setParameter(Object)}.
	 */
	@Deprecated
	public void setColor(Color color) {
		setParameter(color);
	}
	
	/**
	 * @since 5.7.1
	 * 
	 * @deprecated Replaced by {@linkplain AreaEffectCloudDisguise#setParameter(Object)}.
	 */
	@Deprecated
	public void setColor(int rgb) {
		setParameter(Color.fromRGB(rgb));
	}
	
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
		setParticle(particle, null);
	}
	
	/**
	 * @since 5.8.1
	 * @throws IllegalArgumentException if the given parameter is not valid
	 */
	public void setParticle(Particle particle, Object parameter) {
		Particle oldParticle = this.particle;
		this.particle = particle;
		try {
			setParameter(parameter);
		} catch(IllegalArgumentException|NullPointerException e) {
			this.particle = oldParticle;
			throw e;
		}
	}
	
	/**
	 * @since 5.8.1
	 */
	public Object getParameter() {
		return parameter;
	}
	
	/**
	 * @since 5.8.1
	 */
	public String getParameterStringRepresentation() {
		Class<?> parameterType = getParameterType(particle);
		if(parameterType.equals(Color.class)) {
			return Integer.toString(((Color)parameter).asRGB());
		} else if(parameterType.equals(ItemStack.class)) {
			return ((ItemStack)parameter).getType().name().toLowerCase(Locale.ENGLISH).replace('_', '-');
		} else if(parameterType.equals(MaterialData.class)) {
			return ((MaterialData)parameter).getItemType().name().toLowerCase(Locale.ENGLISH).replace('_', '-');
		} else if(VersionHelper.require1_13() && parameterType.equals(BlockData.class)) {
			return ((BlockData)parameter).getAsString();
		}
		
		return "";
	}
	
	/**
	 * @since 5.8.1
	 */
	public Class<?> getParameterType() {
		return getParameterType(particle);
	}
	
	/**
	 * @since 5.8.1
	 * @throws IllegalArgumentException if the given parameter is not valid
	 */
	public void setParameter(Object parameter) {
		if(parameter == null) parameter = DEFAULT_PARAMETERS.get(particle);
		Class<?> parameterType = getParameterType(particle);
		if(parameterType.equals(Void.class)) {
			if(parameter != null) {
				throw new IllegalArgumentException(String.format("Illegal argument: Particle %s does not support parameters", particle.name()));
			}
		} else {
			if(!parameterType.isInstance(parameter)) {
				throw new IllegalArgumentException(String.format("Illegal argument type: Particle %s requires %s", particle.name(), particle.getClass().getSimpleName()));
			}
		}
		this.parameter = parameter;
	}
	
	
	/**
	 * Match the particle parameter from its string representation.<br>
	 * This function is used to parse the command argument <em>parameter=...</em>
	 * 
	 * @since 5.8.1
	 * @return <code>true</code>, if the given string representation could be matched to a <strong>valid</strong> parameter, <code>false</code> otherwise
	 */
	public boolean setParameter(String parameter) {
		parameter = parameter.toLowerCase(Locale.ENGLISH).replace('-', '_');
		Class<?> parameterType = getParameterType(particle);
		if(parameterType.equals(Void.class)) {
			setParameter(null);
		} else if(parameterType.equals(Color.class)) {
			if(parameter.isEmpty()) {
				setParameter(null);
				return true;
			} else if(DEFAULT_COLORS.containsKey(parameter)) {
				setParameter(DEFAULT_COLORS.get(parameter));
				return true;
			} else {
				try {
					setParameter(Color.fromRGB(Integer.parseInt(parameter)));
					return true;
				} catch(NumberFormatException e) { // fail silently
				}
			}
		} else if(parameterType.equals(ItemStack.class)) {
			if(Material.matchMaterial(parameter) != null) {
				setParameter(new ItemStack(Material.matchMaterial(parameter)));
				return true;
			}
		} else if(parameterType.equals(MaterialData.class)) {
			if(Material.matchMaterial(parameter) != null) {
				setParameter(new MaterialData(Material.matchMaterial(parameter)));
				return true;
			}
		} else if(VersionHelper.require1_13() && parameterType.equals(BlockData.class)) {
			if(Material.matchMaterial(parameter) != null) {
				setParameter(Material.matchMaterial(parameter).createBlockData());
				return true;
			} else {
				try {
					setParameter(Bukkit.createBlockData(parameter));
					return true;
				} catch(IllegalArgumentException e) { // fail silently
				}
			}
		}
		
		return false;
	}
	
	/**
	 * This map holds the default values of {@linkplain org.bukkit.Color} mapped to their field names (lower case).
	 * 
	 * @since 5.8.1
	 */
	public static final Map<String, Color> DEFAULT_COLORS;
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; radius=%s; particle=%s; parameter=%s", super.toString(), radius, particle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), getParameterStringRepresentation());
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
		DEFAULT_COLORS = Collections.unmodifiableMap(defaultColors2);
		
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setRadius", "radius", float.class, Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("0.1", "0.5", "1.0", "2.0", "5.0", "10.0", "20.0"))));
		
		Set<String> parameterSuggestions = new HashSet<String>();
		Map<Particle, Object> localMap = new HashMap<Particle, Object>(Particle.values().length);
		for(Particle particle : Particle.values()) {
			parameterSuggestions.add(particle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			Class<?> parameterType = getParameterType(particle);
			if(parameterType.equals(Void.class)) {
				localMap.put(particle, null);
			} else if(parameterType.equals(Color.class)) {
				localMap.put(particle, Color.GRAY);
			} else if(parameterType.equals(ItemStack.class)) {
				localMap.put(particle, new ItemStack(Material.STONE));
			} else if(parameterType.equals(MaterialData.class)) {
				localMap.put(particle, new MaterialData(Material.STONE));
			} else if(VersionHelper.require1_13() && parameterType.equals(BlockData.class)) {
				localMap.put(particle, Material.STONE.createBlockData());
			}
		}
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setParticle", "particle", Particle.class, parameterSuggestions);
		DEFAULT_PARAMETERS = Collections.unmodifiableMap(localMap);
		
//		Set<String> colors = new HashSet<String>(defaultColors.keySet());
//		colors.addAll(Arrays.asList("16711680", "65280", "255"));
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setParameter", "parameter", String.class);
		
	}
	
	/**
	 * @since 5.8.1
	 */
	public static Class<?> getParameterType(Particle particle) {
		if(particle.equals(Particle.SPELL_MOB)) return Color.class;
		else return particle.getDataType();
	}
	
}