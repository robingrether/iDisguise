package de.robingrether.idisguise.disguise;

/**
 * Represents a disguise as a rabbit.
 * 
 * @since 4.0.1
 * @author RobinGrether
 */
public class RabbitDisguise extends AgeableDisguise {
	
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
	public String toString() {
		return String.format("%s; %s", super.toString(), rabbitType.toString());
	}
	
	static {
		for(RabbitType rabbitType : RabbitType.values()) {
			Subtypes.registerSimpleSubtype(RabbitDisguise.class, disguise -> disguise.setRabbitType(rabbitType), rabbitType.toString());
		}
	}
	
	/**
	 * Represents the available rabbit types.
	 * 
	 * @since 4.0.1
	 * @author RobinGrether
	 */
	public enum RabbitType {
		
		BROWN(0, "brown"),
		WHITE(1, "white"),
		BLACK(2, "black"),
		BLACK_AND_WHITE(3, "black-white"),
		GOLD(4, "gold"),
		SALT_AND_PEPPER(5, "salt-pepper"),
		THE_KILLER_BUNNY(99, "killer");
		
		private final int id;
		private final String name;
		
		private RabbitType(int id, String name) {
			this.id = id;
			this.name = name;
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
		
		public String toString() {
			return name;
		}
		
	}
	
}