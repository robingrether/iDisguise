package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a rabbit.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class RabbitDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -2691206163684801474L;
	private RabbitType rabbitType;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public RabbitDisguise() {
		this(true);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param adult whether the rabbit should be an adult
	 */
	public RabbitDisguise(boolean adult) {
		this(adult, RabbitType.BROWN);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 * @param adult whether the rabbit should be an adult
	 * @param rabbitType the type of rabbit this should be
	 */
	public RabbitDisguise(boolean adult, RabbitType rabbitType) {
		super(DisguiseType.RABBIT, adult);
		this.rabbitType = rabbitType;
	}
	
	/**
	 * Returns the rabbit type.
	 * 
	 * @since 4.0.1
	 * @return the rabbit type
	 */
	public RabbitType getRabbitType() {
		return rabbitType;
	}
	
	/**
	 * Sets the rabbit type.
	 * 
	 * @since 4.0.1
	 * @param rabbitType the rabbit type
	 */
	public void setRabbitType(RabbitType rabbitType) {
		this.rabbitType = rabbitType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RabbitDisguise clone() {
		RabbitDisguise clone = new RabbitDisguise(adult, rabbitType);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof RabbitDisguise && ((RabbitDisguise)object).rabbitType.equals(rabbitType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean applySubtype(String argument) {
		if(super.applySubtype(argument)) {
			return true;
		} else {
			switch(argument.replace('-', '_').toLowerCase(Locale.ENGLISH)) {
				case "black":
					setRabbitType(RabbitType.BLACK);
					return true;
				case "blackwhite":
				case "black_white":
				case "blackandwhite":
				case "black_and_white":
					setRabbitType(RabbitType.BLACK_AND_WHITE);
					return true;
				case "brown":
					setRabbitType(RabbitType.BROWN);
					return true;
				case "gold":
					setRabbitType(RabbitType.GOLD);
					return true;
				case "saltpepper":
				case "salt_pepper":
				case "saltandpepper":
				case "salt_and_pepper":
					setRabbitType(RabbitType.SALT_AND_PEPPER);
					return true;
				case "killer":
				case "killer_bunny":
				case "killerbunny":
				case "thekillerbunny":
				case "the_killer_bunny":
					setRabbitType(RabbitType.THE_KILLER_BUNNY);
					return true;
				case "white":
					setRabbitType(RabbitType.WHITE);
					return true;
				default:
					return false;
			}
		}
	}
	
	/**
	 * Represents the available rabbit types.
	 * 
	 * @since 4.0.1
	 * @author RobinGrether
	 */
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
		
		/**
		 * Returns the associated id. <br>
		 * This is used for internal handling.
		 * 
		 * @since 4.0.1
		 * @return the associated id
		 */
		public int getId() {
			return id;
		}
		
	}
	
}