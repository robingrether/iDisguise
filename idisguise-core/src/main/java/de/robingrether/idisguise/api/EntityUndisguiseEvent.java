package de.robingrether.idisguise.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import de.robingrether.idisguise.disguise.Disguise;

/**
 * This event is fired whenever an entity is about to be undisguised.
 * 
 * @since 5.7.1
 * @author RobinGrether
 */
public class EntityUndisguiseEvent extends EntityEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final Disguise disguise;
	private final boolean undisguiseAll;
	
	public EntityUndisguiseEvent(LivingEntity livingEntity, final Disguise disguise, final boolean undisguiseAll) {
		super(livingEntity);
		if(livingEntity instanceof Player) throw new IllegalArgumentException("Entity events must not be called for players!");
		this.disguise = disguise;
		this.undisguiseAll = undisguiseAll;
	}
	
	/**
	 * Returns the disguise.
	 * 
	 * @since 5.7.1
	 */
	public final Disguise getDisguise() {
		return disguise;
	}
	
	/**
	 * Indicates whether this event is part of a '/undisguise *' command
	 * 
	 * @since 5.7.1
	 */
	public final boolean undisguiseAll() {
		return undisguiseAll;
	}
	
	/**
	 * Checks whether this event is cancelled.
	 * 
	 * @since 5.7.1
	 */
	public boolean isCancelled() {
		return cancel;
	}
	
	/**
	 * Sets whether this event is cancelled.
	 * 
	 * @since 5.7.1
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