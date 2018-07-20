package de.robingrether.idisguise.disguise;

import java.util.Locale;

public class PufferfishDisguise extends MobDisguise {
	
	private PuffState puffState = PuffState.DEFLATED;
	
	public PufferfishDisguise() {
		super(DisguiseType.PUFFERFISH);
	}
	
	public PuffState getPuffState() {
		return puffState;
	}
	
	public void setPuffState(PuffState puffState) {
		this.puffState = puffState;
	}
	
	public String toString() {
		return String.format("%s; %s", super.toString(), puffState.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
	}
	
	static {
		for(PuffState puffState : PuffState.values()) {
			Subtypes.registerSubtype(PufferfishDisguise.class, "setPuffState", puffState, puffState.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
	}
	
	public enum PuffState {
		DEFLATED, HALF_PUFFED, PUFFED;
	}
	
}