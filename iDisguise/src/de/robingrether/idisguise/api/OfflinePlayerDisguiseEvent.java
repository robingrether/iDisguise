package de.robingrether.idisguise.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired whenever an offline player is about to be disguised.
 * 
 * @since 5.3.1
 * @author RobinGrether
 */
public class OfflinePlayerDisguiseEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	protected final OfflinePlayer offlinePlayer;
	private boolean cancel = false;
	protected Disguise disguise;
	
	public OfflinePlayerDisguiseEvent(OfflinePlayer offlinePlayer, Disguise disguise) {
		this.offlinePlayer = offlinePlayer;
		this.disguise = disguise;
	}
	
	/**
	 * Returns the offline player.
	 * 
	 * @since 5.3.1
	 * @return the offline player
	 */
	public final OfflinePlayer getPlayer() {
		return offlinePlayer;
	}
	
	/**
	 * Returns the disguise.
	 * 
	 * @since 5.3.1
	 * @return the disguise
	 */
	public Disguise getDisguise() {
		return disguise;
	}
	
	/**
	 * Changes the disguise.
	 * 
	 * @since 5.3.1
	 * @param disguise the disguise to change to
	 */
	public void setDisguise(Disguise disguise) {
		this.disguise = disguise;
	}
	
	/**
	 * Checks whether this event is cancelled.
	 * 
	 * @since 5.3.1
	 * @return <code>true</code>, if and only if this event is cancelled
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Sets whether this event is cancelled.
	 * 
	 * @since 5.3.1
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