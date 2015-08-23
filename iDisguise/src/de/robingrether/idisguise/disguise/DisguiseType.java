package de.robingrether.idisguise.disguise;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * This enum contains all types, you can disguise as.<br>
 * <br>
 * WARNING: At any time, types may be added/removed from this Enum.
 * 
 * @since 2.1.3
 * @author Robingrether
 */
public enum DisguiseType {
	
	BAT(Type.MOB),
	BLAZE(Type.MOB),
	CAVE_SPIDER(Type.MOB),
	CHICKEN(Type.MOB),
	COW(Type.MOB),
	CREEPER(Type.MOB),
	ENDER_DRAGON(Type.MOB),
	ENDERMAN(Type.MOB),
	ENDERMITE(Type.MOB),
	GHAST(Type.MOB),
	GIANT(Type.MOB),
	GUARDIAN(Type.MOB),
	HORSE(Type.MOB),
	IRON_GOLEM(Type.MOB),
	MAGMA_CUBE(Type.MOB),
	MUSHROOM_COW(Type.MOB),
	OCELOT(Type.MOB),
	PIG(Type.MOB),
	PIG_ZOMBIE(Type.MOB),
	RABBIT(Type.MOB),
	SHEEP(Type.MOB),
	SILVERFISH(Type.MOB),
	SKELETON(Type.MOB),
	SLIME(Type.MOB),
	SNOWMAN(Type.MOB),
	SPIDER(Type.MOB),
	SQUID(Type.MOB),
	VILLAGER(Type.MOB),
	WITCH(Type.MOB),
	WITHER(Type.MOB),
	WOLF(Type.MOB),
	ZOMBIE(Type.MOB),
	
	GHOST(Type.PLAYER),
	PLAYER(Type.PLAYER);
	
	//BLOCK(Type.OBJECT),
	//ENDER_CRYSTAL(Type.OBJECT),
	//PRIMED_TNT(Type.OBJECT);
	
	private Type type;
	
	private DisguiseType(Type type) {
		this.type = type;
	}
	
	/**
	 * Checks whether the type is a mob.
	 * 
	 * @since 2.1.3
	 * @return true if it's a mob, false if not
	 */
	public boolean isMob() {
		return type == Type.MOB;
	}
	
	/**
	 * Checks whether the type is a player.
	 * 
	 * @since 2.1.3
	 * @return true if it's a player, false if not
	 */
	public boolean isPlayer() {
		return type == Type.PLAYER;
	}
	
	/**
	 * Checks whether the type is an object.
	 * 
	 * @since 2.1.3
	 * @return true if it's an object, false if not
	 */
	public boolean isObject() {
		return type == Type.OBJECT;
	}
	
	/**
	 * Returns the type of this disguise type.
	 * 
	 * @since 2.2.2
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	private static Random random = new Random();
	
	/**
	 * Creates a random disguise type.
	 * 
	 * @since 2.2.2
	 * @param type the type the disguise type should be, if this is null the type is ignored
	 * @return a random disguise type
	 */
	public static DisguiseType random(Type type) {
		LinkedList<DisguiseType> types = new LinkedList<DisguiseType>(Arrays.asList(values()));
		if(type != null) {
			int pos = 0;
			while(pos < types.size()) {
				if(types.get(pos).getType() != type) {
					types.remove(pos);
				} else {
					pos++;
				}
			}
		}
		return types.get(random.nextInt(types.size()));
	}
	
	/**
	 * The type a disguise can be: mob, player, object.
	 * 
	 * @since 2.1.3
	 * @author Robingrether
	 */
	public enum Type {
		
		MOB,
		PLAYER,
		OBJECT
		
	}
	
}