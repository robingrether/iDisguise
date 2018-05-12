package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.World;

public class EntityHumanNonAbstract181 extends EntityHuman {
	
	public EntityHumanNonAbstract181(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean v() { return false; }
	
}