package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EnumColor;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.DyeColor;
import org.bukkit.Location;

/**
 * Represents a disguise as a colored mob (e.g. sheep, dog).
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class ColoredDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -3226061055696087907L;
	private DyeColor color;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param type the type to disguise as
	 */
	public ColoredDisguise(DisguiseType type) {
		this(type, true, DyeColor.WHITE);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @param type the type to disguise as
	 * @param adult should the disguise be an adult
	 * @param color the color of the disguise
	 */
	public ColoredDisguise(DisguiseType type, boolean adult, DyeColor color) {
		super(type, adult);
		this.color = color;
	}
	
	/**
	 * Returns the color.
	 * 
	 * @since 3.0.1
	 * @return the color
	 */
	public DyeColor getColor() {
		return color;
	}
	
	/**
	 * Changes the color.
	 * 
	 * @since 3.0.1
	 * @param color the color
	 */
	public void setColor(DyeColor color) {
		this.color = color;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ColoredDisguise clone() {
		ColoredDisguise clone = new ColoredDisguise(type, adult, color);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof ColoredDisguise && ((ColoredDisguise)object).getColor().equals(color);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public EntityInsentient getEntity(World world, Location location, int id) {
		EntityInsentient entity = super.getEntity(world, location, id);
		if(entity instanceof EntitySheep) {
			((EntitySheep)entity).setColor(EnumColor.fromColorIndex(color.getData()));
		} else if(entity instanceof EntityWolf) {
			((EntityWolf)entity).setCollarColor(EnumColor.fromColorIndex(color.getData()));
		}
		return entity;
	}
	
}