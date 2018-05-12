package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.World;

public class EntityHumanNonAbstract191 extends EntityHuman {
	
	public EntityHumanNonAbstract191(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean l_() { return false; }
	
}