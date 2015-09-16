package de.robingrether.idisguise.disguise;

/**
 * Thrown to indicate that the currently used minecraft server is outdated and does not support the invoked operation. <br>
 * For example, creating a {@link Disguise} with a {@link DisguiseType} that is not available on this server.
 * 
 * @since 4.1.1
 * @author Robingrether
 */
public class OutdatedServerException extends RuntimeException {
	
	private static final long serialVersionUID = -8034972905754333854L;
	
	/**
	 * Constructs a new outdated server exception with <code>null</code> as its detail message.
	 * 
	 * @since 4.1.1
	 */
	public OutdatedServerException() {
		super();
	}
	
	/**
	 * Constructs a new outdated server exception with the given detail message.
	 * 
	 * @param message the detail message
	 * @since 4.1.1
	 */
	public OutdatedServerException(String message) {
		super(message);
	}
	
}