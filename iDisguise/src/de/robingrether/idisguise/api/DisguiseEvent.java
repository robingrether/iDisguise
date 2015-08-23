package de.robingrether.idisguise.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired whenever someone attempts to disguise.<br>
 * This class replaces the old DisguiseAsPlayerEvent and DisguiseAsMobEvent classes.
 * 
 * @since 3.0.1
 * @author Robingrether
 */
public class DisguiseEvent extends PlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	protected Disguise disguise;
	
	public DisguiseEvent(Player player, Disguise disguise) {
		super(player);
		this.disguise = disguise;
	}
	
	/**
	 * Returns the disguise
	 * 
	 * @since 3.0.1
	 * @return the disguise
	 */
	public Disguise getDisguise() {
		return disguise;
	}
	
	/**
	 * Changes the disguise
	 * 
	 * @since 3.0.1
	 * @param disguise the disguise to change to
	 */
	public void setDisguise(Disguise disguise) {
		this.disguise = disguise;
	}
	
	/**
	 * Checks whether this event is cancelled
	 * 
	 * @since 3.0.1
	 * @return true if cancelled, false if not
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Sets whether this event is cancelled
	 * 
	 * @since 3.0.1
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