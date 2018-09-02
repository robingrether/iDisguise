package de.robingrether.idisguise.disguise;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a disguise as a normal horse that can be 'styled'.
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class StyledHorseDisguise extends HorseDisguise {
	
	private Style style;
	private Color color;
	private Armor armor;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public StyledHorseDisguise() {
		this(true, Style.NONE, Color.BROWN, false, Armor.NONE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public StyledHorseDisguise(boolean adult, Style style, Color color, boolean saddled, Armor armor) {
		super(DisguiseType.HORSE, adult, saddled);
		this.style = style;
		this.color = color;
		this.armor = armor;
	}
	
	/**
	 * @since 5.5.1
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @since 5.5.1
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @since 5.5.1
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @since 5.5.1
	 */
	public void setStyle(Style style) {
		this.style = style;
	}
	
	/**
	 * @since 5.8.1
	 */
	public Armor getArmor() {
		return armor;
	}
	
	/**
	 * @since 5.8.1
	 */
	public void setArmor(Armor armor) {
		this.armor = armor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; pattern=%s; color=%s; armor=%s", super.toString(),
				style.name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
				color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
				armor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	static {
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Style style : Style.values()) {
			parameterSuggestions.add(style.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(StyledHorseDisguise.class, (disguise, parameter) -> disguise.setStyle(Style.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "pattern", parameterSuggestions);
		
		parameterSuggestions = new HashSet<String>();
		for(Color color : Color.values()) {
			parameterSuggestions.add(color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(StyledHorseDisguise.class, (disguise, parameter) -> disguise.setColor(Color.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "color", parameterSuggestions);
		
		parameterSuggestions = new HashSet<String>();
		for(Armor armor : Armor.values()) {
			parameterSuggestions.add(armor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(StyledHorseDisguise.class, (disguise, parameter) -> disguise.setArmor(Armor.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "armor", parameterSuggestions);
	}
	
	/**
	 * Different styles a horse can be.
	 * 
	 * @since 5.5.1
	 * @author RobinGrether
	 */
	public enum Style {
		
		NONE,
		WHITE,
		WHITEFIELD,
		WHITE_DOTS,
		BLACK_DOTS;
		
	}
	
	/**
	 * Different colors a horse can be.
	 * 
	 * @since 5.5.1
	 * @author RobinGrether
	 */
	public enum Color {
		
		WHITE,
		CREAMY,
		CHESTNUT,
		BROWN,
		BLACK,
		GRAY,
		DARK_BROWN;
		
	}
	
}