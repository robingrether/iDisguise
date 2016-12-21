package de.robingrether.idisguise.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;

public class Reflection {
	
	public static Class<?> Bukkit_reflect;
	public static Method Bukkit_getOnlinePlayers;
	public static Method Bukkit_getOfflinePlayer;
	
	public static Class<?> GameProfile;
	public static Method GameProfile_getProfileId;
	
	public static Class<?> CraftPlayer;
	public static Method CraftPlayer_getHandle;
	public static Method CraftPlayer_getProfile;
	
	public static Class<?> CraftOfflinePlayer;
	public static Method CraftOfflinePlayer_getProfile;
	
	public static Class<?> CraftChatMessage;
	public static Method CraftChatMessage_fromString;
	
	public static Class<?> EntityPlayer;
	public static Method EntityPlayer_getBukkitEntity;
	public static Field EntityPlayer_playerConnection;
	
	public static Class<?> EntityLiving;
	
	public static Class<?> DataWatcher;
	
	public static Class<?> DataWatcherItem;
	public static Field DataWatcherItem_dataWatcherObject;
	
	public static Class<?> DataWatcherObject;
	public static Method DataWatcherObject_getId;
	
	public static Class<?> PacketPlayInUseEntity;
	public static Field PacketPlayInUseEntity_entityId;
	public static Method PacketPlayInUseEntity_getAction;
	public static Field PacketPlayInUseEntity_action;
	
	public static Class<?> EnumEntityUseAction;
	public static Field EnumEntityUseAction_ATTACK;
	
	public static Class<?> PacketPlayOutNamedEntitySpawn;
	public static Constructor<?> PacketPlayOutNamedEntitySpawn_new;
	public static Field PacketPlayOutNamedEntitySpawn_entityId;
	public static Field PacketPlayOutNamedEntitySpawn_profileId;
	public static Field PacketPlayOutNamedEntitySpawn_gameProfile;
	public static Field PacketPlayOutNamedEntitySpawn_playerName;
	
	public static Class<?> PacketPlayOutSpawnEntityLiving;
	public static Constructor<?> PacketPlayOutSpawnEntityLiving_new;
	public static Field PacketPlayOutSpawnEntityLiving_yaw;
	
	public static Class<?> PacketPlayOutPlayerInfo;
	public static Constructor<?> PacketPlayOutPlayerInfo_new;
	public static Field PacketPlayOutPlayerInfo_action;
	public static Field PacketPlayOutPlayerInfo_playerInfoList;
	public static Field PacketPlayOutPlayerInfo_playerName;
	public static Field PacketPlayOutPlayerInfo_ping;
	public static Field PacketPlayOutPlayerInfo_isOnline;
	
	public static Class<?> PacketPlayOutBed;
	public static Field PacketPlayOutBed_entityId;
	
	public static Class<?> PacketPlayOutAnimation;
	public static Field PacketPlayOutAnimation_entityId;
	public static Field PacketPlayOutAnimation_animationType;
	
	public static Class<?> PacketPlayOutEntityMetadata;
	public static Constructor<?> PacketPlayOutEntityMetadata_new_empty;
	public static Constructor<?> PacketPlayOutEntityMetadata_new_full;
	public static Field PacketPlayOutEntityMetadata_entityId;
	public static Field PacketPlayOutEntityMetadata_metadataList;
	
	public static Class<?> PacketPlayOutEntityLook;
	
	public static Class<?> PacketPlayOutRelEntityMoveLook;
	
	public static Class<?> PacketPlayOutEntity;
	public static Field PacketPlayOutEntity_entityId;
	public static Field PacketPlayOutEntity_deltaX;
	public static Field PacketPlayOutEntity_deltaY;
	public static Field PacketPlayOutEntity_deltaZ;
	public static Field PacketPlayOutEntity_yaw;
	public static Field PacketPlayOutEntity_pitch;
	public static Field PacketPlayOutEntity_isOnGround;
	
	public static Class<?> PacketPlayOutEntityTeleport;
	public static Constructor<?> PacketPlayOutEntityTeleport_new;
	public static Field PacketPlayOutEntityTeleport_entityId;
	public static Field PacketPlayOutEntityTeleport_x;
	public static Field PacketPlayOutEntityTeleport_y;
	public static Field PacketPlayOutEntityTeleport_z;
	public static Field PacketPlayOutEntityTeleport_yaw;
	public static Field PacketPlayOutEntityTeleport_pitch;
	public static Field PacketPlayOutEntityTeleport_isOnGround;
	
