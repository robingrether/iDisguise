package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.World;

public class EntityHumanNonAbstract182 extends EntityHuman {
	
	public EntityHumanNonAbstract182(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean v() { return false; }
	
}