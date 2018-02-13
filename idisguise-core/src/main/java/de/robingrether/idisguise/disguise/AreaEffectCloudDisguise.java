package de.robingrether.idisguise.disguise;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import org.bukkit.Color;
import org.bukkit.Particle;

public class AreaEffectCloudDisguise extends ObjectDisguise {
	
	private static final long serialVersionUID = 300566387302741105L;
	private float radius;
	private Color color;
	private Particle particle;

	public AreaEffectCloudDisguise() {
		this(0.5f, Color.GRAY, Particle.SPELL_MOB);
	}
	
	public AreaEffectCloudDisguise(float radius, Color color, Particle particle) {
		super(DisguiseType.AREA_EFFECT_CLOUD);
		if(radius <= 0) throw new IllegalArgumentException("Radius must be positive!");
		this.radius = radius;
		this.color = color;
		this.particle = particle;
		
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		if(radius <= 0) throw new IllegalArgumentException("Radius must be positive!");
		this.radius = radius;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setColor(int rgb) {
		this.color = Color.fromRGB(rgb);
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	
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
		for(Particle particle : Particle.values()) {
			Subtypes.registerSubtype(AreaEffectCloudDisguise.class, "setParticle", particle, particle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
	}
	
}