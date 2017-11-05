package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;

public class EntityHumanNonAbstract183 extends EntityHuman {
	
	public EntityHumanNonAbstract183(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean isSpectator() { return false; }
	
}