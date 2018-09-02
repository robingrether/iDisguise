package de.robingrether.idisguise.disguise;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.DyeColor;

/**
 * Represents a disguise as a sheep.
 * 
 * @since 5.1.1
 * @author RobinGrether
 */
public class SheepDisguise extends AgeableDisguise {
	
	private DyeColor color;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 */
	public SheepDisguise() {
		this(true, DyeColor.WHITE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.1.1
	 * @param adult should the disguise be an adult
	 * @param color the color of the wool
	 */
	public SheepDisguise(boolean adult, DyeColor color) {
		super(DisguiseType.SHEEP, adult);
		this.color = color;
	}
	
	/**
	 * Returns the color.
	 * 
	 * @since 5.1.1
	 * @return the wool color
	 */
	public DyeColor getColor() {
		return color;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @since 5.1.1
	 * @param color the wool color
	 */
	public void setColor(DyeColor color) {
		this.color = color;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; color=%s", super.toString(), color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	static {
		Set<String> parameterSuggestions = new HashSet<String>();
		for(DyeColor color : DyeColor.values()) {
			parameterSuggestions.add(color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(SheepDisguise.class, (disguise, parameter) -> disguise.setColor(DyeColor.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "color", parameterSuggestions);
	}
	
}