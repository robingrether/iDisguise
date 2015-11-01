package de.robingrether.idisguise.sound;

import de.robingrether.idisguise.disguise.MobDisguise;

public class LivingSounds extends Sounds {
	
	public String death(MobDisguise disguise) {
		return "game.neutral.die";
	}
	
	public String fallBig(MobDisguise disguise) {
		return "game.neutral.hurt.fall.big";
	}
	
	public String fallSmall(MobDisguise disguise) {
		return "game.neutral.hurt.fall.small";
	}
	
	public String hit(MobDisguise disguise) {
		return "game.neutral.hurt";
	}
	
	public String splash(MobDisguise disguise) {
		return "game.neutral.swim.splash";
	}
	
	public String swim(MobDisguise disguise) {
		return "game.neutral.swim";
	}
	
}