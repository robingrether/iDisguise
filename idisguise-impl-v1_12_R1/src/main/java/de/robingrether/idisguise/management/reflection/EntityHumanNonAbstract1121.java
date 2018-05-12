package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.World;

public class EntityHumanNonAbstract1121 extends EntityHuman {
	
	public EntityHumanNonAbstract1121(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean z() { return false; }
	
}