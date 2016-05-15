package de.robingrether.idisguise.disguise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.util.ObjectUtil;

/**
 * This enum contains all types, you can disguise as.
 * 
 * @since 2.1.3
 * @author RobinGrether
 */
public enum DisguiseType {
	
	BAT(Type.MOB, MobDisguise.class, "EntityBat", "bat"),
	BLAZE(Type.MOB, MobDisguise.class, "EntityBlaze", "blaze"),
	CAVE_SPIDER(Type.MOB, MobDisguise.class, "EntityCaveSpider", "cave_spider", "cave-spider", "cavespider", "blue_spider", "blue-spider", "bluespider", "cave"),
	CHICKEN(Type.MOB, AgeableDisguise.class, "EntityChicken", "chicken", "chick"),
	COW(Type.MOB, AgeableDisguise.class, "EntityCow", "cow", "cattle", "ox"),
	CREEPER(Type.MOB, CreeperDisguise.class, "EntityCreeper", "creeper"),
	ENDER_DRAGON(Type.MOB, MobDisguise.class, "EntityEnderDragon", "ender_dragon", "dragon", "ender-dragon", "enderdragon"),
	ENDERMAN(Type.MOB, EndermanDisguise.class, "EntityEnderman", "enderman", "endermen"),
	ENDERMITE(Type.MOB, MobDisguise.class, "EntityEndermite", "endermite", "mite"),
	GHAST(Type.MOB, MobDisguise.class, "EntityGhast", "ghast"),
	GIANT(Type.MOB, MobDisguise.class, "EntityGiantZombie", "giant", "giant_zombie", "giant-zombie", "giantzombie"),
	GUARDIAN(Type.MOB, GuardianDisguise.class, "EntityGuardian", "guardian"),
	HORSE(Type.MOB, HorseDisguise.class, "EntityHorse", "horse"),
	IRON_GOLEM(Type.MOB, MobDisguise.class, "EntityIronGolem", "iron_golem", "iron-golem", "irongolem", "golem"),
	MAGMA_CUBE(Type.MOB, SizedDisguise.class, "EntityMagmaCube", "magma_cube", "magma-cube", "magmacube", "magma", "lava_cube", "lava-cube", "lavacube", "lava", "magma_slime", "magma-slime", "magmaslime", "lava_slime", "lava-slime", "lavaslime"),
	MUSHROOM_COW(Type.MOB, AgeableDisguise.class, "EntityMushroomCow", "mushroom_cow", "mushroom-cow", "mushroomcow", "mushroom", "mooshroom"),
	OCELOT(Type.MOB, OcelotDisguise.class, "EntityOcelot", "ocelot", "cat"),
	PIG(Type.MOB, PigDisguise.class, "EntityPig", "pig"),
	PIG_ZOMBIE(Type.MOB, AgeableDisguise.class, "EntityPigZombie", "pig_zombie", "pig-zombie", "pigzombie", "pigman", "zombie_pigman", "zombie-pigman", "zombiepigman"),
	RABBIT(Type.MOB, RabbitDisguise.class, "EntityRabbit", "rabbit", "bunny"),
	SHEEP(Type.MOB, SheepDisguise.class, "EntitySheep", "sheep"),
	SHULKER(Type.MOB, MobDisguise.class, "EntityShulker", "shulker"),
	SILVERFISH(Type.MOB, MobDisguise.class, "EntitySilverfish", "silverfish"),
	SKELETON(Type.MOB, SkeletonDisguise.class, "EntitySkeleton", "skeleton"),
	SLIME(Type.MOB, SizedDisguise.class, "EntitySlime", "slime", "cube"),
	SNOWMAN(Type.MOB, MobDisguise.class, "EntitySnowman", "snowman", "snow-man", "snow_man", "snow_golem", "snow-golem", "snowgolem"),
	SPIDER(Type.MOB, MobDisguise.class, "EntitySpider", "spider"),
	SQUID(Type.MOB, MobDisguise.class, "EntitySquid", "squid"),
	VILLAGER(Type.MOB, VillagerDisguise.class, "EntityVillager", "villager"),
	WITCH(Type.MOB, MobDisguise.class, "EntityWitch", "witch"),
	WITHER(Type.MOB, MobDisguise.class, "EntityWither", "witherboss", "wither-boss", "wither_boss"),
	WOLF(Type.MOB, WolfDisguise.class, "EntityWolf", "wolf", "dog"),
	ZOMBIE(Type.MOB, ZombieDisguise.class, "EntityZombie", "zombie"),
	
	GHOST(Type.PLAYER, PlayerDisguise.class, "EntityPlayer"),
	PLAYER(Type.PLAYER, PlayerDisguise.class, "EntityPlayer"),
	
	ARMOR_STAND(Type.OBJECT, ArmorStandDisguise.class, "EntityArmorStand", "armor_stand", "armor-stand", "armorstand"),
	BOAT(Type.OBJECT, ObjectDisguise.class, "EntityBoat", "boat"),
	ENDER_CRYSTAL(Type.OBJECT, ObjectDisguise.class, "EntityEnderCrystal", "ender_crystal", "ender-crystal", "endercrystal", "crystal"),
	FALLING_BLOCK(Type.OBJECT, FallingBlockDisguise.class, "EntityFallingBlock", "falling_block", "falling-block", "fallingblock", "block"),
	ITEM(Type.OBJECT, ItemDisguise.class, "EntityItem", "item", "itemstack", "item-stack", "item_stack"),
	MINECART(Type.OBJECT, MinecartDisguise.class, "EntityMinecartRideable", "minecart", "cart");
	
	private final Type type;
	private final Class<? extends Disguise> disguiseClass;
	private final String nmsClass;
	private final String commandArgument;
	
	private DisguiseType(Type type, Class<? extends Disguise> disguiseClass, String nmsClass, String... commandArgs) {
		this.type = type;
		this.disguiseClass = disguiseClass;
		this.nmsClass = nmsClass;
		if(commandArgs != null) {
			this.commandArgument = commandArgs.length > 0 ? commandArgs[0] : null;
			for(String argument : commandArgs) {
				Matcher.matcher.put(argument, this);
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
	 * Creates and returns a new instance of the correspondent disguise class.<br>
	 * This is not supported for {@link DisguiseType#PLAYER} and {@link DisguiseType#GHOST}.
	 * 
	 * @since 5.1.1
	 * @throws UnsupportedOperationException if the type is {@link DisguiseType#PLAYER} and {@link DisguiseType#GHOST}
	 * @return the new instance or <code>null</code>, if the instantiation failed
	 */
	public Disguise newInstance() {
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
	 * Gets the default command argument.<br>
	 * This is printed for every disguise type when you type <em>/disguise help</em>.
	 * 
	 * @since 5.1.1
	 * @return the default command argument or <code>null</code> for player and ghost
	 */
	public String getDefaultCommandArgument() {
		return commandArgument;
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
				if(types.get(pos).getType() != type) {
					types.remove(pos);
				} else {
					pos++;
				}
			}
		}
		DisguiseType randomType = types.get(random.nextInt(types.size()));
		if((!VersionHelper.require1_8() && ObjectUtil.equals(randomType, DisguiseType.ENDERMITE, DisguiseType.GUARDIAN, DisguiseType.RABBIT)) || (!VersionHelper.require1_6() && randomType.equals(DisguiseType.HORSE))) {
			randomType = random(type);
		}
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
		OBJECT
		
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