	public static Class<?> PacketPlayOutUpdateAttributes;
	public static Field PacketPlayOutUpdateAttributes_entityId;
	
	public static Class<?> PacketPlayOutNamedSoundEffect;
	public static Constructor<?> PacketPlayOutNamedSoundEffect_new;
	public static Field PacketPlayOutNamedSoundEffect_soundEffect;
	public static Field PacketPlayOutNamedSoundEffect_soundCategory;
	public static Field PacketPlayOutNamedSoundEffect_x;
	public static Field PacketPlayOutNamedSoundEffect_y;
	public static Field PacketPlayOutNamedSoundEffect_z;
	public static Field PacketPlayOutNamedSoundEffect_volume;
	public static Field PacketPlayOutNamedSoundEffect_pitch;
	
	public static Class<?> PlayerInfoData;
	public static Constructor<?> PlayerInfoData_new;
	public static Method PlayerInfoData_getProfile;
	public static Method PlayerInfoData_getPing;
	public static Method PlayerInfoData_getGamemode;
	public static Method PlayerInfoData_getDisplayName;
	
	public static Class<?> World;
	public static Method World_findNearbyPlayer;
	
	public static Class<?> Entity;
	public static Field Entity_world;
	public static Method Entity_setLocation;
	public static Method Entity_setEntityId;
	public static Method Entity_getDataWatcher;
	public static Field Entity_entityId;
	
	public static Class<?> EntityInsentient;
	public static Method EntityInsentient_setCustomName;
	public static Method EntityInsentient_setCustomNameVisible;
	
	public static Class<?> EntityAgeable;
	public static Method EntityAgeable_setAge;
	
	public static Class<?> EntitySheep;
	public static Method EntitySheep_setColor;
	
	public static Class<?> EntityWolf;
	public static Method EntityWolf_setCollarColor;
	public static Method EntityWolf_setTamed;
	public static Method EntityWolf_setAngry;
	
	public static Class<?> EnumColor;
	public static Method EnumColor_fromColorIndex;
	
	public static Class<?> EntityCreeper;
	public static Method EntityCreeper_setPowered;
	
	public static Class<?> EntityEnderman;
	public static Method EntityEnderman_setCarried;
	public static Method EntityEnderman_setCarriedId;
	public static Method EntityEnderman_setCarriedBlock;
	public static Method EntityEnderman_setCarriedData;
	
	public static Class<?> Block;
	public static Method Block_getById;
	public static Method Block_fromLegacyData;
	public static Field Block_blocksById;
	
	public static Class<?> EntityGuardian;
	public static Method EntityGuardian_setElder;
	
	public static Class<?> EntityHorse;
	public static Method EntityHorse_setType;
	public static Method EntityHorse_setVariant;
	public static Method EntityHorse_setHasChest;
	public static Field EntityHorse_inventoryChest;
	
	public static Class<?> EntityHorseAbstract;
	public static Field EntityHorseAbstract_inventoryChest;
	
	public static Class<?> EntityHorseChestedAbstract;
	public static Method EntityHorseChestedAbstract_setCarryingChest;
	
	public static Class<?> EnumHorseType;
	public static Method EnumHorseType_fromIndex;
	
	public static Class<?> InventorySubcontainer;
	public static Method InventorySubcontainer_setItem;
	
	public static Class<?> Item;
	public static Method Item_getById;
	public static Field Item_itemsById;
	
	public static Class<?> ItemStack;
	public static Constructor<?> ItemStack_new_Block;
	public static Constructor<?> ItemStack_new_Item;
	
	public static Class<?> CraftItemStack;
	public static Method CraftItemStack_asNMSCopy;
	
	public static Class<?> EntityOcelot;
	public static Method EntityOcelot_setCatType;
	
	public static Class<?> EntityPig;
	public static Method EntityPig_setSaddle;
	
	public static Class<?> EntityRabbit;
	public static Method EntityRabbit_setRabbitType;
	
	public static Class<?> EntitySlime;
	public static Method EntitySlime_setSize;
	
	public static Class<?> EntitySkeleton;
	public static Method EntitySkeleton_setSkeletonType;
	
	public static Class<?> EntityVillager;
	public static Method EntityVillager_setProfession;
	
	public static Class<?> EntityZombie;
	public static Method EntityZombie_setBaby;
	public static Method EntityZombie_setVillager;
	public static Method EntityZombie_setVillagerType;
	
