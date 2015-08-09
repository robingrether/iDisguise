package de.robingrether.idisguise.disguise;

import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.World;

public class GuardianDisguise extends MobDisguise {
	
	private static final long serialVersionUID = 8098510434769803362L;
	private boolean isElder;
	
	public GuardianDisguise() {
		this(false);
	}
	
	public GuardianDisguise(boolean isElder) {
		super(DisguiseType.GUARDIAN, true);
		this.isElder = isElder;
	}
	
	public boolean isElder() {
		return isElder;
	}
	
	public void setElder(boolean isElder) {
		this.isElder = isElder;
	}
	
	public GuardianDisguise clone() {
		GuardianDisguise clone = new GuardianDisguise(isElder);
		clone.setCustomName(customName);
		return clone;
	}
	
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof GuardianDisguise && ((GuardianDisguise)object).isElder == isElder;
	}
	
	public EntityGuardian getEntity(World world, Location location, int id) {
		EntityGuardian guardian = (EntityGuardian)super.getEntity(world, location, id);
		guardian.setElder(isElder);
		return guardian;
	}
	
}