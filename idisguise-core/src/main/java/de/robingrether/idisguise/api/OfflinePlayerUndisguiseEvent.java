package de.robingrether.idisguise.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired whenever an offline player is about to be undisguised.
 * 
 * @since 5.3.1
 * @author RobinGrether
 */
public class OfflinePlayerUndisguiseEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	protected final OfflinePlayer offlinePlayer;
	private boolean cancel = false;
	private final Disguise disguise;
	private final boolean undisguiseAll;
	
	public OfflinePlayerUndisguiseEvent(OfflinePlayer offlinePlayer, final Disguise disguise, final boolean undisguiseAll) {
		this.offlinePlayer = offlinePlayer;
		this.disguise = disguise;
		this.undisguiseAll = undisguiseAll;
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
	public final Disguise getDisguise() {
		return disguise;
	}
	
	/**
	 * Indicates whether this event is part of a '/undisguise *' command.
	 * 
	 * @since 5.3.1
	 * @return <code>true</code>, if and only if this event is part of a '/undisguise *' command
	 */
	public final boolean undisguiseAll() {
		return undisguiseAll;
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