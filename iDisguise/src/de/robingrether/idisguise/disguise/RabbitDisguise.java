package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.EntityRabbit;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;

public class RabbitDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -2691206163684801474L;
	private RabbitType rabbitType;
	
	public RabbitDisguise(boolean adult) {
		this(adult, RabbitType.BROWN);
	}
	
	public RabbitDisguise(boolean adult, RabbitType rabbitType) {
		super(DisguiseType.RABBIT, adult);
		this.rabbitType = rabbitType;
	}
	
	public RabbitType getRabbitType() {
		return rabbitType;
	}
	
	public void setRabbitType(RabbitType rabbitType) {
		this.rabbitType = rabbitType;
	}
	
	public EntityRabbit getEntity(World world, Location location, int id) {
		EntityRabbit rabbit = (EntityRabbit)super.getEntity(world, location, id);
		rabbit.setRabbitType(rabbitType.getId());
		return rabbit;
	}
	
	public enum RabbitType {
		
		BROWN(0),
		WHITE(1),
		BLACK(2),
		BLACK_AND_WHITE(3),
		GOLD(4),
		SALT_AND_PEPPER(5),
		THE_KILLER_BUNNY(99);
		
		private int id;
		
		private RabbitType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
	}
	
}