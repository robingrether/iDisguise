package de.robingrether.idisguise.management.player;

import de.robingrether.idisguise.management.PlayerHelper;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class PlayerHelper17 extends PlayerHelper {
	
	public GameProfile getGameProfile(String skinName, String displayName) {
		return new GameProfile("", displayName.length() <= 16 ? displayName : skinName);
	}
	
}