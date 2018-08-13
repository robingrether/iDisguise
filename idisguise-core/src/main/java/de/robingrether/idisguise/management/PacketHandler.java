package de.robingrether.idisguise.management;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.api.PlayerInteractDisguisedPlayerEvent;
import de.robingrether.idisguise.disguise.*;
import de.robingrether.idisguise.management.channel.Unaltered;
import de.robingrether.idisguise.management.util.EntityIdList;

import static de.robingrether.idisguise.management.Reflection.*;
import de.robingrether.util.ObjectUtil;
import io.netty.channel.Channel;

public final class PacketHandler {
	
	private PacketHandler() {}
	
	public static boolean disguiseViewSelf;
	public static boolean modifyScoreboardPackets;
	public static boolean showOriginalPlayerName;
	public static boolean modifyPlayerListEntry;
	public static boolean replaceSoundEffects;
	
	public static boolean bungeeCord;
	
	private static Object[] getSpawnPackets(UUID disguisable, String customName, int entityId, double posX, double posY, double posZ, float rotYaw, float rotPitch, Player observer) { // TODO: custom name
		try {
			Disguise disguise = DisguiseManager.getDisguise(disguisable);
			if(disguise == null) return null;
			
			Object world = Entity_world.get(CraftPlayer_getHandle.invoke(observer));
			
			DisguiseType type = disguise.getType();
			List<Object> packets = new ArrayList<Object>();
			
			if(disguise instanceof MobDisguise) {
				MobDisguise mobDisguise = (MobDisguise)disguise;
				Object entity = null;
				if(VersionHelper.require1_11()) {
					entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World).newInstance(world);
				} else {
					entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass().replaceAll("(Guardian|Horse|Skeleton|Zombie)(Elder|Donkey|Mule|Skeleton|Zombie|Wither|Stray|Villager|Husk)", "$1")).getConstructor(World).newInstance(world);
					switch(type) {
						case ELDER_GUARDIAN:
							EntityGuardian_setElder.invoke(entity, true);
							break;
						case DONKEY:
						case MULE:
						case SKELETAL_HORSE:
						case UNDEAD_HORSE:
							if(VersionHelper.require1_9()) {
								EntityHorse_setType.invoke(entity, EnumHorseType_fromIndex.invoke(null, ((HorseDisguise)mobDisguise).getVariant()));
							} else {
								EntityHorse_setType.invoke(entity, ((HorseDisguise)mobDisguise).getVariant());
							}
							break;
						case WITHER_SKELETON:
							if(VersionHelper.require1_10()) {
								EntitySkeleton_setSkeletonType.invoke(entity, EnumSkeletonType_fromIndex.invoke(null, 1));
							} else {	
								EntitySkeleton_setSkeletonType.invoke(entity, 1);
							}
							break;
						case STRAY:
							if(VersionHelper.require1_10()) {
								EntitySkeleton_setSkeletonType.invoke(entity, EnumSkeletonType_fromIndex.invoke(null, 2));
							} else {	
								EntitySkeleton_setSkeletonType.invoke(entity, 2);
							}
							break;
						case HUSK:
							EntityZombie_setVillagerType.invoke(entity, EnumZombieType_fromIndex.invoke(null, 6));
							break;
						case ZOMBIE_VILLAGER:
							if(!VersionHelper.require1_9()) {
								EntityZombie_setVillager.invoke(entity, true);
							}
							break;
						default: break;
					}
				}
				
				if(showOriginalPlayerName) {
					Entity_setCustomName.invoke(entity, VersionHelper.require1_13() ? Array.get(CraftChatMessage_fromString.invoke(null, customName), 0) : customName);
					Entity_setCustomNameVisible.invoke(entity, true);
				} else if(mobDisguise.getCustomName() != null && !mobDisguise.getCustomName().isEmpty()) {
					Entity_setCustomName.invoke(entity, VersionHelper.require1_13() ? Array.get(CraftChatMessage_fromString.invoke(null, mobDisguise.getCustomName()), 0) : mobDisguise.getCustomName());
					Entity_setCustomNameVisible.invoke(entity, mobDisguise.isCustomNameVisible());
				}
				
				if(mobDisguise instanceof AgeableDisguise) {
					if(!((AgeableDisguise)mobDisguise).isAdult()) {	
						if(EntityAgeable.isInstance(entity)) {
							EntityAgeable_setAge.invoke(entity, -24000);
						} else if(EntityZombie.isInstance(entity)) {
							EntityZombie_setBaby.invoke(entity, true);
						}
					}
					
					if(mobDisguise instanceof HorseDisguise) {
						HorseDisguise horseDisguise = (HorseDisguise)mobDisguise;
						Object inventoryChest = VersionHelper.require1_11() ? EntityHorseAbstract_inventoryChest.get(entity) : EntityHorse_inventoryChest.get(entity);
						InventorySubcontainer_setItem.invoke(inventoryChest, 0, CraftItemStack_asNMSCopy.invoke(null, horseDisguise.isSaddled() ? new ItemStack(Material.SADDLE) : null));
						if(horseDisguise instanceof StyledHorseDisguise) {
							EntityHorse_setVariant.invoke(entity, ((StyledHorseDisguise)horseDisguise).getColor().ordinal() & 0xFF | ((StyledHorseDisguise)horseDisguise).getStyle().ordinal() << 8);
							if(VersionHelper.require1_13()) {
								EntityHorse_setArmor.invoke(entity, CraftItemStack_asNMSCopy.invoke(null, horseDisguise.getArmor().getItem()));
							} else {
								InventorySubcontainer_setItem.invoke(inventoryChest, 1, CraftItemStack_asNMSCopy.invoke(null, horseDisguise.getArmor().getItem()));
							}
						} else if(horseDisguise instanceof ChestedHorseDisguise) {
							if(VersionHelper.require1_11()) {
								EntityHorseChestedAbstract_setCarryingChest.invoke(entity, ((ChestedHorseDisguise)horseDisguise).hasChest());
							} else {
								EntityHorse_setHasChest.invoke(entity, ((ChestedHorseDisguise)horseDisguise).hasChest());
							}
						}
					} else if(mobDisguise instanceof LlamaDisguise) {
						LlamaDisguise llamaDisguise = (LlamaDisguise)mobDisguise;
						EntityLlama_setVariant.invoke(entity, llamaDisguise.getColor().ordinal());
						Object inventoryChest = EntityHorseAbstract_inventoryChest.get(entity);
						InventorySubcontainer_setItem.invoke(inventoryChest, 1, CraftItemStack_asNMSCopy.invoke(null, llamaDisguise.getSaddle().getItem()));
						EntityHorseChestedAbstract_setCarryingChest.invoke(entity, llamaDisguise.hasChest());
					} else if(mobDisguise instanceof OcelotDisguise) {
						OcelotDisguise ocelotDisguise = (OcelotDisguise)mobDisguise;
						EntityOcelot_setCatType.invoke(entity, ocelotDisguise.getCatType().getId());
						EntityTameableAnimal_setSitting.invoke(entity, ocelotDisguise.isSitting());
					} else if(mobDisguise instanceof PigDisguise) {
						EntityPig_setSaddle.invoke(entity, ((PigDisguise)mobDisguise).isSaddled());
					} else if(mobDisguise instanceof RabbitDisguise) {
						EntityRabbit_setRabbitType.invoke(entity, ((RabbitDisguise)mobDisguise).getRabbitType().getId());
					} else if(mobDisguise instanceof SheepDisguise) {
						EntitySheep_setColor.invoke(entity, EnumColor_fromColorIndex.invoke(null, ((SheepDisguise)mobDisguise).getColor().getWoolData()));
					} else if(mobDisguise instanceof VillagerDisguise) {
						EntityVillager_setProfession.invoke(entity, ((VillagerDisguise)mobDisguise).getProfession().ordinal());
					} else if(mobDisguise instanceof WolfDisguise) {
						WolfDisguise wolfDisguise = (WolfDisguise)mobDisguise;
						EntityWolf_setCollarColor.invoke(entity, EnumColor_fromColorIndex.invoke(null, wolfDisguise.getCollarColor().getWoolData()));
						EntityWolf_setTamed.invoke(entity, wolfDisguise.isTamed());
						EntityWolf_setAngry.invoke(entity, wolfDisguise.isAngry());
						EntityTameableAnimal_setSitting.invoke(entity, wolfDisguise.isSitting());
					} else if(mobDisguise instanceof ZombieVillagerDisguise) {
						if(VersionHelper.require1_11()) {
							EntityZombieVillager_setProfession.invoke(entity, ((ZombieVillagerDisguise)mobDisguise).getProfession().ordinal());
						} else if(VersionHelper.require1_10()) {
							EntityZombie_setVillagerType.invoke(entity, EnumZombieType_fromIndex.invoke(null, ((ZombieVillagerDisguise)mobDisguise).getProfession().ordinal() + 1));
						} else if(VersionHelper.require1_9()) {
							EntityZombie_setVillagerType.invoke(entity, ((ZombieVillagerDisguise)mobDisguise).getProfession().ordinal());
						}
					}
				} else if(mobDisguise instanceof CreeperDisguise) {
					EntityCreeper_setPowered.invoke(entity, ((CreeperDisguise)mobDisguise).isPowered());
				} else if(mobDisguise instanceof EndermanDisguise) {
					EndermanDisguise endermanDisguise = (EndermanDisguise)mobDisguise;
					if(VersionHelper.require1_13()) {
						EntityEnderman_setCarried.invoke(entity, CraftBlockData_getHandle.invoke(endermanDisguise.getBlockData()));
					} else {
						EntityEnderman_setCarried.invoke(entity, Block_fromLegacyData.invoke(Block_getById.invoke(null, endermanDisguise.getBlockInHand().getId()), endermanDisguise.getBlockInHandData()));
					}
				} else if(mobDisguise instanceof ParrotDisguise) {
					ParrotDisguise parrotDisguise = (ParrotDisguise)mobDisguise;
					EntityParrot_setVariant.invoke(entity, parrotDisguise.getVariant().ordinal());
					EntityTameableAnimal_setSitting.invoke(entity, parrotDisguise.isSitting());
				} else if(mobDisguise instanceof SizedDisguise) {
					if(mobDisguise.getType().equals(DisguiseType.PHANTOM)) {
						EntityPhantom_setSize.invoke(entity, ((SizedDisguise)mobDisguise).getSize());
					} else {
						if(VersionHelper.require1_11()) {
							EntitySlime_setSize.invoke(entity, ((SizedDisguise)mobDisguise).getSize(), false);
						} else {
							EntitySlime_setSize.invoke(entity, ((SizedDisguise)mobDisguise).getSize());
						}
					}
				} else if(mobDisguise instanceof PufferfishDisguise) {
					EntityPufferFish_setPuffState.invoke(entity, ((PufferfishDisguise)mobDisguise).getPuffState().ordinal());
				} else if(mobDisguise instanceof TropicalFishDisguise) {
					TropicalFishDisguise tfDisguise = (TropicalFishDisguise)mobDisguise;
					int variant = tfDisguise.getPatternColor().getWoolData() << 24 | tfDisguise.getBodyColor().getWoolData() << 16 | tfDisguise.getPattern().getData();
					EntityTropicalFish_setVariant.invoke(entity, variant);
				}
				
				if(EntityBat.isInstance(entity)) {
					EntityBat_setAsleep.invoke(entity, false);
				}
				
				Entity_setLocation.invoke(entity, posX, posY, posZ, rotYaw, rotPitch);
				Entity_setEntityId.invoke(entity, entityId);
				packets.add(PacketPlayOutSpawnEntityLiving_new.newInstance(entity));
			} else if(disguise instanceof PlayerDisguise) {
				PlayerDisguise playerDisguise = (PlayerDisguise)disguise;
				
				if(EntityIdList.isPlayer(disguisable)) {
					Object gameProfile = ProfileHelper.getInstance().getGameProfile(disguisable, playerDisguise.getSkinName(), playerDisguise.getDisplayName());
					
					Object entity = EntityHumanNonAbstract_new.newInstance(world, gameProfile);
					Entity_setLocation.invoke(entity, posX, posY, posZ, rotYaw, rotPitch);
					Entity_setEntityId.invoke(entity, entityId);
					
					Object spawnPacket = PacketPlayOutNamedEntitySpawn_new.newInstance(entity);
					
					packets.add(spawnPacket);
				} else {
					Object gameProfile = ProfileHelper.getInstance().getGameProfile(disguisable, playerDisguise.getSkinName(), playerDisguise.getDisplayName());
					
					// send game profile to client
					Object playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
					PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_ADD_PLAYER.get(null));
					List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
					playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, gameProfile, 35, EnumGamemode_SURVIVAL.get(null), Array.get(CraftChatMessage_fromString.invoke(null, playerDisguise.getDisplayName()), 0)));
					packets.add(playerInfoPacket);
					
					// send entity to client
					Object entity = EntityHumanNonAbstract_new.newInstance(world, gameProfile);
					Entity_setLocation.invoke(entity, posX, posY, posZ, rotYaw, rotPitch);
					Entity_setEntityId.invoke(entity, entityId);
					packets.add(PacketPlayOutNamedEntitySpawn_new.newInstance(entity));
					
					if(!modifyPlayerListEntry) {
						// remove player list entry
						playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
						PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
						playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
						playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, gameProfile, 35, EnumGamemode_SURVIVAL.get(null), null));
						packets.add(playerInfoPacket);
					}
				}
			} else if(disguise instanceof ObjectDisguise) {
				ObjectDisguise objectDisguise = (ObjectDisguise)disguise;
				Object entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World).newInstance(world);
				
				Entity_setLocation.invoke(entity, posX, posY, posZ, rotYaw, rotPitch);
				Entity_setEntityId.invoke(entity, entityId);
				if(showOriginalPlayerName) {
					Entity_setCustomName.invoke(entity, VersionHelper.require1_13() ? Array.get(CraftChatMessage_fromString.invoke(null, customName), 0) : customName);
					Entity_setCustomNameVisible.invoke(entity, true);
				} else if(objectDisguise.getCustomName() != null && !objectDisguise.getCustomName().isEmpty()) {
					Entity_setCustomName.invoke(entity, VersionHelper.require1_13() ? Array.get(CraftChatMessage_fromString.invoke(null, objectDisguise.getCustomName()), 0) : objectDisguise.getCustomName());
					Entity_setCustomNameVisible.invoke(entity, objectDisguise.isCustomNameVisible());
				}
				if(EntityBoat.isInstance(entity)) {
					if(VersionHelper.require1_9() && objectDisguise instanceof BoatDisguise) {
						EntityBoat_setType.invoke(entity, EnumBoatType_fromString.invoke(null, ((BoatDisguise)objectDisguise).getBoatType().name().toLowerCase(Locale.ENGLISH)));
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(entityId, Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityFallingBlock.isInstance(entity)) {
					int typeId = 1;
					if(objectDisguise instanceof FallingBlockDisguise) {
						FallingBlockDisguise fbDisguise = (FallingBlockDisguise)objectDisguise;
						if(VersionHelper.require1_13()) {
							typeId = (Integer)Block_getCombinedId.invoke(null, CraftBlockData_getHandle.invoke(fbDisguise.getMaterialData()));
						} else {
							typeId = fbDisguise.getMaterial().getId() | fbDisguise.getData() << 12;
						}
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), typeId));
				} else if(EntityItem.isInstance(entity)) {
					if(objectDisguise instanceof ItemDisguise) {
						ItemDisguise itemDisguise = (ItemDisguise)objectDisguise;
						EntityItem_setItemStack.invoke(entity, CraftItemStack_asNMSCopy.invoke(null, itemDisguise.getItemStack()));
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 1));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(entityId, Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityMinecartAbstract.isInstance(entity)) {
					if(objectDisguise instanceof MinecartDisguise) {
						MinecartDisguise minecartDisguise = (MinecartDisguise)objectDisguise;
						if(VersionHelper.require1_13()) {
							EntityMinecartAbstract_setDisplayBlock.invoke(entity, CraftBlockData_getHandle.invoke(minecartDisguise.getBlockData()));
						} else {
							EntityMinecartAbstract_setDisplayBlock.invoke(entity, Block_fromLegacyData.invoke(Block_getById.invoke(null, minecartDisguise.getDisplayedBlock().getId()), minecartDisguise.getDisplayedBlockData()));
						}
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(entityId, Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityArmorStand.isInstance(entity)) {
					if(objectDisguise instanceof ArmorStandDisguise) {
						EntityArmorStand_setArms.invoke(entity, ((ArmorStandDisguise)objectDisguise).getShowArms());
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(entityId, Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityAreaEffectCloud.isInstance(entity)) {
					if(objectDisguise instanceof AreaEffectCloudDisguise) {
						AreaEffectCloudDisguise aecDisguise = (AreaEffectCloudDisguise)objectDisguise;
						EntityAreaEffectCloud_setRadius.invoke(entity, aecDisguise.getRadius());
						Class<?> parameterType = aecDisguise.getParameterType();
						if(parameterType.equals(Void.class)) {
							EntityAreaEffectCloud_setParticle.invoke(entity, VersionHelper.require1_13() ? CraftParticle_toNMS.invoke(null, aecDisguise.getParticle(), null) : CraftParticle_toNMS.invoke(null, aecDisguise.getParticle()));
						} else if(parameterType.equals(Color.class)) {
							EntityAreaEffectCloud_setParticle.invoke(entity, VersionHelper.require1_13() ? CraftParticle_toNMS.invoke(null, aecDisguise.getParticle(), null) : CraftParticle_toNMS.invoke(null, aecDisguise.getParticle()));
							EntityAreaEffectCloud_setColor.invoke(entity, ((Color)aecDisguise.getParameter()).asRGB());
						} else {
							EntityAreaEffectCloud_setParticle.invoke(entity, VersionHelper.require1_13() ? CraftParticle_toNMS.invoke(null, aecDisguise.getParticle(), aecDisguise.getParameter()) : CraftParticle_toNMS.invoke(null, aecDisguise.getParticle()));
							if(VersionHelper.require1_10() && !VersionHelper.require1_13()) {
								int[] data = (int[])CraftParticle_getData.invoke(null, aecDisguise.getParticle(), aecDisguise.getParameter());
								if(data.length >= 1) EntityAreaEffectCloud_setParticleParam1.invoke(entity, data[0]);
								if(data.length >= 2) EntityAreaEffectCloud_setParticleParam2.invoke(entity, data[1]);
							}
						}
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(entityId, Entity_getDataWatcher.invoke(entity), true));
				} else {
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
				}
			}
			return packets.toArray(new Object[0]);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required packet.", e);
			}
		}
		return new Object[0];
	}
	
	private static Object getPlayerInfo(UUID disguisable, Player observer, Object context, Object originalPlayerInfo) {
		if(observer.getUniqueId().equals(disguisable)) {
			return getPlayerInfoSelf(disguisable, context, originalPlayerInfo);
		}
		Disguise disguise = DisguiseManager.getDisguise(disguisable);
		try {
			if(disguise == null) {
				return originalPlayerInfo; // PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			} else if(disguise instanceof PlayerDisguise) {
				if(modifyPlayerListEntry) {
					Object displayName = PlayerInfoData_getDisplayName.invoke(originalPlayerInfo);
					return PlayerInfoData_new.newInstance(context,
							ProfileHelper.getInstance().getGameProfile(formatUniqueId(disguisable), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()),
							PlayerInfoData_getPing.invoke(originalPlayerInfo), PlayerInfoData_getGamemode.invoke(originalPlayerInfo), displayName != null ?
								Array.get(CraftChatMessage_fromString.invoke(null, ((String)CraftChatMessage_fromComponent.invoke(null, displayName, EnumChatFormat_WHITE.get(null))).replace(EntityIdList.getPlayerName(disguisable), ((PlayerDisguise)disguise).getDisplayName())), 0) :
								null);
				} else {
					Object displayName = PlayerInfoData_getDisplayName.invoke(originalPlayerInfo);
					return PlayerInfoData_new.newInstance(context,
							ProfileHelper.getInstance().getGameProfile(formatUniqueId(disguisable), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()),
							PlayerInfoData_getPing.invoke(originalPlayerInfo), PlayerInfoData_getGamemode.invoke(originalPlayerInfo), displayName != null ? displayName :
								Array.get(CraftChatMessage_fromString.invoke(null, EntityIdList.getPlayerName(disguisable)), 0));
				}
			} else if(!modifyPlayerListEntry) {
				return originalPlayerInfo; // PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required player info.", e);
			}
		}
		return null;
	}
	
	private static Object getPlayerInfoSelf(UUID disguisable, Object context, Object originalPlayerInfo) {
		Disguise disguise = DisguiseManager.getDisguise(disguisable);
		try {
			if(disguise == null) {
				return originalPlayerInfo; // PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			} else if(disguise instanceof PlayerDisguise) {
				Object displayName = PlayerInfoData_getDisplayName.invoke(originalPlayerInfo);
				return PlayerInfoData_new.newInstance(context,
						ProfileHelper.getInstance().getGameProfile(disguisable, ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()),
						PlayerInfoData_getPing.invoke(originalPlayerInfo), PlayerInfoData_getGamemode.invoke(originalPlayerInfo), displayName != null ? displayName : Array.get(CraftChatMessage_fromString.invoke(null, EntityIdList.getPlayerName(disguisable)), 0));
			} else {
				String mhfSkin = disguise.getType().getMHFSkin();
				if(mhfSkin != null) {
					Object displayName = PlayerInfoData_getDisplayName.invoke(originalPlayerInfo);
					return PlayerInfoData_new.newInstance(context,
							ProfileHelper.getInstance().getGameProfile(disguisable, mhfSkin, EntityIdList.getPlayerName(disguisable)),
							PlayerInfoData_getPing.invoke(originalPlayerInfo), PlayerInfoData_getGamemode.invoke(originalPlayerInfo), displayName != null ? displayName : Array.get(CraftChatMessage_fromString.invoke(null, EntityIdList.getPlayerName(disguisable)), 0));
				} else {
					return originalPlayerInfo; // PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
				}
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required player info.", e);
			}
		}
		return null;
	}
	
	private static int getMetadataId(Object metadataItem) {
		try {
			if(VersionHelper.require1_9()) {
				return (Integer)DataWatcherObject_getId.invoke(DataWatcherItem_dataWatcherObject.get(metadataItem));
			} else {
				return (Integer)DataWatcherObject_getId.invoke(metadataItem);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required metadata id.", e);
			}
		}
		return 127;
	}
	
	private static String soundEffectToString(Object soundEffect) {
		if(VersionHelper.require1_9()) {
			try {
				return (String)MinecraftKey_getName.invoke(RegistryMaterials_getKey.invoke(SoundEffect_registry.get(null), soundEffect));
			} catch(Exception e) {
				if(VersionHelper.debug()) {
					iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required sound effect name.", e);
				}
			}
		} else {
			return (String)soundEffect;
		}
		return null;
	}
	
	private static Object soundEffectFromString(String name) {
		if(VersionHelper.require1_9()) {
			try {
				return RegistryMaterials_getValue.invoke(SoundEffect_registry.get(null), MinecraftKey_new.newInstance(name));
			} catch(Exception e) {
				if(VersionHelper.debug()) {
					iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot retrieve the required sound effect.", e);
				}
			}
		} else {
			return name;
		}
		return null;
	}
	
	private static UUID formatUniqueId(UUID origin) {
		return bungeeCord ? new UUID(origin.getMostSignificantBits() & 0xFFFFFFFFFFFF0FFFL | 0x0000000000005000, origin.getLeastSignificantBits()) : origin; // TODO: use different version
	}
	
	private static Object clonePacket(Object packet) {
		Object clone = null;
		try {
			if(PacketPlayOutPlayerInfo.isInstance(packet)) {
				clone = PacketPlayOutPlayerInfo_new.newInstance();
				PacketPlayOutPlayerInfo_action.set(clone, PacketPlayOutPlayerInfo_action.get(packet));
				PacketPlayOutPlayerInfo_playerInfoList.set(clone, ((ArrayList<?>)PacketPlayOutPlayerInfo_playerInfoList.get(packet)).clone());
			} else if(PacketPlayOutEntityMetadata.isInstance(packet)) {
				clone = PacketPlayOutEntityMetadata_new_empty.newInstance();
				PacketPlayOutEntityMetadata_entityId.setInt(clone, PacketPlayOutEntityMetadata_entityId.getInt(packet));
				PacketPlayOutEntityMetadata_metadataList.set(clone, ((ArrayList<?>)PacketPlayOutEntityMetadata_metadataList.get(packet)).clone());
			} else if(PacketPlayOutEntity.isInstance(packet)) {
				clone = packet.getClass().getConstructor().newInstance();
				PacketPlayOutEntity_entityId.setInt(clone, PacketPlayOutEntity_entityId.getInt(packet));
				PacketPlayOutEntity_deltaX.set(clone, PacketPlayOutEntity_deltaX.get(packet));
				PacketPlayOutEntity_deltaY.set(clone, PacketPlayOutEntity_deltaY.get(packet));
				PacketPlayOutEntity_deltaZ.set(clone, PacketPlayOutEntity_deltaZ.get(packet));
				PacketPlayOutEntity_yaw.setByte(clone, PacketPlayOutEntity_yaw.getByte(packet));
				PacketPlayOutEntity_pitch.setByte(clone, PacketPlayOutEntity_pitch.getByte(packet));
				PacketPlayOutEntity_isOnGround.setBoolean(clone, PacketPlayOutEntity_isOnGround.getBoolean(packet));
			} else if(PacketPlayOutEntityTeleport.isInstance(packet)) {
				clone = PacketPlayOutEntityTeleport_new.newInstance();
				PacketPlayOutEntityTeleport_entityId.setInt(clone, PacketPlayOutEntityTeleport_entityId.getInt(packet));
				PacketPlayOutEntityTeleport_x.set(clone, PacketPlayOutEntityTeleport_x.get(packet));
				PacketPlayOutEntityTeleport_y.set(clone, PacketPlayOutEntityTeleport_y.get(packet));
				PacketPlayOutEntityTeleport_z.set(clone, PacketPlayOutEntityTeleport_z.get(packet));
				PacketPlayOutEntityTeleport_yaw.setByte(clone, PacketPlayOutEntityTeleport_yaw.getByte(packet));
				PacketPlayOutEntityTeleport_pitch.setByte(clone, PacketPlayOutEntityTeleport_pitch.getByte(packet));
				PacketPlayOutEntityTeleport_isOnGround.setBoolean(clone, PacketPlayOutEntityTeleport_isOnGround.getBoolean(packet));
			} else if(PacketPlayOutNamedSoundEffect.isInstance(packet)) {
				clone = PacketPlayOutNamedSoundEffect_new.newInstance();
				PacketPlayOutNamedSoundEffect_soundEffect.set(clone, PacketPlayOutNamedSoundEffect_soundEffect.get(packet));
				if(VersionHelper.require1_9()) {
					PacketPlayOutNamedSoundEffect_soundCategory.set(clone, PacketPlayOutNamedSoundEffect_soundCategory.get(packet));
				}
				PacketPlayOutNamedSoundEffect_x.setInt(clone, PacketPlayOutNamedSoundEffect_x.getInt(packet));
				PacketPlayOutNamedSoundEffect_y.setInt(clone, PacketPlayOutNamedSoundEffect_y.getInt(packet));
				PacketPlayOutNamedSoundEffect_z.setInt(clone, PacketPlayOutNamedSoundEffect_z.getInt(packet));
				PacketPlayOutNamedSoundEffect_volume.setFloat(clone, PacketPlayOutNamedSoundEffect_volume.getFloat(packet));
				if(VersionHelper.require1_10()) {
					PacketPlayOutNamedSoundEffect_pitch.setFloat(clone, PacketPlayOutNamedSoundEffect_pitch.getFloat(packet));
				} else {
					PacketPlayOutNamedSoundEffect_pitch.setInt(clone, PacketPlayOutNamedSoundEffect_pitch.getInt(packet));
				}
			} else if(PacketPlayOutScoreboardTeam.isInstance(packet)) {
				clone = PacketPlayOutScoreboardTeam_new.newInstance();
				PacketPlayOutScoreboardTeam_teamName.set(clone, PacketPlayOutScoreboardTeam_teamName.get(packet));
				PacketPlayOutScoreboardTeam_displayName.set(clone, PacketPlayOutScoreboardTeam_displayName.get(packet));
				PacketPlayOutScoreboardTeam_prefix.set(clone, PacketPlayOutScoreboardTeam_prefix.get(packet));
				PacketPlayOutScoreboardTeam_suffix.set(clone, PacketPlayOutScoreboardTeam_suffix.get(packet));
				PacketPlayOutScoreboardTeam_nameTagVisibility.set(clone, PacketPlayOutScoreboardTeam_nameTagVisibility.get(packet));
				if(VersionHelper.require1_9()) {
					PacketPlayOutScoreboardTeam_collisionRule.set(clone, PacketPlayOutScoreboardTeam_collisionRule.get(packet));
				}
				PacketPlayOutScoreboardTeam_color.setInt(clone, PacketPlayOutScoreboardTeam_color.getInt(packet));
				((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(clone)).addAll((Collection<String>)PacketPlayOutScoreboardTeam_entries.get(packet));
				PacketPlayOutScoreboardTeam_action.setInt(clone, PacketPlayOutScoreboardTeam_action.getInt(packet));
				PacketPlayOutScoreboardTeam_friendlyFlags.setInt(clone, PacketPlayOutScoreboardTeam_friendlyFlags.getInt(packet));
			} else if(PacketPlayOutScoreboardScore.isInstance(packet)) {
				clone = PacketPlayOutScoreboardScore_new.newInstance();
				PacketPlayOutScoreboardScore_entry.set(clone, PacketPlayOutScoreboardScore_entry.get(packet));
				PacketPlayOutScoreboardScore_objective.set(clone, PacketPlayOutScoreboardScore_objective.get(packet));
				PacketPlayOutScoreboardScore_score.setInt(clone, PacketPlayOutScoreboardScore_score.getInt(packet));
				PacketPlayOutScoreboardScore_action.set(clone, PacketPlayOutScoreboardScore_action.get(packet));
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot clone the given packet.", e);
			}
		}
		return clone;
	}
	
	public static void sendPacket(final Player observer, final Object packet) {
		try {
			Channel channel = (Channel)NetworkManager_channel.get(PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))));//.writeAndFlush(packet);
			if(channel.eventLoop().inEventLoop()) {
				for(Object p : packet instanceof Object[] ? (Object[])packet : new Object[] {packet}) channel.writeAndFlush(p);
			} else {
				channel.eventLoop().execute(() -> {
					for(Object p : packet instanceof Object[] ? (Object[])packet : new Object[] {packet}) channel.writeAndFlush(p);
				});
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot send packet: " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
	public static void sendPacketUnaltered(final Player observer, final Object packet) {
		try {
			Channel channel = (Channel)NetworkManager_channel.get(PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(observer))));//.writeAndFlush(new Unaltered(packet));
			if(channel.eventLoop().inEventLoop()) {
				for(Object p : packet instanceof Object[] ? (Object[])packet : new Object[] {packet}) channel.writeAndFlush(new Unaltered(p));
			} else {
				channel.eventLoop().execute(() -> {
					for(Object p : packet instanceof Object[] ? (Object[])packet : new Object[] {packet}) channel.writeAndFlush(new Unaltered(p));
				});
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot send packet (unaltered): " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
	public static Object[] handlePacket(final Player observer, Object packet) {
		try {
			if(handlers.containsKey(packet.getClass())) {
				packet = handlers.get(packet.getClass()).handlePacket(observer, packet);
			}
			return packet instanceof Object[] ? (Object[])packet : new Object[] {packet};
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
			}
		}
		return new Object[0];
	}
	
	private static final Map<Class<?>, IPacketHandler> handlers;
	
	static {
		Map<Class<?>, IPacketHandler> localHandlers = new HashMap<Class<?>, IPacketHandler>();
		
		localHandlers.put(PacketPlayOutNamedEntitySpawn, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutNamedEntitySpawn_entityId.getInt(packet));
			if(disguisable != null && EntityIdList.isPlayer(disguisable) && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer)) {
				Object[] spawnPackets = VersionHelper.require1_9() ?
						getSpawnPackets(disguisable,
								null,
								PacketPlayOutNamedEntitySpawn_entityId.getInt(packet),
								PacketPlayOutNamedEntitySpawn_x.getDouble(packet),
								PacketPlayOutNamedEntitySpawn_y.getDouble(packet),
								PacketPlayOutNamedEntitySpawn_z.getDouble(packet),
								PacketPlayOutNamedEntitySpawn_yaw.getByte(packet) * 360.0F / 256.0F,
								PacketPlayOutNamedEntitySpawn_pitch.getByte(packet) * 360.0F / 256.0F,
								observer) :
						getSpawnPackets(disguisable,
								null,
								PacketPlayOutNamedEntitySpawn_entityId.getInt(packet),
								PacketPlayOutNamedEntitySpawn_x.getInt(packet) / 32.0,
								PacketPlayOutNamedEntitySpawn_y.getInt(packet) / 32.0,
								PacketPlayOutNamedEntitySpawn_z.getInt(packet) / 32.0,
								PacketPlayOutNamedEntitySpawn_yaw.getByte(packet) * 360.0F / 256.0F,
								PacketPlayOutNamedEntitySpawn_pitch.getByte(packet) * 360.0F / 256.0F,
								observer);
				if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.ENDER_DRAGON)) {
					byte yaw = PacketPlayOutSpawnEntityLiving_yaw.getByte(spawnPackets[0]);
					if(yaw < 0) {
						yaw += 128;
					} else {
						yaw -= 128;
					}
					PacketPlayOutSpawnEntityLiving_yaw.setByte(spawnPackets[0], yaw);
				} else if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.FALLING_BLOCK)) {
					if(DisguiseManager.getDisguise(disguisable) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(disguisable)).onlyBlockCoordinates()) {
						if(VersionHelper.require1_9()) {
							PacketPlayOutSpawnEntity_x.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_x.getDouble(spawnPackets[0])) + 0.5);
							PacketPlayOutSpawnEntity_y.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_y.getDouble(spawnPackets[0])));
							PacketPlayOutSpawnEntity_z.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_z.getDouble(spawnPackets[0])) + 0.5);
						} else {
							PacketPlayOutSpawnEntity_x.setInt(spawnPackets[0], (int)((Math.floor(PacketPlayOutSpawnEntity_x.getInt(spawnPackets[0]) / 32.0) + 0.5) * 32));
							PacketPlayOutSpawnEntity_y.setInt(spawnPackets[0], (int)(Math.floor(PacketPlayOutSpawnEntity_y.getInt(spawnPackets[0]) / 32.0) * 32));
							PacketPlayOutSpawnEntity_z.setInt(spawnPackets[0], (int)((Math.floor(PacketPlayOutSpawnEntity_z.getInt(spawnPackets[0]) / 32.0) + 0.5) * 32));
						}
					}
				}
				return spawnPackets;
			}
			return new Object[] {packet};
		});
		
		localHandlers.put(PacketPlayOutSpawnEntityLiving, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutSpawnEntityLiving_entityId.getInt(packet));
			if(disguisable != null && DisguiseManager.isDisguisedTo(disguisable, observer)) {
				Object[] spawnPackets = VersionHelper.require1_9() ?
						getSpawnPackets(disguisable,
								null,
								PacketPlayOutSpawnEntityLiving_entityId.getInt(packet),
								PacketPlayOutSpawnEntityLiving_x.getDouble(packet),
								PacketPlayOutSpawnEntityLiving_y.getDouble(packet),
								PacketPlayOutSpawnEntityLiving_z.getDouble(packet),
								PacketPlayOutSpawnEntityLiving_yaw.getByte(packet) * 360.0F / 256.0F,
								PacketPlayOutSpawnEntityLiving_pitch.getByte(packet) * 360.0F / 256.0F,
								observer) :
						getSpawnPackets(disguisable,
								null,
								PacketPlayOutSpawnEntityLiving_entityId.getInt(packet),
								PacketPlayOutSpawnEntityLiving_x.getInt(packet) / 32.0,
								PacketPlayOutSpawnEntityLiving_y.getInt(packet) / 32.0,
								PacketPlayOutSpawnEntityLiving_z.getInt(packet) / 32.0,
								PacketPlayOutSpawnEntityLiving_yaw.getByte(packet) * 360.0F / 256.0F,
								PacketPlayOutSpawnEntityLiving_pitch.getByte(packet) * 360.0F / 256.0F,
								observer);
				if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.PLAYER) && !modifyPlayerListEntry) {
					final Object playerInfoRemovePacket = spawnPackets[2];
					spawnPackets = new Object[] {spawnPackets[0], spawnPackets[1]};
					Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), () -> {
						try {
							sendPacketUnaltered(observer, playerInfoRemovePacket);
						} catch(Exception e) {
							if(VersionHelper.debug()) {
								iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
							}
						}
					}, 40L);
				} else if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.FALLING_BLOCK)) {
					if(DisguiseManager.getDisguise(disguisable) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(disguisable)).onlyBlockCoordinates()) {
						if(VersionHelper.require1_9()) {
							PacketPlayOutSpawnEntity_x.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_x.getDouble(spawnPackets[0])) + 0.5);
							PacketPlayOutSpawnEntity_y.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_y.getDouble(spawnPackets[0])));
							PacketPlayOutSpawnEntity_z.setDouble(spawnPackets[0], Math.floor(PacketPlayOutSpawnEntity_z.getDouble(spawnPackets[0])) + 0.5);
						} else {
							PacketPlayOutSpawnEntity_x.setInt(spawnPackets[0], (int)((Math.floor(PacketPlayOutSpawnEntity_x.getInt(spawnPackets[0]) / 32.0) + 0.5) * 32));
							PacketPlayOutSpawnEntity_y.setInt(spawnPackets[0], (int)(Math.floor(PacketPlayOutSpawnEntity_y.getInt(spawnPackets[0]) / 32.0) * 32));
							PacketPlayOutSpawnEntity_z.setInt(spawnPackets[0], (int)((Math.floor(PacketPlayOutSpawnEntity_z.getInt(spawnPackets[0]) / 32.0) + 0.5) * 32));
						}
					}
				} else if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.ENDER_DRAGON) ^ EntityIdList.isEnderDragon(disguisable)) {
					byte yaw = PacketPlayOutSpawnEntityLiving_yaw.getByte(spawnPackets[0]);
					if(yaw < 0) {
						yaw += 128;
					} else {
						yaw -= 128;
					}
					PacketPlayOutSpawnEntityLiving_yaw.setByte(spawnPackets[0], yaw);
				}
				return spawnPackets;
			}
			return new Object[] {packet};
		});
		
		localHandlers.put(PacketPlayOutPlayerInfo, (final Player observer, final Object packet) -> {
			Object customizablePacket = clonePacket(packet);
			List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(customizablePacket);
			List itemsToAdd = new ArrayList();
			List itemsToRemove = new ArrayList();
			for(Object playerInfo : playerInfoList) {
				UUID disguisable = (UUID)GameProfile_getProfileId.invoke(PlayerInfoData_getProfile.invoke(playerInfo));
				if(disguisable != null && (disguiseViewSelf || !observer.getUniqueId().equals(disguisable)) && DisguiseManager.isDisguisedTo(disguisable, observer)) {
					Object newPlayerInfo = getPlayerInfo(disguisable, observer, customizablePacket, playerInfo);
					itemsToRemove.add(playerInfo);
					if(newPlayerInfo != null) {
						itemsToAdd.add(newPlayerInfo);
					}
				}
			}
			playerInfoList.removeAll(itemsToRemove);
			playerInfoList.addAll(itemsToAdd);
			return customizablePacket;
		});
		
		localHandlers.put(PacketPlayOutBed, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutBed_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer) && !(DisguiseManager.getDisguise(disguisable) instanceof PlayerDisguise)) {
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutAnimation, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutAnimation_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer) && !(DisguiseManager.getDisguise(disguisable) instanceof PlayerDisguise)) {
				if(DisguiseManager.getDisguise(disguisable) instanceof MobDisguise) {
					if(PacketPlayOutAnimation_animationType.getInt(packet) == 2) {
						return null;
					}
				} else if(DisguiseManager.getDisguise(disguisable) instanceof ObjectDisguise) {
					if(ObjectUtil.equals(PacketPlayOutAnimation_animationType.getInt(packet), 0, 2, 3)) {
						return null;
					}
				}
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutEntityMetadata, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutEntityMetadata_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer)/* && !(DisguiseManager.getDisguise(livingEntity) instanceof PlayerDisguise)*/) {
				Object customizablePacket = clonePacket(packet);
				boolean living = DisguiseManager.getDisguise(disguisable) instanceof MobDisguise;
				List metadataList = (List)PacketPlayOutEntityMetadata_metadataList.get(customizablePacket);
				List itemsToRemove = new ArrayList();
				for(Object metadataItem : metadataList) {
					int metadataId = getMetadataId(metadataItem);
					if(living) {
						if(!ObjectUtil.equals(metadataId, 0, 6, 7, 8, 9, 10)) itemsToRemove.add(metadataItem);
					} else {
						if(metadataId != 0) itemsToRemove.add(metadataItem);
					}
				}
				metadataList.removeAll(itemsToRemove);
				return customizablePacket;
			}
			return packet;
		});
		
		IPacketHandler playOutEntityHandler = (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutEntity_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer)) {
				if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.FALLING_BLOCK)) {
					if(DisguiseManager.getDisguise(disguisable) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(disguisable)).onlyBlockCoordinates()) {
						Bukkit.getScheduler().runTask(iDisguise.getInstance(), () -> {
							try {
								final LivingEntity livingEntity = EntityIdList.getEntityByUID(disguisable);
								Object customizablePacket = PacketPlayOutEntityTeleport_new.newInstance();
								PacketPlayOutEntityTeleport_entityId.setInt(customizablePacket, PacketPlayOutEntity_entityId.getInt(packet));
								if(VersionHelper.require1_9()) {
									PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(livingEntity.getLocation().getX()) + 0.5);
									PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(livingEntity.getLocation().getY()));
									PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(livingEntity.getLocation().getZ()) + 0.5);
								} else {
									PacketPlayOutEntityTeleport_x.setInt(customizablePacket, (int)((Math.floor(livingEntity.getLocation().getX()) + 0.5) * 32));
									PacketPlayOutEntityTeleport_y.setInt(customizablePacket, (int)(Math.floor(livingEntity.getLocation().getY()) * 32));
									PacketPlayOutEntityTeleport_z.setInt(customizablePacket, (int)((Math.floor(livingEntity.getLocation().getZ()) + 0.5) * 32));
								}
								PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, (byte)(livingEntity.getLocation().getYaw() * 256 / 360));
								PacketPlayOutEntityTeleport_pitch.setByte(customizablePacket, (byte)(livingEntity.getLocation().getPitch() * 256 / 360));
								PacketPlayOutEntityTeleport_isOnGround.setBoolean(customizablePacket, PacketPlayOutEntity_isOnGround.getBoolean(packet));
								sendPacketUnaltered(observer, customizablePacket);
							} catch(Exception e) {
								if(VersionHelper.debug()) {
									iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
								}
							}
						});
						return null;
					}
				} else if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.ENDER_DRAGON) ^ EntityIdList.isEnderDragon(disguisable)) {
					Object customizablePacket = clonePacket(packet);
					byte yaw = PacketPlayOutEntity_yaw.getByte(customizablePacket);
					if(yaw < 0) {
						yaw += 128;
					} else {
						yaw -= 128;
					}
					PacketPlayOutEntity_yaw.setByte(customizablePacket, yaw);
					return customizablePacket;
				} 
			}
			return packet;
		};
		for(Class<?> clazz : new Class<?>[] {PacketPlayOutEntity, PacketPlayOutEntityLook, PacketPlayOutRelEntityMove, PacketPlayOutRelEntityMoveLook}) localHandlers.put(clazz, playOutEntityHandler);
		
		localHandlers.put(PacketPlayOutEntityTeleport, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutEntityTeleport_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer)) {
				if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.FALLING_BLOCK)) {
					if(DisguiseManager.getDisguise(disguisable) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(disguisable)).onlyBlockCoordinates()) {
						Object customizablePacket = clonePacket(packet);
						if(VersionHelper.require1_9()) {
							PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(PacketPlayOutEntityTeleport_x.getDouble(packet)) + 0.5);
							PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(PacketPlayOutEntityTeleport_y.getDouble(packet)));
							PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(PacketPlayOutEntityTeleport_z.getDouble(packet)) + 0.5);
						} else {
							PacketPlayOutEntityTeleport_x.setInt(customizablePacket, (int)((Math.floor(PacketPlayOutEntityTeleport_x.getInt(packet) / 32.0) + 0.5) * 32));
							PacketPlayOutEntityTeleport_y.setInt(customizablePacket, (int)(Math.floor(PacketPlayOutEntityTeleport_y.getInt(packet) / 32.0) * 32));
							PacketPlayOutEntityTeleport_z.setInt(customizablePacket, (int)((Math.floor(PacketPlayOutEntityTeleport_x.getInt(packet) / 32.0) + 0.5) * 32));
						}
						return customizablePacket;
					}
				} else if(DisguiseManager.getDisguise(disguisable).getType().equals(DisguiseType.ENDER_DRAGON) ^ EntityIdList.isEnderDragon(disguisable)) {
					Object customizablePacket = clonePacket(packet);
					byte yaw = PacketPlayOutEntityTeleport_yaw.getByte(customizablePacket);
					if(yaw < 0) {
						yaw += 128;
					} else {
						yaw -= 128;
					}
					PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, yaw);
					return customizablePacket;
				}
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutUpdateAttributes, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutUpdateAttributes_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer) && DisguiseManager.getDisguise(disguisable) instanceof ObjectDisguise) {
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutCollect, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayOutCollect_entityId.getInt(packet));
			if(disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer) && DisguiseManager.getDisguise(disguisable) instanceof ObjectDisguise) {
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutNamedSoundEffect, (final Player observer, final Object packet) -> {
			if(replaceSoundEffects) {
				Bukkit.getScheduler().runTask(iDisguise.getInstance(), () -> {
					try {
						String soundEffect = soundEffectToString(PacketPlayOutNamedSoundEffect_soundEffect.get(packet));
						LivingEntity livingEntity = EntityIdList.getClosestEntity(new Location(observer.getWorld(), PacketPlayOutNamedSoundEffect_x.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_y.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_z.getInt(packet) / 8.0), 1.0);
						if(livingEntity != null && livingEntity != observer && DisguiseManager.isDisguisedTo(livingEntity, observer)) {
							String newSoundEffect = Sounds.replaceSoundEffect(DisguiseType.fromEntityType(livingEntity.getType()), soundEffect, DisguiseManager.getDisguise(livingEntity));
							if(!soundEffect.equals(newSoundEffect)) {
								if(newSoundEffect != null) {
									Object nmsSoundEffect = soundEffectFromString(newSoundEffect);
									if(nmsSoundEffect != null) {
										Object customizablePacket = clonePacket(packet);
										PacketPlayOutNamedSoundEffect_soundEffect.set(customizablePacket, nmsSoundEffect);
										sendPacketUnaltered(observer, customizablePacket);
										return;
									} else {
										return;
									}
								} else {
									return;
								}
							}
						}
						sendPacketUnaltered(observer, packet);
					} catch(Exception e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
						}
					}
				});
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutScoreboardTeam, (final Player observer, final Object packet) -> {
			if(modifyScoreboardPackets && ObjectUtil.equals(PacketPlayOutScoreboardTeam_action.getInt(packet), 0, 3, 4)) {
				Bukkit.getScheduler().runTask(iDisguise.getInstance(), () -> {
					try {
						Object customizablePacket = clonePacket(packet);
						List<String> entries = (List<String>)PacketPlayOutScoreboardTeam_entries.get(customizablePacket);
						List<String> itemsToRemove = new ArrayList<String>();
						List<String> itemsToAdd = new ArrayList<String>();
						for(String entry : entries) {
							Player player = Bukkit.getPlayer(entry);
							if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
								itemsToRemove.add(entry);
								itemsToAdd.add(((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName());
							}
						}
						entries.removeAll(itemsToRemove);
						entries.addAll(itemsToAdd);
						sendPacketUnaltered(observer, customizablePacket);
					} catch(Exception e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
						}
					}
				});
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutScoreboardScore, (final Player observer, final Object packet) -> {
			if(modifyScoreboardPackets) {
				Bukkit.getScheduler().runTask(iDisguise.getInstance(), () -> {
					try {
						Player player = Bukkit.getPlayer((String)PacketPlayOutScoreboardScore_entry.get(packet));
						if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
							Object customizablePacket = clonePacket(packet);
							PacketPlayOutScoreboardScore_entry.set(customizablePacket, ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName());
							sendPacketUnaltered(observer, customizablePacket);
						}
					} catch(Exception e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet: " + packet.getClass().getSimpleName() + " for " + observer.getName(), e);
						}
					}
				});
				return null;
			}
			return packet;
		});
		
		localHandlers.put(PacketPlayOutEntityDestroy, (final Player observer, final Object packet) -> {
			int[] entityIds = (int[])PacketPlayOutEntityDestroy_entityIds.get(packet);
			
			// construct the player info packet
			Object playerInfoPacket = PacketPlayOutPlayerInfo_new.newInstance();
			PacketPlayOutPlayerInfo_action.set(playerInfoPacket, EnumPlayerInfoAction_REMOVE_PLAYER.get(null));
			List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(playerInfoPacket);
			
			for(int entityId : entityIds) {
				UUID disguisable = EntityIdList.getEntityUIDByEntityId(entityId);
				if(disguisable != null && Bukkit.getPlayer(disguisable) == null && DisguiseManager.isDisguisedTo(disguisable, observer) && DisguiseManager.getDisguise(disguisable) instanceof PlayerDisguise) {
					playerInfoList.add(PlayerInfoData_new.newInstance(playerInfoPacket, ProfileHelper.getInstance().getGameProfile(disguisable, "", ""), 35, null, null));
				}
			}
			
			if(playerInfoList.isEmpty()) {
				return new Object[] {packet};
			} else {
				return new Object[] {packet, playerInfoPacket};
			}
		});
		
		localHandlers.put(PacketPlayInUseEntity, (final Player observer, final Object packet) -> {
			final UUID disguisable = EntityIdList.getEntityUIDByEntityId(PacketPlayInUseEntity_entityId.getInt(packet));
			boolean attack = PacketPlayInUseEntity_getAction.invoke(packet).equals(EnumEntityUseAction_ATTACK.get(null));
			if(!attack && disguisable != null && !observer.getUniqueId().equals(disguisable) && DisguiseManager.isDisguisedTo(disguisable, observer)) {
				if(ObjectUtil.equals(DisguiseManager.getDisguise(disguisable).getType(), DisguiseType.SHEEP, DisguiseType.WOLF)) {
					Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), () -> {
						DisguiseManager.resendPackets(EntityIdList.getEntityByUID(disguisable));
						observer.updateInventory();
					}, 2L);
				}
				Bukkit.getScheduler().runTask(iDisguise.getInstance(), () -> {
					if(Bukkit.getPlayer(disguisable) != null) Bukkit.getPluginManager().callEvent(new PlayerInteractDisguisedPlayerEvent(observer, Bukkit.getPlayer(disguisable)));
				});
			}
			return packet;
		});
		
		handlers = Collections.unmodifiableMap(localHandlers);
	}
	
	interface IPacketHandler {
		Object handlePacket(Player observer, Object packet) throws Exception;
	}
	
}