package de.robingrether.idisguise.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.management.DisguiseManager;

/**
 * This event is fired whenever someone right clicks an entity that represents a disguised player.<br>
 * This event was introduced to enable new plugin functionality (e.g. MobAbilities).
 * 
 * @since 5.2.1
 * @author RobinGrether
 */
public class PlayerInteractDisguisedPlayerEvent extends PlayerInteractEntityEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private final Disguise disguise;
	
	public PlayerInteractDisguisedPlayerEvent(Player who, Player clicked) {
		super(who, clicked);
		disguise = DisguiseManager.getInstance().getDisguise(clicked).clone();
	}
	
	/**
	 * Gets the disguised player that was rightclicked by the player.
	 * 
	 * @since 5.2.1
	 * @return disguised player right clicked by player
	 */
	public Player getRightClicked() {
		return (Player)clickedEntity;
	}
	
	/**
	 * Gets the disguise of the rightclicked player.
	 * 
	 * @since 5.2.1
	 * @return disguise of the rightclicked player
	 */
	public Disguise getDisguise() {
		return disguise;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}