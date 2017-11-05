package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.World;

public class EntityHumanNonAbstract1111 extends EntityHuman {
	
	public EntityHumanNonAbstract1111(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean z() { return false; }
	
}