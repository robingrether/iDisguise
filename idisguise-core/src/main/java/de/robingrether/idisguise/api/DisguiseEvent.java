package de.robingrether.idisguise.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired whenever an online player is about to be disguised.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class DisguiseEvent extends PlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	protected final Disguise disguise;
	
	public DisguiseEvent(Player player, final Disguise disguise) {
		super(player);
		this.disguise = disguise;
	}
	
	/**
	 * Returns a copy of the disguise.
	 * 
	 * @since 3.0.1
	 * @return the disguise
	 */
	public final Disguise getDisguise() {
		return disguise.clone();
	}
	
	@Deprecated
	public void setDisguise(Disguise disguise) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Checks whether this event is cancelled.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code>, if and only if this event is cancelled
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Sets whether this event is cancelled.
	 * 
	 * @since 3.0.1
	 * @param cancel <code>true</code> if this event shall be cancelled
	 */
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}