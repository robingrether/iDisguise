package de.robingrether.idisguise.disguise;

import org.bukkit.Location;

import net.minecraft.server.v1_8_R3.EntityAgeable;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityEndermite;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntityRabbit;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySilverfish;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntitySquid;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;

/**
 * Represents a disguise as a mob.
 * 
 * @since 2.1.3
 * @author Robingrether
 */
public class MobDisguise extends Disguise {
	
	private static final long serialVersionUID = -8536172774722123370L;
	protected boolean adult;
	protected String customName = null;
	
	/**
	 * Creates an instance.
	 * 
	 * @since 2.1.3
	 * @param type the type to disguise as
	 * @param adult should the disguise be an adult
	 */
	public MobDisguise(DisguiseType type, boolean adult) {
		super(type);
		if(!type.isMob()) {
			throw new IllegalArgumentException("DisguiseType must be a mob");
		}
		this.adult = adult;
	}
	
	/**
	 * Checks whether the disguise is an adult.
	 * 
	 * @since 2.1.3
	 * @return true if it's an adult, false if not
	 */
	public boolean isAdult() {
		return this.adult;
	}
	
	/**
	 * Sets if the disguise is an adult.
	 * 
	 * @since 2.1.3
	 * @param adult should the disguise be an adult
	 */
	public void setAdult(boolean adult) {
		this.adult = adult;
	}
	
	/**
	 * Gets the custom name of this entity.<br />
	 * The default value is <code>null</code>.
	 * 
	 * @since 3.0.1
	 * @return the custom name
	 */
	public String getCustomName() {
		return customName;
	}
	
	/**
	 * Sets the custom name of this entity.<br />
	 * The default value is <code>null</code>.
	 * 
	 * @since 3.0.1
	 * @param customName the custom name
	 */
	public void setCustomName(String customName) {
		if(customName != null && customName.length() > 64) {
			customName = customName.substring(0, 64);
		}
		this.customName = customName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MobDisguise clone() {
		MobDisguise clone = new MobDisguise(type, adult);
		clone.setCustomName(customName);
		return clone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		return super.equals(object) && object instanceof MobDisguise && ((MobDisguise)object).adult == adult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityInsentient getEntity(World world, Location location, int id) {
		EntityInsentient entity;
		switch (this.type) {
			case BAT:
				entity = new EntityBat(world);
				break;
			case BLAZE:
				entity = new EntityBlaze(world);
				break;
			case CAVE_SPIDER:
				entity = new EntityCaveSpider(world);
				break;
			case CHICKEN:
				entity = new EntityChicken(world);
				break;
			case COW:
				entity = new EntityCow(world);
				break;
			case CREEPER:
				entity = new EntityCreeper(world);
				break;
			case ENDER_DRAGON:
				entity = new EntityEnderDragon(world);
				break;
			case ENDERMAN:
				entity = new EntityEnderman(world);
				break;
			case ENDERMITE:
				entity = new EntityEndermite(world);
				break;
			case GHAST:
				entity = new EntityGhast(world);
				break;
			case GIANT:
				entity = new EntityGiantZombie(world);
				break;
			case GUARDIAN:
				entity = new EntityGuardian(world);
				break;
			case HORSE:
				entity = new EntityHorse(world);
				break;
			case IRON_GOLEM:
				entity = new EntityIronGolem(world);
				break;
			case MAGMA_CUBE:
				entity = new EntityMagmaCube(world);
				break;
			case MUSHROOM_COW:
				entity = new EntityMushroomCow(world);
				break;
			case OCELOT:
				entity = new EntityOcelot(world);
				break;
			case PIG:
				entity = new EntityPig(world);
				break;
			case PIG_ZOMBIE:
				entity = new EntityPigZombie(world);
				break;
			case RABBIT:
				entity = new EntityRabbit(world);
				break;
			case SHEEP:
				entity = new EntitySheep(world);
				break;
			case SILVERFISH:
				entity = new EntitySilverfish(world);
				break;
			case SKELETON:
				entity = new EntitySkeleton(world);
				break;
			case SLIME:
				entity = new EntitySlime(world);
				break;
			case SNOWMAN:
				entity = new EntitySnowman(world);
				break;
			case SPIDER:
				entity = new EntitySpider(world);
				break;
			case SQUID:
				entity = new EntitySquid(world);
				break;
			case VILLAGER:
				entity = new EntityVillager(world);
				break;
			case WITCH:
				entity = new EntityWitch(world);
				break;
			case WITHER:
				entity = new EntityWither(world);
				break;
			case WOLF:
				entity = new EntityWolf(world);
				break;
			case ZOMBIE:
				entity = new EntityZombie(world);
				break;
			default:
				entity = null;
				break;
		}
		if(entity instanceof EntityAgeable && !adult) {
			((EntityAgeable)entity).setAge(-24000);
		} else if(entity instanceof EntityZombie && !adult) {
			((EntityZombie)entity).setBaby(true);
		}
		if(customName != null && !customName.isEmpty()) {
			entity.setCustomName(customName);
			entity.setCustomNameVisible(true);
		} else {
			entity.setCustomNameVisible(false);
		}
		entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.d(id);
		return entity;
	}
	
}