package de.robingrether.idisguise.disguise;

import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.entity.Skeleton.SkeletonType;

public class SkeletonDisguise extends MobDisguise {
	
	private static final long serialVersionUID = -5133866912715315405L;
	private SkeletonType skeletonType;
	
	public SkeletonDisguise() {
		this(SkeletonType.NORMAL);
	}
	
	public SkeletonDisguise(SkeletonType skeletonType) {
		super(DisguiseType.SKELETON, true);
		this.skeletonType = skeletonType;
	}
	
	public SkeletonType getSkeletonType() {
		return skeletonType;
	}
	
	public void setSkeletonType(SkeletonType skeletonType) {
		this.skeletonType = skeletonType;
	}
	
	public SkeletonDisguise clone() {
		SkeletonDisguise clone = new SkeletonDisguise(skeletonType);
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof SkeletonDisguise && ((SkeletonDisguise)object).skeletonType == skeletonType;
	}
	
	@SuppressWarnings("deprecation")
	public EntitySkeleton getEntity(World world, Location location, int id) {
		EntitySkeleton skeleton = (EntitySkeleton)super.getEntity(world, location, id);
		skeleton.setSkeletonType(skeletonType.getId());
		return skeleton;
	}
	
}