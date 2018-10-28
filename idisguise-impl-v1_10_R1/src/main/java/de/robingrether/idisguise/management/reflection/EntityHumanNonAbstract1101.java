package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.World;

public class EntityHumanNonAbstract1101 extends EntityHuman {
	
	public EntityHumanNonAbstract1101(World world, GameProfile gameProfile) {
		super(world, gameProfile);
		
		getDataWatcher().set(br, (byte)0x7f);
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean z() { return false; }
	
}