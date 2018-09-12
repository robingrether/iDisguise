package de.robingrether.idisguise.disguise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.EntityType;

import static de.robingrether.idisguise.management.VersionHelper.*;

/**
 * This enum contains all types, you can disguise as.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public enum DisguiseType {
	
	BAT(Type.MOB, EARLIEST, MobDisguise.class, "EntityBat"),
	BLAZE(Type.MOB, EARLIEST, MobDisguise.class, "EntityBlaze", "MHF_Blaze"),
	CAVE_SPIDER(Type.MOB, EARLIEST, MobDisguise.class, "EntityCaveSpider", "MHF_CaveSpider"),
	CHICKEN(Type.MOB, EARLIEST, AgeableDisguise.class, "EntityChicken", "MHF_Chicken"),
	COD(Type.MOB, v1_13, MobDisguise.class, "EntityCod"),
	COW(Type.MOB, EARLIEST, AgeableDisguise.class, "EntityCow", "MHF_Cow"),
	CREEPER(Type.MOB, EARLIEST, CreeperDisguise.class, "EntityCreeper", "Schmusini"),
	DOLPHIN(Type.MOB, v1_13, MobDisguise.class, "EntityDolphin"),
	DONKEY(Type.MOB, EARLIEST, ChestedHorseDisguise.class, "EntityHorseDonkey"),
	DROWNED(Type.MOB, v1_13, AgeableDisguise.class, "EntityDrowned", "MHF_Drowned"),
	ELDER_GUARDIAN(Type.MOB, EARLIEST, MobDisguise.class, "EntityGuardianElder", "MHF_EGuardian"),
	ENDER_DRAGON(Type.MOB, EARLIEST, MobDisguise.class, "EntityEnderDragon", "MHF_EnderDragon"),
	ENDERMAN(Type.MOB, EARLIEST, EndermanDisguise.class, "EntityEnderman", "MHF_Enderman"),
	ENDERMITE(Type.MOB, EARLIEST, MobDisguise.class, "EntityEndermite", "MHF_Endermite"),
	EVOKER(Type.MOB, v1_11, MobDisguise.class, "EntityEvoker", "MHF_Illager"),
	GHAST(Type.MOB, EARLIEST, MobDisguise.class, "EntityGhast", "MHF_Ghast"),
	GIANT(Type.MOB, EARLIEST, MobDisguise.class, "EntityGiantZombie"),
	GUARDIAN(Type.MOB, EARLIEST, MobDisguise.class, "EntityGuardian", "MHF_Guardian"),
	HORSE(Type.MOB, EARLIEST, StyledHorseDisguise.class, "EntityHorse"),
	HUSK(Type.MOB, v1_10, AgeableDisguise.class, "EntityZombieHusk"),
	ILLUSIONER(Type.MOB, v1_12, MobDisguise.class, "EntityIllagerIllusioner"),
	IRON_GOLEM(Type.MOB, EARLIEST, MobDisguise.class, "EntityIronGolem", "MHF_Golem"),
	LLAMA(Type.MOB, v1_11, LlamaDisguise.class, "EntityLlama"),
	MAGMA_CUBE(Type.MOB, EARLIEST, SizedDisguise.class, "EntityMagmaCube", "MHF_LavaSlime"),
	MULE(Type.MOB, EARLIEST, ChestedHorseDisguise.class, "EntityHorseMule"),
	MUSHROOM_COW(Type.MOB, EARLIEST, AgeableDisguise.class, "EntityMushroomCow", "MHF_MushroomCow"),
	OCELOT(Type.MOB, EARLIEST, OcelotDisguise.class, "EntityOcelot", "MHF_Ocelot"),
	PARROT(Type.MOB, v1_12, ParrotDisguise.class, "EntityParrot", "MHF_Parrot"),
	PHANTOM(Type.MOB, v1_13, SizedDisguise.class, "EntityPhantom"),
	PIG(Type.MOB, EARLIEST, PigDisguise.class, "EntityPig", "MHF_Pig"),
	PIG_ZOMBIE(Type.MOB, EARLIEST, AgeableDisguise.class, "EntityPigZombie", "MHF_PigZombie"),
	POLAR_BEAR(Type.MOB, v1_10, AgeableDisguise.class, "EntityPolarBear"),
	PUFFERFISH(Type.MOB, v1_13, PufferfishDisguise.class, "EntityPufferFish", "MHF_PufferFish"),
	RABBIT(Type.MOB, EARLIEST, RabbitDisguise.class, "EntityRabbit", "MHF_Rabbit"),
	SALMON(Type.MOB, v1_13, MobDisguise.class, "EntitySalmon"),
	SHEEP(Type.MOB, EARLIEST, SheepDisguise.class, "EntitySheep", "MHF_Sheep"),
	SHULKER(Type.MOB, v1_9, MobDisguise.class, "EntityShulker", "MHF_Shulker"),
	SILVERFISH(Type.MOB, EARLIEST, MobDisguise.class, "EntitySilverfish"),
	SKELETAL_HORSE(Type.MOB, EARLIEST, HorseDisguise.class, "EntityHorseSkeleton"),
	SKELETON(Type.MOB, EARLIEST, MobDisguise.class, "EntitySkeleton", "MHF_Skeleton"),
	SLIME(Type.MOB, EARLIEST, SizedDisguise.class, "EntitySlime", "MHF_Slime"),
	SNOWMAN(Type.MOB, EARLIEST, MobDisguise.class, "EntitySnowman", "MHF_SnowGolem"),
	SPIDER(Type.MOB, EARLIEST, MobDisguise.class, "EntitySpider", "MHF_Spider"),
	SQUID(Type.MOB, EARLIEST, MobDisguise.class, "EntitySquid", "MHF_Squid"),
	STRAY(Type.MOB, v1_10, MobDisguise.class, "EntitySkeletonStray", "MHF_Stray"),
	TROPICAL_FISH(Type.MOB, v1_13, TropicalFishDisguise.class, "EntityTropicalFish"),
	TURTLE(Type.MOB, v1_13, AgeableDisguise.class, "EntityTurtle", "MHF_Turtle"),
	UNDEAD_HORSE(Type.MOB, EARLIEST, HorseDisguise.class, "EntityHorseZombie"),
	VEX(Type.MOB, v1_11, MobDisguise.class, "EntityVex", "MHF_Vex"),
	VILLAGER(Type.MOB, EARLIEST, VillagerDisguise.class, "EntityVillager", "MHF_Villager"),
	VINDICATOR(Type.MOB, v1_11, MobDisguise.class, "EntityVindicator"),
	WITCH(Type.MOB, EARLIEST, MobDisguise.class, "EntityWitch", "MHF_Witch"),
	WITHER(Type.MOB, EARLIEST, MobDisguise.class, "EntityWither", "MHF_Wither"),
	WITHER_SKELETON(Type.MOB, EARLIEST, MobDisguise.class, "EntitySkeletonWither", "MHF_WSkeleton"),
	WOLF(Type.MOB, EARLIEST, WolfDisguise.class, "EntityWolf", "MHF_Wolf"),
	ZOMBIE(Type.MOB, EARLIEST, AgeableDisguise.class, "EntityZombie", "RobinGrether"),
	ZOMBIE_VILLAGER(Type.MOB, EARLIEST, ZombieVillagerDisguise.class, "EntityZombieVillager"),
	
	PLAYER(Type.PLAYER, EARLIEST, PlayerDisguise.class, "EntityPlayer"),
	
	AREA_EFFECT_CLOUD(Type.OBJECT, v1_9, AreaEffectCloudDisguise.class, "EntityAreaEffectCloud"),
	ARMOR_STAND(Type.OBJECT, EARLIEST, ArmorStandDisguise.class, "EntityArmorStand"),
	BOAT(Type.OBJECT, EARLIEST, BoatDisguise.class, "EntityBoat"),
	ENDER_CRYSTAL(Type.OBJECT, EARLIEST, ObjectDisguise.class, "EntityEnderCrystal"),
	FALLING_BLOCK(Type.OBJECT, EARLIEST, FallingBlockDisguise.class, "EntityFallingBlock"),
	ITEM(Type.OBJECT, EARLIEST, ItemDisguise.class, "EntityItem"),
	MINECART(Type.OBJECT, EARLIEST, MinecartDisguise.class, "EntityMinecartRideable");
	
	private final Type type;
	private final String requiredVersion;
	private final Class<? extends Disguise> disguiseClass;
	private final String nmsClass;
	private final String mhfSkin;
	private String translation;
	
	private DisguiseType(Type type, String requiredVersion, Class<? extends Disguise> disguiseClass, String nmsClass) {
		this(type, requiredVersion, disguiseClass, nmsClass, null);
	}
	
	private DisguiseType(Type type, String requiredVersion, Class<? extends Disguise> disguiseClass, String nmsClass, String mhfSkin) {
		this.type = type;
		this.requiredVersion = requiredVersion;
		this.disguiseClass = disguiseClass;
		this.nmsClass = nmsClass;
		this.mhfSkin = mhfSkin;
		this.translation = getDefaultCommandArgument();
	}
	
	/**
	 * Checks whether the type is a mob.
	 * 
	 * @since 2.1.3
	 * @return true if it's a mob, false if not
	 */
	public boolean isMob() {
		return type == Type.MOB;
	}
	
	/**
	 * Checks whether the type is a player.
	 * 
	 * @since 2.1.3
	 * @return true if it's a player, false if not
	 */
	public boolean isPlayer() {
		return type == Type.PLAYER;
	}
	
	/**
	 * Checks whether the type is an object.
	 * 
	 * @since 2.1.3
	 * @return true if it's an object, false if not
	 */
	public boolean isObject() {
		return type == Type.OBJECT;
	}
	
	/**
	 * Returns the type of this disguise type.
	 * 
	 * @since 2.2.2
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Indicates whether this disguise type is available on this server.
	 * 
	 * @since 5.3.1
	 * @return <code>true</code>, if and only if this disguise type is available
	 */
	public boolean isAvailable() {
		return requireVersion(requiredVersion);
	}
	
	/**
	 * Returns the class that handles the subtypes for this disguise type.
	 * 
	 * @since 5.3.1
	 * @return the disguise class that handles the subtypes for this disguise type
	 */
	public Class<? extends Disguise> getDisguiseClass() {
		return disguiseClass;
	}
	
	/**
	 * Creates and returns a new instance of the correspondent disguise class.<br>
	 * This is not supported for {@link DisguiseType#PLAYER}.
	 * 
	 * @since 5.1.1
	 * @throws UnsupportedOperationException if the type is {@link DisguiseType#PLAYER}
	 * @return the new instance or <code>null</code>, if the instantiation failed
	 */
	public Disguise newInstance() {
		if(!isAvailable()) {
			throw new OutdatedServerException();
		}
		try {
			return disguiseClass.getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
		} catch(NoSuchMethodException e) {
			try {
				return disguiseClass.getDeclaredConstructor(DisguiseType.class).newInstance(this);
			} catch(NoSuchMethodException e2) {
				throw new UnsupportedOperationException();
			} catch(Exception e2) {
				return null;
			}
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * @since 5.5.1
	 */
	public String getNMSClass() {
		return nmsClass;
	}
	
	/**
	 * @since 5.8.1
	 */
	public String getMHFSkin() {
		return mhfSkin;
	}
	
	private static final Map<String, DisguiseType> commandMatcher = new ConcurrentHashMap<String, DisguiseType>();
	
	static {
		for(DisguiseType type : values()) {
			commandMatcher.put(type.getDefaultCommandArgument(), type);
		}
	}
	
	/**
	 * Gets the default (english) command argument.<br>
	 * Calling this method is similar to: <code>type.name().toLowerCase(Locale.ENGLISH).replace('_', '-')</code>
	 * 
	 * @since 5.1.1
	 */
	public String getDefaultCommandArgument() {
		return name().toLowerCase(Locale.ENGLISH).replace('_', '-');
	}
	
	/**
	 * Gets the custom (probably translated) command argument.<br>
	 * The custom command argument may be set via {@link #setCustomCommandArgument(String)}.<br>
	 * This command argument is shown whenever <em>/disguise help</em> is run.
	 * 
	 * @since 5.7.1
	 */
	public String getCustomCommandArgument() {
		return translation;
	}
	
	/**
	 * Sets the custom command argument.<br>
	 * This command argument is shown whenever <em>/disguise help</em> is executed.
	 * 
	 * @since 5.7.1
	 * @return <code>false</code> in case the given command argument is already registered
	 */
	public boolean setCustomCommandArgument(String customCommandArgument) {
		customCommandArgument = customCommandArgument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		if(commandMatcher.containsKey(customCommandArgument)) return false;
		translation = customCommandArgument;
		commandMatcher.put(customCommandArgument, this);
		return true;
	}
	
	/**
	 * Adds another custom command argument.<br>
	 * This command argument will <strong>NOT</strong> be shown when <em>/disguise help</em> is executed.
	 * 
	 * @since 5.7.1
	 * @return <code>false</code> in case the given command argument is already registered
	 */
	public boolean addCustomCommandArgument(String customCommandArgument) {
		customCommandArgument = customCommandArgument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		if(commandMatcher.containsKey(customCommandArgument)) return false;
		commandMatcher.put(customCommandArgument, this);
		return true;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @since 5.3.1
	 * @return a string representation of the object
	 */
	public String toString() {
		return getDefaultCommandArgument();
	}
	
	/**
	 * Match a disguise type from one of its registered command arguments.<br>
	 * Notice: This operation is case-insensitive.
	 * 
	 * @since 5.7.1
	 * @return the disguise type that belongs to the given command argument or <code>null</code> if the given command argument is not registered
	 */
	public static DisguiseType fromString(String commandArgument) {
		commandArgument = commandArgument.toLowerCase(Locale.ENGLISH).replace('_', '-');
		return commandMatcher.get(commandArgument);
	}
	
	/**
	 * Match a disguise type from the equivalent entity type.
	 * 
	 * @since 5.7.1
	 * @return the equivalent disguise type or <code>null</code> if there is no equivalent
	 */
	public static DisguiseType fromEntityType(EntityType entityType) {
		try {
			return DisguiseType.valueOf(entityType.name().replace("DROPPED_", "").replace("SKELETON_", "SKELETAL_").replace("ZOMBIE_H", "UNDEAD_H").replaceAll("MINECART.*", "MINECART"));
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	private static final Random random = new Random();
	
	/**
	 * Creates a random disguise type.
	 * 
	 * @since 2.2.2
	 * @param type the type the disguise type should be, if this is null the type is ignored
	 * @return a random disguise type
	 */
	public static DisguiseType random(Type type) {
		List<DisguiseType> types = new ArrayList<DisguiseType>(Arrays.asList(values()));
		if(type != null) {
			int pos = 0;
			while(pos < types.size()) {
				if(types.get(pos).getType() != type || !types.get(pos).isAvailable()) {
					types.remove(pos);
				} else {
					pos++;
				}
			}
		}
		DisguiseType randomType = types.get(random.nextInt(types.size()));
		return randomType;
	}
	
	/**
	 * The type a disguise can be: mob, player, object.
	 * 
	 * @since 2.1.3
	 * @author RobinGrether
	 */
	public enum Type {
		
		MOB,
		PLAYER,
		OBJECT;
		
	}
	
}