package de.robingrether.idisguise.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired when a player undisguises.
 * 
 * @since 2.2.1
 * @author Robingrether
 */
public class UndisguiseEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final Disguise disguise;
	private final boolean undisguiseAll;
	
	public UndisguiseEvent(Player player, final Disguise disguise, final boolean undisguiseAll) {
		super(player);
		this.disguise = disguise;
		this.undisguiseAll = undisguiseAll;
	}
	
	/**
	 * Returns the disguise
	 * 
	 * @since 2.2.1
	 * @return the disguise
	 */
	public final Disguise getDisguise() {
		return disguise;
	}
	
	/**
	 * Indicates whether this undisguise is part of a '/undisguise *' command
	 * 
	 * @since 4.0.1
	 * @return <code>true</code>, if this undisguise is part of a a '/undisguise *' command
	 */
	public final boolean undisguiseAll() {
		return undisguiseAll;
	}
	
	/**
	 * Checks whether this event is cancelled
	 * 
	 * @since 2.2.1
	 * @return true if cancelled, false if not
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Sets whether this event is cancelled
	 * 
	 * @since 2.2.1
	 * @param cancel true if it should be cancelled
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