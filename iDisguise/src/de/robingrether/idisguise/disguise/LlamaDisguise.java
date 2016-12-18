package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a llama.
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class LlamaDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -6219216156842365747L;
	private Color color;
	private SaddleColor saddle;
	private boolean hasChest;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public LlamaDisguise() {
		this(true, Color.CREAMY, SaddleColor.NOT_SADDLED, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public LlamaDisguise(boolean adult, Color color, SaddleColor saddle, boolean hasChest) {
		super(DisguiseType.LLAMA, adult);
		this.color = color;
		this.saddle = saddle;
		this.hasChest = hasChest;
	}
	
	/**
	 * Returns the color.
	 * 
	 * @since 5.5.1
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @since 5.5.1
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Returns the saddle color.
	 * 
	 * @since 5.5.1
	 * @return the saddle color
	 */
	public SaddleColor getSaddle() {
		return saddle;
	}
	
	/**
	 * Sets the saddle color.
	 * 
	 * @since 5.5.1
	 * @param saddle the saddle color
	 */
	public void setSaddle(SaddleColor saddle) {
		this.saddle = saddle;
	}
	
	/**
	 * Returns whether the llama has a chest.
	 * 
	 * @since 5.5.1
	 * @return <code>true</code> if the llama has a chest
	 */
	public boolean hasChest() {
		return hasChest;
	}
	
	/**
	 * Sets whether the llama has a chest.
	 * 
	 * @since 5.5.1
	 * @param hasChest <code>true</code> if the llama shall have a chest
	 */
	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public LlamaDisguise clone() {
		LlamaDisguise clone = new LlamaDisguise(adult, color, saddle, hasChest);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && ((LlamaDisguise)object).color.equals(color) && ((LlamaDisguise)object).saddle.equals(saddle) && ((LlamaDisguise)object).hasChest == hasChest;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return super.toString() + "; " + color.name().toLowerCase(Locale.ENGLISH) + "; " + saddle.name().toLowerCase(Locale.ENGLISH).replace('_', '-') + "; " + (hasChest ? "chest" : "no-chest");
	}
	
	static {
		for(Color color : Color.values()) {
			Subtypes.registerSubtype(LlamaDisguise.class, "setColor", color, color.name().toLowerCase(Locale.ENGLISH));
		}
		for(SaddleColor saddle : SaddleColor.values()) {
			Subtypes.registerSubtype(LlamaDisguise.class, "setSaddle", saddle, saddle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerSubtype(LlamaDisguise.class, "setHasChest", true, "chest");
		Subtypes.registerSubtype(LlamaDisguise.class, "setHasChest", false, "no-chest");
	}
	
	/**
	 * Different colors a llama can be.
	 * 
	 * @since 5.5.1
	 * @author RobinGrether
	 */
	public enum Color {
		
		CREAMY,
		WHITE,
		BROWN,
		GRAY;
		
	}
	
	/**
	 * Different saddle colors a llama can have.<br>
	 * {@linkplain SaddleColor#NOT_SADDLED} means no saddle is shown.
	 * 
	 * @since 5.5.1
	 * @author RobinGrether
	 */
	public enum SaddleColor {
		
		WHITE,
		ORANGE,
		MAGENTA,
		LIGHT_BLUE,
		YELLOW,
		LIME,
		PINK,
		GRAY,
		SILVER,
		CYAN,
		PURPLE,
		BLUE,
		BROWN,
		GREEN,
		RED,
		BLACK,
		NOT_SADDLED;
		
	}
	
}