	public static Class<?> EntityZombieVillager;
	public static Method EntityZombieVillager_setProfession;
	
	public static Class<?> EntityBat;
	public static Method EntityBat_setAsleep;
	
	public static Class<?> EntityFallingBlock;
	
	public static Class<?> PacketPlayOutSpawnEntity;
	public static Constructor<?> PacketPlayOutSpawnEntity_new;
	public static Field PacketPlayOutSpawnEntity_x;
	public static Field PacketPlayOutSpawnEntity_y;
	public static Field PacketPlayOutSpawnEntity_z;
	
	public static Class<?> EntityItem;
	public static Method EntityItem_setItemStack;
	
	public static Class<?> EntityMinecartAbstract;
	public static Method EntityMinecartAbstract_setDisplayBlock;
	public static Method EntityMinecartAbstract_setDisplayBlockId;
	public static Method EntityMinecartAbstract_setDisplayBlockData;
	
	public static Class<?> MinecraftKey;
	public static Constructor<?> MinecraftKey_new;
	public static Method MinecraftKey_getName;
	
	public static Class<?> RegistryMaterials;
	public static Method RegistryMaterials_getKey;
	public static Method RegistryMaterials_getValue;
	
	public static Class<?> SoundEffect;
	public static Field SoundEffect_registry;
	
	public static Class<?> MinecraftServer;
	public static Method MinecraftServer_getServer;
	public static Method MinecraftServer_getSessionService;
	
	public static Class<?> MinecraftSessionService;
	public static Method MinecraftSessionService_fillProfileProperties;
	
	public static Class<?> EntityTameableAnimal;
	
	public static Class<?> EntityArmorStand;
	public static Method EntityArmorStand_setArms;
	
	public static Class<?> PacketPlayOutCollect;
	public static Field PacketPlayOutCollect_entityId;
	
	public static Class<?> EnumSkeletonType;
	public static Method EnumSkeletonType_fromIndex;
	
	public static Class<?> EnumZombieType;
	public static Method EnumZombieType_fromIndex;
	
	public static Class<?> EntityLlama;
	public static Method EntityLlama_setVariant;
	
	private static final Pattern basicPattern = Pattern.compile("([A-Za-z0-9_]+)->(C|F|M|N)(.+)");
	private static final Pattern fieldPattern = Pattern.compile("([A-Za-z0-9_]+)\\$(.+)");
	private static final Pattern methodPattern = Pattern.compile("([A-Za-z0-9_]+)\\$([^\\(\\)]+)\\(([^\\(\\)]*)\\)");
	private static final Pattern newPattern = Pattern.compile("([A-Za-z0-9_]+)\\(([^\\(\\)]*)\\)");
	
