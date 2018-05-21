package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a normal horse that can be 'styled'.
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class StyledHorseDisguise extends HorseDisguise {
	
	private Style style;
	private Color color;
	
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
		super(DisguiseType.HORSE, adult, saddled, armor);
		this.style = style;
		this.color = color;
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
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s", super.toString(),
				style.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings"),
				color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	static {
		for(Style style : Style.values()) {
			Subtypes.registerSubtype(StyledHorseDisguise.class, "setStyle", style, style.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("white$", "white-stripes").replace("none", "no-markings"));
		}
		for(Color color : Color.values()) {
			Subtypes.registerSubtype(StyledHorseDisguise.class, "setColor", color, color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
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