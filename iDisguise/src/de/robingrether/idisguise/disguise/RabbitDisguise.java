package de.robingrether.idisguise.disguise;

import java.util.Locale;

/**
 * Represents a disguise as a rabbit.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class RabbitDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = 1540855063412621247L;
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
	public String toString() {
		return super.toString() + "; " + rabbitType.name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer");
	}
	
	static {
		for(RabbitType rabbitType : RabbitType.values()) {
			Subtypes.registerSubtype(RabbitDisguise.class, "setRabbitType", rabbitType, rabbitType.name().toLowerCase(Locale.ENGLISH).replace("_and_", "-").replace("the_killer_bunny", "killer"));
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