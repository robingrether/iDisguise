package de.robingrether.idisguise.disguise;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * Represents a disguise as a llama.
 * 
 * @since 5.5.1
 * @author RobinGrether
 */
public class LlamaDisguise extends AgeableDisguise {
	
	private Color color;
	private SaddleColor saddle;
	private boolean hasChest;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.5.1
	 */
	public LlamaDisguise() {
		this(true, Color.CREAMY, SaddleColor.NONE, false);
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
	public String toString() {
		return String.format("%s; color=%s; saddle=%s; %s", super.toString(), color.name().toLowerCase(Locale.ENGLISH), saddle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), hasChest ? "chest" : "no-chest");
	}
	
	static {
		Set<String> parameterSuggestions = new HashSet<String>();
		for(Color color : Color.values()) {
			parameterSuggestions.add(color.name().toLowerCase(Locale.ENGLISH));
		}
		Subtypes.registerParameterizedSubtype(LlamaDisguise.class, (disguise, parameter) -> disguise.setColor(Color.valueOf(parameter.toUpperCase(Locale.ENGLISH))), "color", parameterSuggestions);
		
		parameterSuggestions = new HashSet<String>();
		for(SaddleColor saddle : SaddleColor.values()) {
			parameterSuggestions.add(saddle.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(LlamaDisguise.class, (disguise, parameter) -> disguise.setSaddle(SaddleColor.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "saddle", parameterSuggestions);
		
		Subtypes.registerSimpleSubtype(LlamaDisguise.class, disguise -> disguise.setHasChest(true), "chest");
		Subtypes.registerSimpleSubtype(LlamaDisguise.class, disguise -> disguise.setHasChest(false), "no-chest");
	}
	
	/**
	 * Different colors a llama can be.
	 * 
	 * @since 5.5.1
	 * @author RobinGrether
	 */
	public enum Color {
		
		CREAMY,
		LIGHT,
		BROWN,
		GRAY;
		
	}
	
	/**
	 * Different saddle colors a llama can have.<br>
	 * {@linkplain SaddleColor#NONE} means no saddle is shown.
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
		LIGHT_GRAY,
		CYAN,
		PURPLE,
		BLUE,
		BROWN,
		GREEN,
		RED,
		BLACK,
		NONE;
		
		/**
		 * @since 5.8.1
		 */
		public ItemStack getItem() {
			if(this == NONE) return null;
			
			if(VersionHelper.require1_13()) {
				return new ItemStack(Material.valueOf(name() + "_CARPET"), 1);
			} else {
				return new ItemStack(Material.valueOf("CARPET"), 1, (short)ordinal());
			}
		}
		
	}
	
}