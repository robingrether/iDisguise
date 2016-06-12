package de.robingrether.idisguise.disguise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.management.VersionHelper;

/**
 * This enum contains all types, you can disguise as.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public enum DisguiseType {
	
	BAT(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityBat", "bat"),
	BLAZE(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityBlaze", "blaze"),
	CAVE_SPIDER(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityCaveSpider", "cave_spider", "cave-spider", "cavespider", "blue_spider", "blue-spider", "bluespider", "cave"),
	CHICKEN(Type.MOB, "v1_5_R2", AgeableDisguise.class, "EntityChicken", "chicken", "chick"),
	COW(Type.MOB, "v1_5_R2", AgeableDisguise.class, "EntityCow", "cow", "cattle", "ox"),
	CREEPER(Type.MOB, "v1_5_R2", CreeperDisguise.class, "EntityCreeper", "creeper"),
	ENDER_DRAGON(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityEnderDragon", "ender_dragon", "dragon", "ender-dragon", "enderdragon"),
	ENDERMAN(Type.MOB, "v1_5_R2", EndermanDisguise.class, "EntityEnderman", "enderman", "endermen"),
	ENDERMITE(Type.MOB, "v1_8_R1", MobDisguise.class, "EntityEndermite", "endermite", "mite"),
	GHAST(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityGhast", "ghast"),
	GIANT(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityGiantZombie", "giant", "giant_zombie", "giant-zombie", "giantzombie"),
	GUARDIAN(Type.MOB, "v1_8_R1", GuardianDisguise.class, "EntityGuardian", "guardian"),
	HORSE(Type.MOB, "v1_6_R1", HorseDisguise.class, "EntityHorse", "horse"),
	IRON_GOLEM(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityIronGolem", "iron_golem", "iron-golem", "irongolem", "golem"),
	MAGMA_CUBE(Type.MOB, "v1_5_R2", SizedDisguise.class, "EntityMagmaCube", "magma_cube", "magma-cube", "magmacube", "magma", "lava_cube", "lava-cube", "lavacube", "lava", "magma_slime", "magma-slime", "magmaslime", "lava_slime", "lava-slime", "lavaslime"),
	MUSHROOM_COW(Type.MOB, "v1_5_R2", AgeableDisguise.class, "EntityMushroomCow", "mushroom_cow", "mushroom-cow", "mushroomcow", "mushroom", "mooshroom"),
	OCELOT(Type.MOB, "v1_5_R2", OcelotDisguise.class, "EntityOcelot", "ocelot", "cat"),
	PIG(Type.MOB, "v1_5_R2", PigDisguise.class, "EntityPig", "pig"),
	PIG_ZOMBIE(Type.MOB, "v1_5_R2", AgeableDisguise.class, "EntityPigZombie", "pig_zombie", "pig-zombie", "pigzombie", "pigman", "zombie_pigman", "zombie-pigman", "zombiepigman"),
	RABBIT(Type.MOB, "v1_8_R1", RabbitDisguise.class, "EntityRabbit", "rabbit", "bunny"),
	SHEEP(Type.MOB, "v1_5_R2", SheepDisguise.class, "EntitySheep", "sheep"),
	SHULKER(Type.MOB, "v1_9_R1", MobDisguise.class, "EntityShulker", "shulker"),
	SILVERFISH(Type.MOB, "v1_5_R2", MobDisguise.class, "EntitySilverfish", "silverfish"),
	SKELETON(Type.MOB, "v1_5_R2", SkeletonDisguise.class, "EntitySkeleton", "skeleton"),
	SLIME(Type.MOB, "v1_5_R2", SizedDisguise.class, "EntitySlime", "slime", "cube"),
	SNOWMAN(Type.MOB, "v1_5_R2", MobDisguise.class, "EntitySnowman", "snowman", "snow-man", "snow_man", "snow_golem", "snow-golem", "snowgolem"),
	SPIDER(Type.MOB, "v1_5_R2", MobDisguise.class, "EntitySpider", "spider"),
	SQUID(Type.MOB, "v1_5_R2", MobDisguise.class, "EntitySquid", "squid"),
	VILLAGER(Type.MOB, "v1_5_R2", VillagerDisguise.class, "EntityVillager", "villager"),
	WITCH(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityWitch", "witch"),
	WITHER(Type.MOB, "v1_5_R2", MobDisguise.class, "EntityWither", "witherboss", "wither-boss", "wither_boss"),
	WOLF(Type.MOB, "v1_5_R2", WolfDisguise.class, "EntityWolf", "wolf", "dog"),
	ZOMBIE(Type.MOB, "v1_5_R2", ZombieDisguise.class, "EntityZombie", "zombie"),
	
	GHOST(Type.PLAYER, "v1_5_R2", PlayerDisguise.class, "EntityPlayer"),
	PLAYER(Type.PLAYER, "v1_5_R2", PlayerDisguise.class, "EntityPlayer"),
	
	ARMOR_STAND(Type.OBJECT, "v1_8_R1", ArmorStandDisguise.class, "EntityArmorStand", "armor_stand", "armor-stand", "armorstand"),
	BOAT(Type.OBJECT, "v1_5_R2", ObjectDisguise.class, "EntityBoat", "boat"),
	ENDER_CRYSTAL(Type.OBJECT, "v1_5_R2", ObjectDisguise.class, "EntityEnderCrystal", "ender_crystal", "ender-crystal", "endercrystal", "crystal"),
	FALLING_BLOCK(Type.OBJECT, "v1_5_R2", FallingBlockDisguise.class, "EntityFallingBlock", "falling_block", "falling-block", "fallingblock", "block"),
	ITEM(Type.OBJECT, "v1_5_R2", ItemDisguise.class, "EntityItem", "item", "itemstack", "item-stack", "item_stack"),
	MINECART(Type.OBJECT, "v1_5_R2", MinecartDisguise.class, "EntityMinecartRideable", "minecart", "cart");
	
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
	 * This is not supported for {@link DisguiseType#PLAYER} and {@link DisguiseType#GHOST}.
	 * 
	 * @since 5.1.1
	 * @throws UnsupportedOperationException if the type is {@link DisguiseType#PLAYER} and {@link DisguiseType#GHOST}
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
	 * Returns this disguise type's class file inside the given package.
	 * 
	 * @param packageName the package
	 * @return this disguise type's class file, if one exists
	 * @throws ClassNotFoundException if the class file is not found inside the given package.
	 * @since 5.0.1
	 */
	public Class<?> getClass(String packageName) throws ClassNotFoundException {
		return Class.forName(packageName + "." + nmsClass);
	}
	
	/**
	 * Gets the default command argument.<br>
	 * This is printed for every disguise type when you type <em>/disguise help</em>.
	 * 
	 * @since 5.1.1
	 * @return the default command argument or <code>null</code> for player and ghost
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