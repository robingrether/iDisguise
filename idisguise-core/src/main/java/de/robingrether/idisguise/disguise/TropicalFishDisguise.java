package de.robingrether.idisguise.disguise;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.DyeColor;

public class TropicalFishDisguise extends MobDisguise {
	
	private DyeColor bodyColor;
	private DyeColor patternColor;
	private Pattern pattern;
	
	public TropicalFishDisguise() {
		this(DyeColor.WHITE, DyeColor.RED, Pattern.CLAYFISH);
	}
	
	public TropicalFishDisguise(DyeColor bodyColor, DyeColor patternColor, Pattern pattern) {
		super(DisguiseType.TROPICAL_FISH);
		this.bodyColor = bodyColor;
		this.patternColor = patternColor;
		this.pattern = pattern;
	}
	
	public DyeColor getBodyColor() {
		return bodyColor;
	}
	
	public void setBodyColor(DyeColor bodyColor) {
		this.bodyColor = bodyColor;
	}
	
	public DyeColor getPatternColor() {
		return patternColor;
	}
	
	public void setPatternColor(DyeColor patternColor) {
		this.patternColor = patternColor;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public String toString() {
		return String.format("%s; body-color=%s; pattern-color=%s; pattern=%s", super.toString(),
				bodyColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
				patternColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'),
				pattern.name().toLowerCase(Locale.ENGLISH));
	}
	
	static {
		Set<String> parameterSuggestions = new HashSet<String>();
		for(DyeColor color : DyeColor.values()) {
			parameterSuggestions.add(color.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerParameterizedSubtype(TropicalFishDisguise.class, (disguise, parameter) -> disguise.setBodyColor(DyeColor.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "body-color", parameterSuggestions);
		Subtypes.registerParameterizedSubtype(TropicalFishDisguise.class, (disguise, parameter) -> disguise.setPatternColor(DyeColor.valueOf(parameter.toUpperCase(Locale.ENGLISH).replace('-', '_'))), "pattern-color", parameterSuggestions);
		
		parameterSuggestions = new HashSet<String>();
		for(Pattern pattern : Pattern.values()) {
			parameterSuggestions.add(pattern.name().toLowerCase(Locale.ENGLISH));
		}
		Subtypes.registerParameterizedSubtype(TropicalFishDisguise.class, (disguise, parameter) -> disguise.setPattern(Pattern.valueOf(parameter.toUpperCase(Locale.ENGLISH))), "pattern", parameterSuggestions);
	}
	
	public enum Pattern {
		
		KOB(0 << 8 | 0),
		SUNSTREAK(1 << 8 | 0),
		SNOOPER(2 << 8 | 0),
		DASHER(3 << 8 | 0),
		BRINELY(4 << 8 | 0),
		SPOTTY(5 << 8 | 0),
		FLOPPER(0 << 8 | 1),
		STRIPEY(1 << 8 | 1),
		GLITTER(2 << 8 | 1),
		BLOCKFISH(3 << 8 | 1),
		BETTY(4 << 8 | 1),
		CLAYFISH(5 << 8 | 1);
		
		private final int data;
		
		private Pattern(int data) {
			this.data = data;
		}
		
		public int getData() {
			return data;
		}
		
	}
	
}