	public static void init(String file, String nms, String obc) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Reflection.class.getResourceAsStream(file)));
			String line;
			while((line = reader.readLine()) != null) {
				Matcher basicMatcher = basicPattern.matcher(line);
				if(basicMatcher.matches()) {
					try {
						Field field = Reflection.class.getDeclaredField(basicMatcher.group(1));
						char type = basicMatcher.group(2).charAt(0);
						String argument = basicMatcher.group(3);
						if(type == 'C') {
							field.set(null, Class.forName(argument.replace("{nms}", nms).replace("{obc}", obc)));
						} else if(type == 'F') {
							Matcher fieldMatcher = fieldPattern.matcher(argument);
							if(fieldMatcher.matches()) {
								Field clazz = Reflection.class.getDeclaredField(fieldMatcher.group(1));
								String name = fieldMatcher.group(2);
								field.set(null, ((Class<?>)clazz.get(null)).getDeclaredField(name));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else if(type == 'M') {
							Matcher methodMatcher = methodPattern.matcher(argument);
							if(methodMatcher.matches()) {
								Field clazz = Reflection.class.getDeclaredField(methodMatcher.group(1));
								String name = methodMatcher.group(2);
								String[] parameters = methodMatcher.group(3).length() > 0 ? methodMatcher.group(3).split(",") : new String[0];
								Class<?>[] parameterTypes = new Class<?>[parameters.length];
								for(int i = 0; i < parameters.length; i++) {
									switch(parameters[i]) {
										case "boolean":
											parameterTypes[i] = boolean.class;
											continue;
										case "boolean[]":
											parameterTypes[i] = boolean[].class;
											continue;
										case "byte":
											parameterTypes[i] = byte.class;
											continue;
										case "byte[]":
											parameterTypes[i] = byte[].class;
											continue;
										case "short":
											parameterTypes[i] = short.class;
											continue;
										case "short[]":
											parameterTypes[i] = short[].class;
											continue;
										case "int":
											parameterTypes[i] = int.class;
											continue;
										case "int[]":
											parameterTypes[i] = int[].class;
											continue;
										case "long":
											parameterTypes[i] = long.class;
											continue;
										case "long[]":
											parameterTypes[i] = long[].class;
											continue;
										case "float":
											parameterTypes[i] = float.class;
											continue;
										case "float[]":
											parameterTypes[i] = float[].class;
											continue;
										case "double":
											parameterTypes[i] = double.class;
											continue;
										case "double[]":
											parameterTypes[i] = double[].class;
											continue;
									}
									if(parameters[i].endsWith("[]")) {
										parameters[i] = "[L" + parameters[i].substring(0, parameters[i].length() - 2) + ";";
									}
									try {
										parameterTypes[i] = Class.forName(parameters[i].replace("{nms}", nms).replace("{obc}", obc));
										continue;
									} catch(ClassNotFoundException e) {
										if(VersionHelper.debug()) {
											iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot find the given class file.", e);
										}
									}
									parameterTypes[i] = null;
								}
								field.set(null, ((Class<?>)clazz.get(null)).getDeclaredMethod(name, parameterTypes));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else if(type == 'N') {
							Matcher newMatcher = newPattern.matcher(argument);
							if(newMatcher.matches()) {
								Field clazz = Reflection.class.getDeclaredField(newMatcher.group(1));
								String[] parameters = newMatcher.group(2).length() > 0 ? newMatcher.group(2).split(",") : new String[0];
								Class<?>[] parameterTypes = new Class<?>[parameters.length];
								for(int i = 0; i < parameters.length; i++) {
									switch(parameters[i]) {
										case "boolean":
											parameterTypes[i] = boolean.class;
											continue;
										case "boolean[]":
											parameterTypes[i] = boolean[].class;
											continue;
										case "byte":
											parameterTypes[i] = byte.class;
											continue;
										case "byte[]":
											parameterTypes[i] = byte[].class;
											continue;
										case "short":
											parameterTypes[i] = short.class;
											continue;
										case "short[]":
											parameterTypes[i] = short[].class;
											continue;
										case "int":
											parameterTypes[i] = int.class;
											continue;
										case "int[]":
											parameterTypes[i] = int[].class;
											continue;
										case "long":
											parameterTypes[i] = long.class;
											continue;
										case "long[]":
											parameterTypes[i] = long[].class;
											continue;
										case "float":
											parameterTypes[i] = float.class;
											continue;
										case "float[]":
											parameterTypes[i] = float[].class;
											continue;
										case "double":
											parameterTypes[i] = double.class;
											continue;
										case "double[]":
											parameterTypes[i] = double[].class;
											continue;
									}
									if(parameters[i].endsWith("[]")) {
										parameters[i] = "[L" + parameters[i].substring(0, parameters[i].length() - 2) + ";";
									}
									try {
										parameterTypes[i] = Class.forName(parameters[i].replace("{nms}", nms).replace("{obc}", obc));
										continue;
									} catch(ClassNotFoundException e) {
										if(VersionHelper.debug()) {
											iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot find the given class file.", e);
										}
									}
									parameterTypes[i] = null;
								}
								field.set(null, ((Class<?>)clazz.get(null)).getConstructor(parameterTypes));
								((AccessibleObject)field.get(null)).setAccessible(true);
							}
						} else {
							if(VersionHelper.debug()) {
								iDisguise.getInstance().getLogger().log(Level.WARNING, "Cannot parse line: " + line);
							}
						}
					} catch(Exception e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot parse line: " + line, e);
						}
					}
				}
			}
		} catch(IOException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot load the required reflection configuration.", e);
			}
		}
	}
	
	public static Collection<Player> getOnlinePlayers() {
		try {
			Object object = Bukkit_getOnlinePlayers.invoke(null);
			if(object instanceof Collection) {
				return (Collection<Player>)object;
			} else {
				return Arrays.asList((Player[])object);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot access org.bukkit.Bukkit$getOnlinePlayers()", e);
			}
			return new ArrayList<Player>();
		}
	}
	
}