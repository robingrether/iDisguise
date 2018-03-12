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
	
	BAT(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityBat", "bat"),
	BLAZE(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityBlaze", "blaze"),
	CAVE_SPIDER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityCaveSpider", "cave_spider", "cave-spider", "cavespider", "blue_spider", "blue-spider", "bluespider", "cave"),
	CHICKEN(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityChicken", "chicken", "chick"),
	COW(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityCow", "cow", "cattle", "ox"),
	CREEPER(Type.MOB, VersionHelper.EARLIEST, CreeperDisguise.class, "EntityCreeper", "creeper"),
	DONKEY(Type.MOB, VersionHelper.EARLIEST, ChestedHorseDisguise.class, "EntityHorseDonkey", "donkey"),
	ELDER_GUARDIAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGuardianElder", "elder_guardian"),
	ENDER_DRAGON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityEnderDragon", "ender_dragon", "dragon", "ender-dragon", "enderdragon"),
	ENDERMAN(Type.MOB, VersionHelper.EARLIEST, EndermanDisguise.class, "EntityEnderman", "enderman", "endermen"),
	ENDERMITE(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityEndermite", "endermite", "mite"),
	EVOKER(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityEvoker", "evoker"),
	GHAST(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGhast", "ghast"),
	GIANT(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGiantZombie", "giant", "giant_zombie", "giant-zombie", "giantzombie"),
	GUARDIAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityGuardian", "guardian"),
	HORSE(Type.MOB, VersionHelper.EARLIEST, StyledHorseDisguise.class, "EntityHorse", "horse"),
	HUSK(Type.MOB, "v1_10_R1", AgeableDisguise.class, "EntityZombieHusk", "husk"),
	ILLUSIONER(Type.MOB, "v1_12_R1", MobDisguise.class, "EntityIllagerIllusioner", "illusioner"),
	IRON_GOLEM(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityIronGolem", "iron_golem", "iron-golem", "irongolem", "golem"),
	LLAMA(Type.MOB, "v1_11_R1", LlamaDisguise.class, "EntityLlama", "llama"),
	MAGMA_CUBE(Type.MOB, VersionHelper.EARLIEST, SizedDisguise.class, "EntityMagmaCube", "magma_cube", "magma-cube", "magmacube", "magma", "magma_slime", "magma-slime", "magmaslime"),
	MULE(Type.MOB, VersionHelper.EARLIEST, ChestedHorseDisguise.class, "EntityHorseMule", "mule"),
	MUSHROOM_COW(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityMushroomCow", "mushroom_cow", "mushroom-cow", "mushroomcow", "mushroom", "mooshroom"),
	OCELOT(Type.MOB, VersionHelper.EARLIEST, OcelotDisguise.class, "EntityOcelot", "ocelot", "cat"),
	PARROT(Type.MOB, "v1_12_R1", ParrotDisguise.class, "EntityParrot", "parrot"),
	PIG(Type.MOB, VersionHelper.EARLIEST, PigDisguise.class, "EntityPig", "pig"),
	PIG_ZOMBIE(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityPigZombie", "pig_zombie", "pig-zombie", "pigzombie", "pigman", "zombie_pigman", "zombie-pigman", "zombiepigman"),
	POLAR_BEAR(Type.MOB, "v1_10_R1", AgeableDisguise.class, "EntityPolarBear", "polar_bear", "polar-bear", "polarbear", "bear"),
	RABBIT(Type.MOB, VersionHelper.EARLIEST, RabbitDisguise.class, "EntityRabbit", "rabbit", "bunny"),
	SHEEP(Type.MOB, VersionHelper.EARLIEST, SheepDisguise.class, "EntitySheep", "sheep"),
	SHULKER(Type.MOB, "v1_9_R1", MobDisguise.class, "EntityShulker", "shulker"),
	SILVERFISH(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySilverfish", "silverfish"),
	SKELETAL_HORSE(Type.MOB, VersionHelper.EARLIEST, HorseDisguise.class, "EntityHorseSkeleton", "skeletal_horse"),
	SKELETON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySkeleton", "skeleton"),
	SLIME(Type.MOB, VersionHelper.EARLIEST, SizedDisguise.class, "EntitySlime", "slime", "cube"),
	SNOWMAN(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySnowman", "snowman", "snow-man", "snow_man", "snow_golem", "snow-golem", "snowgolem"),
	SPIDER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySpider", "spider"),
	SQUID(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySquid", "squid"),
	STRAY(Type.MOB, "v1_10_R1", MobDisguise.class, "EntitySkeletonStray", "stray"),
	UNDEAD_HORSE(Type.MOB, VersionHelper.EARLIEST, HorseDisguise.class, "EntityHorseZombie", "undead_horse"),
	VEX(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityVex", "vex"),
	VILLAGER(Type.MOB, VersionHelper.EARLIEST, VillagerDisguise.class, "EntityVillager", "villager"),
	VINDICATOR(Type.MOB, "v1_11_R1", MobDisguise.class, "EntityVindicator", "vindicator"),
	WITCH(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityWitch", "witch"),
	WITHER(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntityWither", "wither", "witherboss", "wither-boss", "wither_boss"),
	WITHER_SKELETON(Type.MOB, VersionHelper.EARLIEST, MobDisguise.class, "EntitySkeletonWither", "wither_skeleton"),
	WOLF(Type.MOB, VersionHelper.EARLIEST, WolfDisguise.class, "EntityWolf", "wolf", "dog"),
	ZOMBIE(Type.MOB, VersionHelper.EARLIEST, AgeableDisguise.class, "EntityZombie", "zombie"),
	ZOMBIE_VILLAGER(Type.MOB, VersionHelper.EARLIEST, ZombieVillagerDisguise.class, "EntityZombieVillager", "zombie_villager", "infected_villager"),
	
	PLAYER(Type.PLAYER, VersionHelper.EARLIEST, PlayerDisguise.class, "EntityPlayer"),
	
	AREA_EFFECT_CLOUD(Type.OBJECT, "v1_9_R1", AreaEffectCloudDisguise.class, "EntityAreaEffectCloud", "area_effect_cloud"),
	ARMOR_STAND(Type.OBJECT, VersionHelper.EARLIEST, ArmorStandDisguise.class, "EntityArmorStand", "armor_stand", "armor-stand", "armorstand"),
	BOAT(Type.OBJECT, VersionHelper.EARLIEST, ObjectDisguise.class, "EntityBoat", "boat"),
	ENDER_CRYSTAL(Type.OBJECT, VersionHelper.EARLIEST, ObjectDisguise.class, "EntityEnderCrystal", "ender_crystal", "ender-crystal", "endercrystal", "crystal"),
	FALLING_BLOCK(Type.OBJECT, VersionHelper.EARLIEST, FallingBlockDisguise.class, "EntityFallingBlock", "falling_block", "falling-block", "fallingblock", "block"),
	ITEM(Type.OBJECT, VersionHelper.EARLIEST, ItemDisguise.class, "EntityItem", "item", "itemstack", "item-stack", "item_stack"),
	MINECART(Type.OBJECT, VersionHelper.EARLIEST, MinecartDisguise.class, "EntityMinecartRideable", "minecart", "cart");
	
	private final Type type;
	private final String requiredVersion;
	private final Class<? extends Disguise> disguiseClass;
	private final String nmsClass;
	private final String commandArgument;
	
	private DisguiseType(Type type, String requiredVersion, Class<? extends Disguise> disguiseClass, String nmsClass, String... commandArgs) {
		this.type = type;
		this.requiredVersion = requiredVersion;
		this.disguiseClass = disguiseClass;
		this.nmsClass = nmsClass;
		if(commandArgs != null) {
			this.commandArgument = commandArgs.length > 0 ? commandArgs[0] : null;
			for(String argument : commandArgs) {
				if(!Matcher.matcher.containsKey(argument)) {
					Matcher.matcher.put(argument, this);
				}
			}
		} else {
			this.commandArgument = null;
		}
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
	
	/**
	 * Gets the default command argument.<br>
	 * This is printed for every disguise type when you type <em>/disguise help</em>.
	 * 
	 * @since 5.1.1
	 * @return the default command argument or <code>null</code> for {@link DisguiseType#PLAYER}
	 */
	public String getDefaultCommandArgument() {
		return commandArgument;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @since 5.3.1
	 * @return a string representation of the object
	 */
	public String toString() {
		if(getDefaultCommandArgument() != null) {
			return getDefaultCommandArgument();
		} else {
			return name().toLowerCase(Locale.ENGLISH);
		}
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
	 */
	public static class Matcher {
		
		private static final Map<String, DisguiseType> matcher = new ConcurrentHashMap<String, DisguiseType>();
		
		/**
		 * Find a matching {@linkplain DisguiseType} from one of its command arguments.
		 * 
		 * @since 5.1.1
		 * @param string the command argument
		 * @return the matching {@linkplain DisguiseType}, if one is found
		 */
		public static DisguiseType match(String string) {
			return matcher.get(string.toLowerCase(Locale.ENGLISH));
		}
		
	}
	
}