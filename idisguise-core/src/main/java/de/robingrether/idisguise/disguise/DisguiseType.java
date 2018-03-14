package de.robingrether.idisguise.disguise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.EntityType;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * This enum contains all types, you can disguise as.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public enum DisguiseType {
	
	BAT(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityBat"),
	BLAZE(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityBlaze"),
	CAVE_SPIDER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityCaveSpider"),
	CHICKEN(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityChicken"),
	COW(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityCow"),
	CREEPER(Type.MOB, VersionHelper.EARLIEST, CreeperDisguise.class, "EntityCreeper"),
	DONKEY(Type.MOB, VersionHelper.EARLIEST, ChestedHorseDisguise.class, "EntityHorseDonkey"),
	ELDER_GUARDIAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGuardianElder"),
	ENDER_DRAGON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityEnderDragon"),
	ENDERMAN(Type.MOB, VersionHelper.EARLIEST, EndermanDisguise.class, "EntityEnderman"),
	ENDERMITE(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityEndermite"),
	EVOKER(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityEvoker"),
	GHAST(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGhast"),
	GIANT(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGiantZombie"),
	GUARDIAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGuardian"),
	HORSE(Type.MOB, VersionHelper.EARLIEST, StyledHorseDisguise.class, "EntityHorse"),
	HUSK(Type.MOB, "v1_10_R1", AgeableDisguise.class, "EntityZombieHusk"),
	ILLUSIONER(Type.MOB, "v1_12_R1", MobDisguise.class, "EntityIllagerIllusioner"),
	IRON_GOLEM(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityIronGolem"),
	LLAMA(Type.MOB, "v1_11_R1", LlamaDisguise.class, "EntityLlama"),
	MAGMA_CUBE(Type.MOB, VersionHelper.EARLIEST, SizedDisguise.class, "EntityMagmaCube"),
	MULE(Type.MOB, VersionHelper.EARLIEST, ChestedHorseDisguise.class, "EntityHorseMule"),
	MUSHROOM_COW(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityMushroomCow"),
	OCELOT(Type.MOB, VersionHelper.EARLIEST, OcelotDisguise.class, "EntityOcelot"),
	PARROT(Type.MOB, "v1_12_R1", ParrotDisguise.class, "EntityParrot"),
	PIG(Type.MOB, VersionHelper.EARLIEST, PigDisguise.class, "EntityPig"),
	PIG_ZOMBIE(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityPigZombie"),
	POLAR_BEAR(Type.MOB, "v1_10_R1", AgeableDisguise.class, "EntityPolarBear"),
	RABBIT(Type.MOB, VersionHelper.EARLIEST, RabbitDisguise.class, "EntityRabbit"),
	SHEEP(Type.MOB, VersionHelper.EARLIEST, SheepDisguise.class, "EntitySheep"),
	SHULKER(Type.MOB, "v1_9_R1", MobDisguise.class, "EntityShulker"),
	SILVERFISH(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySilverfish"),
	SKELETAL_HORSE(Type.MOB, VersionHelper.EARLIEST, HorseDisguise.class, "EntityHorseSkeleton"),
	SKELETON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySkeleton"),
	SLIME(Type.MOB, VersionHelper.EARLIEST, SizedDisguise.class, "EntitySlime"),
	SNOWMAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySnowman"),
	SPIDER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySpider"),
	SQUID(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySquid"),
	STRAY(Type.MOB, "v1_10_R1", MobDisguise.class, "EntitySkeletonStray"),
	UNDEAD_HORSE(Type.MOB, VersionHelper.EARLIEST, HorseDisguise.class, "EntityHorseZombie"),
	VEX(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityVex"),
	VILLAGER(Type.MOB, VersionHelper.EARLIEST, VillagerDisguise.class, "EntityVillager"),
	VINDICATOR(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityVindicator"),
	WITCH(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityWitch"),
	WITHER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityWither"),
	WITHER_SKELETON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySkeletonWither"),
	WOLF(Type.MOB, VersionHelper.EARLIEST, WolfDisguise.class, "EntityWolf"),
	ZOMBIE(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityZombie"),
	ZOMBIE_VILLAGER(Type.MOB, VersionHelper.EARLIEST, ZombieVillagerDisguise.class, "EntityZombieVillager"),
	
	PLAYER(Type.PLAYER, VersionHelper.EARLIEST, PlayerDisguise.class, "EntityPlayer"),
	
	AREA_EFFECT_CLOUD(Type.OBJECT, "v1_9_R1", AreaEffectCloudDisguise.class, "EntityAreaEffectCloud"),
	ARMOR_STAND(Type.OBJECT, VersionHelper.EARLIEST, ArmorStandDisguise.class, "EntityArmorStand"),
	BOAT(Type.OBJECT, VersionHelper.EARLIEST, ObjectDisguise.class, "EntityBoat"),
	ENDER_CRYSTAL(Type.OBJECT, VersionHelper.EARLIEST, ObjectDisguise.class, "EntityEnderCrystal"),
	FALLING_BLOCK(Type.OBJECT, VersionHelper.EARLIEST, FallingBlockDisguise.class, "EntityFallingBlock"),
	ITEM(Type.OBJECT, VersionHelper.EARLIEST, ItemDisguise.class, "EntityItem"),
	MINECART(Type.OBJECT, VersionHelper.EARLIEST, MinecartDisguise.class, "EntityMinecartRideable");
	
	private final Type type;
	private final String requiredVersion;
	private final Class<? extends Disguise> disguiseClass;
	private final String nmsClass;
	private String translation;
	
	private DisguiseType(Type type, String requiredVersion, Class<? extends Disguise> disguiseClass, String nmsClass) {
		this.type = type;
		this.requiredVersion = requiredVersion;
		this.disguiseClass = disguiseClass;
		this.nmsClass = nmsClass;
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
		return VersionHelper.requireVersion(requiredVersion);
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
	public boolean setCustomCommandArgument(String customCommandArgument) { // TODO: adapt iDisguise.class and Language.class
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
	
	/**
	 * This class provides the capability to match a {@linkplain DisguiseType} from one of its command arguments.
	 * 
	 * @since 5.1.1
	 * @author RobinGrether
	 * @deprecated Replaced by {@link DisguiseType#fromString(String)}.
	 */
	@Deprecated
	public static class Matcher {
		
		/**
		 * Find a matching {@linkplain DisguiseType} from one of its command arguments.
		 * 
		 * @since 5.1.1
		 * @param string the command argument
		 * @return the matching {@linkplain DisguiseType}, if one is found
		 * @deprecated Replaced by {@link DisguiseType#fromString(String)}.
		 */
		@Deprecated
		public static DisguiseType match(String string) {
			return commandMatcher.get(string.toLowerCase(Locale.ENGLISH).replace('_', '-'));
		}
		
	}
	
}