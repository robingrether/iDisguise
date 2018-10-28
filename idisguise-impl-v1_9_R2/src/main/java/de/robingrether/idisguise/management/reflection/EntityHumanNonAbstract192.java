package de.robingrether.idisguise.management.reflection;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.World;

public class EntityHumanNonAbstract192 extends EntityHuman {
	
	public EntityHumanNonAbstract192(World world, GameProfile gameProfile) {
		super(world, gameProfile);
		
		getDataWatcher().set(bq, (byte)0x7f);
	}
	
	public boolean isSpectator() { return false; }
	
	public boolean l_() { return false; }
	
}