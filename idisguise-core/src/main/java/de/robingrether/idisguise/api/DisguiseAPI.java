package de.robingrether.idisguise.api;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.management.Sounds;

/**
 * The API to hook into iDisguise. The following code returns an instance:<br>
 * <code>Bukkit.getServicesManager().getRegistration(DisguiseAPI.class).getProvider();</code>
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public interface DisguiseAPI {
	
	/**
	 * Disguise an offline player.<br>
	 * Calling <code>disguise(offlinePlayer, disguise)</code> is equivalent to calling <code>disguise(offlinePlayer, disguise, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.1.1
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(OfflinePlayer offlinePlayer, Disguise disguise);
	
	/**
	 * Disguise a player.<br>
	 * Calling <code>disguise(player, disguise)</code> is equivalent to calling <code>disguise(player, disguise, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(Player player, Disguise disguise);
	
	/**
	 * Disguise a living entity.<br>
	 * Calling <code>disguise(livingEntity, disguise)</code> is equivalent to calling <code>disguise(livingEntity, disguise, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(LivingEntity livingEntity, Disguise disguise);
	
	/**
	 * Disguise an offline player.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.1.1
	 * @param fireEvent whether an event ({@linkplain OfflinePlayerDisguiseEvent} or {@linkplain DisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(OfflinePlayer offlinePlayer, Disguise disguise, boolean fireEvent);
	
	/**
	 * Disguise a player.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @param fireEvent whether an event ({@linkplain DisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(Player player, Disguise disguise, boolean fireEvent);
	
	/**
	 * Disguise a living entity.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @param fireEvent whether an event ({@linkplain EntityDisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the disguise
	 */
	public boolean disguise(LivingEntity livingEntity, Disguise disguise, boolean fireEvent);
	
	/**
	 * Undisguise an offline player.<br>
	 * Calling <code>undisguise(offlinePlayer)</code> is equivalent to calling <code>undisguise(offlinePlayer, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.1.1
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(OfflinePlayer offlinePlayer);
	
	/**
	 * Undisguise a player.<br>
	 * Calling <code>undisguise(player)</code> is equivalent to calling <code>undisguise(player, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(Player player);
	
	/**
	 * Undisguise a living entity.<br>
	 * Calling <code>undisguise(livingEntity)</code> is equivalent to calling <code>undisguise(livingEntity, true)</code>.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(LivingEntity livingEntity);
	
	/**
	 * Undisguise an offline player.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.1.1
	 * @param fireEvent whether an event ({@linkplain OfflinePlayerUndisguiseEvent} or {@linkplain UndisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(OfflinePlayer offlinePlayer, boolean fireEvent);
	
	/**
	 * Undisguise a player.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @param fireEvent whether an event ({@linkplain UndisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(Player player, boolean fireEvent);
	
	/**
	 * Undisguise a living entity.<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 5.7.1
	 * @param fireEvent whether an event ({@linkplain EntityUndisguiseEvent}) shall be fired
	 * @return <code>false</code>, if another plugin denies the undisguise
	 */
	public boolean undisguise(LivingEntity livingEntity, boolean fireEvent);
	
	/**
	 * Undisguise everyone (includes online and offline players as well as entities).<br>
	 * <br>
	 * <strong>This function must only be accessed synchronously.</strong>
	 * 
	 * @since 2.1.3
	 */
	public void undisguiseAll();
	
	/**
	 * Indicates whether an offline player is disguised.
	 * 
	 * @since 5.1.1
	 */
	public boolean isDisguised(OfflinePlayer offlinePlayer);
	
	/**
	 * Indicates whether a player is disguised.
	 * 
	 * @since 2.1.3
	 */
	public boolean isDisguised(Player player);
	
	/**
	 * Indicates whether a living entity is disguised.
	 * 
	 * @since 5.7.1
	 */
	public boolean isDisguised(LivingEntity livingEntity);
	
	/**
	 * Indicates whether an offline player is disguised <strong>and</strong> the disguise is visible to a given observer.<br>
	 * Calling this function is similar to: <code>api.isDisguised(offlinePlayer) && api.getDisguise(offlinePlayer).isVisibleTo(observer)</code>
	 * 
	 * @since 5.6.1
	 * @param offlinePlayer the offline player to check
	 * @param observer the observing player
	 * @return <code>true</code>, if and only if the offline player is disguised <strong>and</strong> the disguise is visible to the given observer
	 */
	public boolean isDisguisedTo(OfflinePlayer offlinePlayer, Player observer);
	
	/**
	 * Indicates whether a player is disguised <strong>and</strong> the disguise is visible to a given observer.<br>
	 * Calling this function is similar to: <code>api.isDisguised(player) && api.getDisguise(player).isVisibleTo(observer)</code>
	 * 
	 * @since 5.7.1
	 * @param player the player to check
	 * @param observer the observing player
	 * @return <code>true</code>, if and only if the player is disguised <strong>and</strong> the disguise is visible to the given observer
	 */
	public boolean isDisguisedTo(Player player, Player observer);
	
	/**
	 * Indicates whether a living entity is disguised <strong>and</strong> the disguise is visible to a given observer.<br>
	 * Calling this function is similar to: <code>api.isDisguised(livingEntity) && api.getDisguise(livingEntity).isVisibleTo(observer)</code>
	 * 
	 * @since 5.7.1
	 * @param livingEntity the living entity to check
	 * @param observer the observing player
	 * @return <code>true</code>, if and only if the living entity is disguised <strong>and</strong> the disguise is visible to the given observer
	 */
	public boolean isDisguisedTo(LivingEntity livingEntity, Player observer);
	
	/**
	 * Get a copy of an offline player's current disguise.
	 * 
	 * @since 5.1.1
	 * @return a <strong>copy</strong> of the given offline player's disguise
	 */
	public Disguise getDisguise(OfflinePlayer offlinePlayer);
	
	/**
	 * Get a copy of a player's current disguise.
	 * 
	 * @since 2.1.3
	 * @return a <strong>copy</strong> of the given player's disguise
	 */
	public Disguise getDisguise(Player player);
	
	/**
	 * Get a copy of a living entity's current disguise.
	 * 
	 * @since 5.7.1
	 * @return a <strong>copy</strong> of the given living entity's disguise
	 */
	public Disguise getDisguise(LivingEntity livingEntity);
	
	/**
	 * Get the number of <strong>online</strong> players who are disguised.
	 * 
	 * @since 5.6.1
	 * @return the number of online players who are disguised
	 */
	public int getNumberOfDisguisedPlayers();
	
	/**
	 * Gets the {@link Sounds} for a specific entity type.
	 * 
	 * @since 5.1.1
	 * @param type the entity/disguise type
	 * @return the sounds for the given entity/disguise type
	 */
	public Sounds getSoundsForEntity(DisguiseType type);
	
	/**
	 * Sets the {@link Sounds} for a specific entity type.
	 * 
	 * @since 5.1.1
	 * @param type the entity/disguise type
	 * @param sounds the sounds
	 * @return <code>true</code>, if the sounds have been set
	 */
	public boolean setSoundsForEntity(DisguiseType type, Sounds sounds);
	
	/**
	 * Indicates whether the disguised players' sounds are currently replaced.
	 * 
	 * @since 5.1.1
	 * @return <code>true</code>, if they are replaced
	 */
	public boolean isSoundsEnabled();
	
	/**
	 * Sets whether the disguised players' sounds are replaced.
	 * 
	 * @since 5.1.1
	 * @param enabled <code>true</code>, if they shall be replaced
	 */
	public void setSoundsEnabled(boolean enabled);
	
	/**
	 * Indicates whether a given player has the required permissions to disguise as the given disguise type.<br>
	 * This function will always return <code>false</code> for {@link DisguiseType#PLAYER}.
	 * 
	 * @since 5.5.4
	 * @param player the player
	 * @param type the disguise type
	 * @return <code>true</code>, if and only if the player has the required permissions
	 */
	public boolean hasPermission(Player player, DisguiseType type);
	
	/**
	 * Indicates whether a given player has the required permissions to carry the given disguise.
	 * 
	 * @since 5.5.4
	 * @param player the player
	 * @param disguise the disguise
	 * @return <code>true</code>, if and only if the player has all required permissions for the given disguise (including subtype permissions)
	 */
	public boolean hasPermission(Player player, Disguise disguise);
	
	/**
	 * Indicates whether a given player can see through disguises.
	 * 
	 * @since 5.6.4
	 * @param player the player
	 * @return <code>true</code>, if the player can see through disguises
	 */
	public boolean canSeeThrough(OfflinePlayer player);
	
	/**
	 * Sets whether a given player can see through disguises.
	 * 
	 * @since 5.6.4
	 * @param player the player
	 * @param seeThrough <code>true</code>, if the player shall see through disguises
	 */
	public void setSeeThrough(OfflinePlayer player, boolean seeThrough);
	
	/**
	 * Get a {@linkplain Set} of all disguised players (online and offline) and entities.<br>
	 * Each element of this {@linkplain Set} is an instance of either {@linkplain OfflinePlayer} or {@linkplain LivingEntity}.
	 * 
	 * @since 5.7.1
	 */
	public Set<Object> getDisguisedEntities();
	
}