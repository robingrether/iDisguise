package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.Color;
import org.bukkit.Particle;

/**
 * Represents a disguise as an area effect cloud.
 * 
 * @since 5.7.1
 * @author RobinGrether
 */
public class AreaEffectCloudDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = 300566387302741105L;
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
		return super.toString() + "; radius=" + radius + "; color=" + color.asRGB() + "; " + particle.name().toLowerCase(Locale.ENGLISH);
	}
	
	static {
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setRadius", "radius", float.class);
		Subtypes.registerParameterizedSubtype(AreaEffectCloudDisguise.class, "setColor", "color", int.class);
		try {
			for(String colorName : new String[] {"AQUA", "BLACK", "BLUE", "FUCHSIA", "GRAY", "GREEN", "LIME", "MAROON", "NAVY", "OLIVE", "ORANGE", "PURPLE", "RED", "SILVER", "TEAL", "WHITE", "YELLOW"}) {
				Subtypes.registerSubtype(AreaEffectCloudDisguise.class, "setColor", Color.class.getDeclaredField(colorName).get(null), colorName.toLowerCase(Locale.ENGLISH));
			}
		} catch(Exception e) { // fail silently
		}
		for(String particle : new String[] {"EXPLOSION_NORMAL", "EXPLOSION_LARGE", "EXPLOSION_HUGE", "FIREWORKS_SPARK", "WATER_BUBBLE", "WATER_SPLASH", "WATER_WAKE", "CRIT", "CRIT_MAGIC", "SMOKE_NORMAL",
				"SMOKE_LARGE", "SPELL", "SPELL_INSTANT", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL_WITCH", "DRIP_WATER", "DRIP_LAVA", "VILLAGER_ANGRY", "VILLAGER_HAPPY", "NOTE", "PORTAL", "ENCHANTMENT_TABLE",
				"FLAME", "REDSTONE", "SNOWBALL", "SNOW_SHOVEL", "HEART", "BARRIER", "ITEM_CRACK", "BLOCK_CRACK", "WATER_DROP", "DRAGON_BREATH", "END_ROD", "DAMAGE_INDICATOR", "SWEEP_ATTACK", "FALLING_DUST",
				"TOTEM", "SPIT"}) {
			try {
				Subtypes.registerSubtype(AreaEffectCloudDisguise.class, "setParticle", Particle.valueOf(particle), particle.toLowerCase(Locale.ENGLISH).replace('_', '-'));
			} catch(IllegalArgumentException e) { // fail silently
			}
		}
	}
	
}