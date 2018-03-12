package de.robingrether.idisguise.disguise;

import java.util.Locale;

import org.bukkit.DyeColor;

/**
 * Represents a disguise as a wolf.
 * 
 * @since 3.0.1
 * @author RobinGrether
 */
public class WolfDisguise extends AgeableDisguise {
	
	private static final long serialVersionUID = -6203460877408219137L;
	private State state;
	private DyeColor collarColor;
	private boolean sitting;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 4.0.1
	 */
	public WolfDisguise() {
		this(true, State.NORMAL, DyeColor.RED, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 3.0.1
	 * @deprecated Replaced by {@link WolfDisguise#WolfDisguise(boolean, State, DyeColor, boolean)}.
	 */
	@Deprecated
	public WolfDisguise(boolean adult, DyeColor collarColor, boolean tamed, boolean angry) {
		this(adult, tamed ? State.TAMED : angry ? State.ANGRY : State.NORMAL, collarColor, false);
	}
	
	/**
	 * Creates an instance.
	 * 
	 * @since 5.7.1
	 * @param collarColor The collar is invisible unless the state is {@link State#TAMED}.
	 */
	public WolfDisguise(boolean adult, State state, DyeColor collarColor, boolean sitting) {
		super(DisguiseType.WOLF, adult);
		this.state = state;
		this.collarColor = collarColor;
		this.sitting = sitting;
	}
	
	/**
	 * Returns the collar color.
	 * 
	 * @since 5.1.1
	 * @return the collar color
	 */
	public DyeColor getCollarColor() {
		return collarColor;
	}
	
	/**
	 * Sets the collar color.
	 * 
	 * @since 5.1.1
	 * @param collarColor the collar color
	 */
	public void setCollarColor(DyeColor collarColor) {
		this.collarColor = collarColor;
	}
	
	/**
	 * Indicates whether the wolf is tamed.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the wolf is tamed
	 */
	public boolean isTamed() {
		return state.equals(State.TAMED);
	}
	
	/**
	 * Sets whether the wolf is tamed.
	 * 
	 * @since 3.0.1
	 * @param tamed should the wolf be tamed
	 * @deprecated Replaced by {@link WolfDisguise#setState(State)}.
	 */
	@Deprecated
	public void setTamed(boolean tamed) {
		if(tamed)
			state = State.TAMED;
		else
			state = State.NORMAL;
	}
	
	/**
	 * Indicates whether the wolf is angry.
	 * 
	 * @since 3.0.1
	 * @return <code>true</code> if the wolf is angry
	 */
	public boolean isAngry() {
		return state.equals(State.ANGRY);
	}
	
	/**
	 * Sets whether the wolf is angry.
	 * 
	 * @since 3.0.1
	 * @param angry should the wolf be angry
	 * @deprecated Replaced by {@link WolfDisguise#setState(State)}.
	 */
	@Deprecated
	public void setAngry(boolean angry) {
		if(angry)
			state = State.ANGRY;
		else
			state = State.NORMAL;
	}
	
	/**
	 * @since 5.7.1
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setState(State state) {
		if(state == null) throw new IllegalArgumentException("State must not be null!");
		this.state = state;
	}
	
	/**
	 * @since 5.7.1
	 */
	public boolean isSitting() {
		return sitting;
	}
	
	/**
	 * @since 5.7.1
	 */
	public void setSitting(boolean sitting) {
		this.sitting = sitting;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format("%s; %s; %s; %s", super.toString(), state.name().toLowerCase(Locale.ENGLISH), collarColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'), sitting ? "sitting" : "not-sitting");
	}
	
	static {
		for(State state : State.values()) {
			Subtypes.registerSubtype(WolfDisguise.class, "setState", state, state.name().toLowerCase(Locale.ENGLISH));
		}
		Subtypes.registerSubtype(WolfDisguise.class, "setState", State.NORMAL, "not-tamed"); // legacy support
		Subtypes.registerSubtype(WolfDisguise.class, "setState", State.NORMAL, "not-angry"); // legacy support
		for(DyeColor collarColor : DyeColor.values()) {
			Subtypes.registerSubtype(WolfDisguise.class, "setCollarColor", collarColor, collarColor.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		Subtypes.registerSubtype(WolfDisguise.class, "setSitting", true, "sitting");
		Subtypes.registerSubtype(WolfDisguise.class, "setSitting", false, "not-sitting");
	}
	
	public enum State {
		ANGRY, TAMED, NORMAL;
	}
	
}