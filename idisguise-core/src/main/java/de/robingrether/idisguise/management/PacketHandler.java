package de.robingrether.idisguise.management;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.api.PlayerInteractDisguisedPlayerEvent;
import de.robingrether.idisguise.disguise.*;
import de.robingrether.idisguise.disguise.LlamaDisguise.SaddleColor;
import de.robingrether.idisguise.management.util.EntityIdList;

import static de.robingrether.idisguise.management.Reflection.*;
import de.robingrether.util.ObjectUtil;

public final class PacketHandler {
	
	private PacketHandler() {}
	
	public static boolean modifyScoreboardPackets;
	public static boolean showOriginalPlayerName;
	public static boolean modifyPlayerListEntry;
	
	public static Object[] getSpawnPackets(Player player) {
		try {
			Disguise disguise = DisguiseManager.getDisguise(player);
			if(disguise == null) return null;
			
			Object entityPlayer = CraftPlayer_getHandle.invoke(player);
			DisguiseType type = disguise.getType();
			List<Object> packets = new ArrayList<Object>();
			
			if(disguise instanceof MobDisguise) {
				MobDisguise mobDisguise = (MobDisguise)disguise;
				Object entity = null;
				if(VersionHelper.require1_11()) {
					entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World).newInstance(Entity_world.get(entityPlayer));
				} else {
					entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass().replaceAll("(Guardian|Horse|Skeleton|Zombie)(Elder|Donkey|Mule|Skeleton|Zombie|Wither|Stray|Villager|Husk)", "$1")).getConstructor(World).newInstance(Entity_world.get(entityPlayer));
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
				if(mobDisguise.getCustomName() != null && !mobDisguise.getCustomName().isEmpty()) {
					Entity_setCustomName.invoke(entity, mobDisguise.getCustomName());
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
						InventorySubcontainer_setItem.invoke(inventoryChest, 1, CraftItemStack_asNMSCopy.invoke(null, horseDisguise.getArmor().getItem()));
						if(horseDisguise instanceof StyledHorseDisguise) {
							EntityHorse_setVariant.invoke(entity, ((StyledHorseDisguise)horseDisguise).getColor().ordinal() & 0xFF | ((StyledHorseDisguise)horseDisguise).getStyle().ordinal() << 8);
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
						InventorySubcontainer_setItem.invoke(inventoryChest, 1, CraftItemStack_asNMSCopy.invoke(null, llamaDisguise.getSaddle().equals(SaddleColor.NOT_SADDLED) ? null : new ItemStack(Material.CARPET, 1, (short)llamaDisguise.getSaddle().ordinal())));
						EntityHorseChestedAbstract_setCarryingChest.invoke(entity, llamaDisguise.hasChest());
					} else if(mobDisguise instanceof OcelotDisguise) {
						EntityOcelot_setCatType.invoke(entity, ((OcelotDisguise)mobDisguise).getCatType().getId());
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
					EntityEnderman_setCarried.invoke(entity, Block_fromLegacyData.invoke(Block_getById.invoke(null, endermanDisguise.getBlockInHand().getId()), endermanDisguise.getBlockInHandData()));
				} else if(mobDisguise instanceof ParrotDisguise) {
					EntityParrot_setVariant.invoke(entity, ((ParrotDisguise)mobDisguise).getVariant().ordinal());
				} else if(mobDisguise instanceof SizedDisguise) {
					if(VersionHelper.require1_11()) {
						EntitySlime_setSize.invoke(entity, ((SizedDisguise)mobDisguise).getSize(), false);
					} else {
						EntitySlime_setSize.invoke(entity, ((SizedDisguise)mobDisguise).getSize());
					}
				}
				
				if(EntityBat.isInstance(entity)) {
					EntityBat_setAsleep.invoke(entity, false);
				}
				
				Location location = player.getLocation();
				Entity_setLocation.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
				Entity_setEntityId.invoke(entity, player.getEntityId());
				if(showOriginalPlayerName) {
					Entity_setCustomName.invoke(entity, player.getName());
					Entity_setCustomNameVisible.invoke(entity, true);
				}
				packets.add(PacketPlayOutSpawnEntityLiving_new.newInstance(entity));
			} else if(disguise instanceof PlayerDisguise) {
				packets.add(PacketPlayOutNamedEntitySpawn_new.newInstance(entityPlayer));
				// don't modify anything here, skin is applied via player list item packet
			} else if(disguise instanceof ObjectDisguise) {
				ObjectDisguise objectDisguise = (ObjectDisguise)disguise;
				Object entity = Class.forName(VersionHelper.getNMSPackage() + "." + type.getNMSClass()).getConstructor(World).newInstance(Entity_world.get(entityPlayer));
				Location location = player.getLocation();
				Entity_setLocation.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
				Entity_setEntityId.invoke(entity, player.getEntityId());
				if(showOriginalPlayerName) {
					Entity_setCustomName.invoke(entity, player.getName());
					Entity_setCustomNameVisible.invoke(entity, true);
				} else if(objectDisguise.getCustomName() != null && !objectDisguise.getCustomName().isEmpty()) {
					Entity_setCustomName.invoke(entity, objectDisguise.getCustomName());
					Entity_setCustomNameVisible.invoke(entity, objectDisguise.isCustomNameVisible());
				}
				if(EntityFallingBlock.isInstance(entity)) {
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), objectDisguise instanceof FallingBlockDisguise ? ((FallingBlockDisguise)objectDisguise).getMaterial().getId() | (((FallingBlockDisguise)objectDisguise).getData() << 12) : 1));
				} else if(EntityItem.isInstance(entity)) {
					if(objectDisguise instanceof ItemDisguise) {
						ItemDisguise itemDisguise = (ItemDisguise)objectDisguise;
						EntityItem_setItemStack.invoke(entity, CraftItemStack_asNMSCopy.invoke(null, itemDisguise.getItemStack()));
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(player.getEntityId(), Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityMinecartAbstract.isInstance(entity)) {
					if(objectDisguise instanceof MinecartDisguise) {
						MinecartDisguise minecartDisguise = (MinecartDisguise)objectDisguise;
						EntityMinecartAbstract_setDisplayBlock.invoke(entity, Block_fromLegacyData.invoke(Block_getById.invoke(null, minecartDisguise.getDisplayedBlock().getId()), minecartDisguise.getDisplayedBlockData()));
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(player.getEntityId(), Entity_getDataWatcher.invoke(entity), true));
				} else if(EntityArmorStand.isInstance(entity)) {
					if(objectDisguise instanceof ArmorStandDisguise) {
						EntityArmorStand_setArms.invoke(entity, ((ArmorStandDisguise)objectDisguise).getShowArms());
					}
					packets.add(PacketPlayOutSpawnEntity_new.newInstance(entity, objectDisguise.getTypeId(), 0));
					packets.add(PacketPlayOutEntityMetadata_new_full.newInstance(player.getEntityId(), Entity_getDataWatcher.invoke(entity), true));
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
	
	public static Object getPlayerInfo(OfflinePlayer offlinePlayer, Object context, int ping, Object gamemode, Object displayName) {
		Disguise disguise = DisguiseManager.getDisguise(offlinePlayer);
		try {
			if(disguise == null) {
				return PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			} else if(disguise instanceof PlayerDisguise) {
				return PlayerInfoData_new.newInstance(context, ProfileHelper.getInstance().getGameProfile(offlinePlayer.getUniqueId(), ((PlayerDisguise)disguise).getSkinName(), ((PlayerDisguise)disguise).getDisplayName()), ping, gamemode, modifyPlayerListEntry ? Array.get(CraftChatMessage_fromString.invoke(null, ((PlayerDisguise)disguise).getDisplayName()), 0) : displayName != null ? displayName : Array.get(CraftChatMessage_fromString.invoke(null, offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getPlayerListName() : offlinePlayer.getName()), 0));
			} else if(!modifyPlayerListEntry) {
				return PlayerInfoData_new.newInstance(context, offlinePlayer.isOnline() ? CraftPlayer_getProfile.invoke(offlinePlayer) : CraftOfflinePlayer_getProfile.invoke(offlinePlayer), ping, gamemode, displayName);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot construct the required player info.", e);
			}
		}
		return null;
	}
	
	public static int getMetadataId(Object metadataItem) {
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
	
	public static String soundEffectToString(Object soundEffect) {
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
	
	public static Object soundEffectFromString(String name) {
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
	
	public static Object clonePacket(Object packet) {
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
				clone = packet.getClass().newInstance();
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
				PacketPlayOutNamedSoundEffect_pitch.setInt(clone, PacketPlayOutNamedSoundEffect_pitch.getInt(packet));
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
	
	public static Object handlePacketPlayInUseEntity(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayInUseEntity_entityId.getInt(packet));
		boolean attack = PacketPlayInUseEntity_getAction.invoke(packet).equals(EnumEntityUseAction_ATTACK.get(null));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && !attack) {
			if(ObjectUtil.equals(DisguiseManager.getDisguise(player).getType(), DisguiseType.SHEEP, DisguiseType.WOLF)) {
				Bukkit.getScheduler().runTaskLater(iDisguise.getInstance(), new Runnable() {
					
					public void run() {
						DisguiseManager.resendPackets(player);
						observer.updateInventory();
					}
					
				}, 2L);
			}
			Bukkit.getPluginManager().callEvent(new PlayerInteractDisguisedPlayerEvent(observer, player));
			return null;
		}
		return packet;
	}
	
	public static Object[] handlePacketOut(final Player observer, final Object packet) {
		try {
			if(PacketPlayOutNamedEntitySpawn.isInstance(packet)) {
				return handlePacketPlayOutNamedEntitySpawn(observer, packet);
			} else if(PacketPlayOutPlayerInfo.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutPlayerInfo(observer, packet)};
			} else if(PacketPlayOutBed.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutBed(observer, packet)};
			} else if(PacketPlayOutAnimation.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutAnimation(observer, packet)};
			} else if(PacketPlayOutEntityMetadata.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutEntityMetadata(observer, packet)};
			} else if(PacketPlayOutEntity.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutEntity(observer, packet)};
			} else if(PacketPlayOutEntityTeleport.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutEntityTeleport(observer, packet)};
			} else if(PacketPlayOutUpdateAttributes.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutUpdateAttributes(observer, packet)};
			} else if(PacketPlayOutCollect.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutCollect(observer, packet)};
			} else if(PacketPlayOutScoreboardTeam.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutScoreboardTeam(observer, packet)};
			} else if(PacketPlayOutScoreboardScore.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutScoreboardScore(observer, packet)};
			} else if(Sounds.isEnabled() && PacketPlayOutNamedSoundEffect.isInstance(packet)) {
				return new Object[] {handlePacketPlayOutNamedSoundEffect(observer, packet)};
			}
			return new Object[] {packet};
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet out: " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
		return new Object[0];
	}
	
	private static Object[] handlePacketPlayOutNamedEntitySpawn(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutNamedEntitySpawn_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer)) {
			Object[] spawnPackets = getSpawnPackets(player);
			if(PacketPlayOutSpawnEntityLiving.isInstance(spawnPackets[0]) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				byte yaw = PacketPlayOutSpawnEntityLiving_yaw.getByte(spawnPackets[0]);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutSpawnEntityLiving_yaw.setByte(spawnPackets[0], yaw);
			} else if(PacketPlayOutSpawnEntity.isInstance(spawnPackets[0]) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
				if(DisguiseManager.getDisguise(player) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(player)).onlyBlockCoordinates()) {
					if(VersionHelper.require1_9()) {
						PacketPlayOutSpawnEntity_x.setDouble(spawnPackets[0], Math.floor(player.getLocation().getX()) + 0.5);
						PacketPlayOutSpawnEntity_y.setDouble(spawnPackets[0], Math.floor(player.getLocation().getY()));
						PacketPlayOutSpawnEntity_z.setDouble(spawnPackets[0], Math.floor(player.getLocation().getZ()) + 0.5);
					} else {
						PacketPlayOutSpawnEntity_x.setInt(spawnPackets[0], (int)((Math.floor(player.getLocation().getX()) + 0.5) * 32));
						PacketPlayOutSpawnEntity_y.setInt(spawnPackets[0], (int)(Math.floor(player.getLocation().getY()) * 32));
						PacketPlayOutSpawnEntity_z.setInt(spawnPackets[0], (int)((Math.floor(player.getLocation().getZ()) + 0.5) * 32));
					}
				}
			}
			return spawnPackets;
		}
		return new Object[] {packet};
	}
	
	private static Object handlePacketPlayOutPlayerInfo(final Player observer, final Object packet) throws Exception {
		Object customizablePacket = clonePacket(packet);
		List playerInfoList = (List)PacketPlayOutPlayerInfo_playerInfoList.get(customizablePacket);
		List itemsToAdd = new ArrayList();
		List itemsToRemove = new ArrayList();
		for(Object playerInfo : playerInfoList) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)GameProfile_getProfileId.invoke(PlayerInfoData_getProfile.invoke(playerInfo)));
			if(offlinePlayer != null && offlinePlayer != observer && DisguiseManager.isDisguisedTo(offlinePlayer, observer)) {
				Object newPlayerInfo = getPlayerInfo(offlinePlayer, customizablePacket, (Integer)PlayerInfoData_getPing.invoke(playerInfo), PlayerInfoData_getGamemode.invoke(playerInfo), PlayerInfoData_getDisplayName.invoke(playerInfo));
				itemsToRemove.add(playerInfo);
				if(newPlayerInfo != null) {
					itemsToAdd.add(newPlayerInfo);
				}
			}
		}
		playerInfoList.removeAll(itemsToRemove);
		playerInfoList.addAll(itemsToAdd);
		return customizablePacket;
	}
	
	private static Object handlePacketPlayOutBed(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutBed_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
			return null;
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutAnimation(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutAnimation_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
			if(DisguiseManager.getDisguise(player) instanceof MobDisguise) {
				if(PacketPlayOutAnimation_animationType.getInt(packet) == 2) {
					return null;
				}
			} else if(DisguiseManager.getDisguise(player) instanceof ObjectDisguise) {
				if(ObjectUtil.equals(PacketPlayOutAnimation_animationType.getInt(packet), 0, 2, 3)) {
					return null;
				}
			}
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutEntityMetadata(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutEntityMetadata_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
			Object customizablePacket = clonePacket(packet);
			boolean living = DisguiseManager.getDisguise(player) instanceof MobDisguise;
			List metadataList = (List)PacketPlayOutEntityMetadata_metadataList.get(customizablePacket);
			List itemsToRemove = new ArrayList();
			for(Object metadataItem : metadataList) {
				int metadataId = getMetadataId(metadataItem);
				if(metadataId > 0 && !(living && metadataId >= 6 && metadataId <= 9)) {
					itemsToRemove.add(metadataItem);
				}
			}
			metadataList.removeAll(itemsToRemove);
			return customizablePacket;
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutEntity(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutEntity_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer)) {
			if(DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				Object customizablePacket = clonePacket(packet);
				byte yaw = PacketPlayOutEntity_yaw.getByte(customizablePacket);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutEntity_yaw.setByte(customizablePacket, yaw);
				return customizablePacket;
			} else if(DisguiseManager.getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
				if(DisguiseManager.getDisguise(player) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(player)).onlyBlockCoordinates()) {
					Object customizablePacket = PacketPlayOutEntityTeleport_new.newInstance();
					PacketPlayOutEntityTeleport_entityId.setInt(customizablePacket, player.getEntityId());
					if(VersionHelper.require1_9()) {
						PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(player.getLocation().getX()) + 0.5);
						PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(player.getLocation().getY()));
						PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(player.getLocation().getZ()) + 0.5);
					} else {
						PacketPlayOutEntityTeleport_x.setInt(customizablePacket, (int)((Math.floor(player.getLocation().getX()) + 0.5) * 32));
						PacketPlayOutEntityTeleport_y.setInt(customizablePacket, (int)(Math.floor(player.getLocation().getY()) * 32));
						PacketPlayOutEntityTeleport_z.setInt(customizablePacket, (int)((Math.floor(player.getLocation().getZ()) + 0.5) * 32));
					}
					PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, (byte)(player.getLocation().getYaw() * 256 / 360));
					PacketPlayOutEntityTeleport_pitch.setByte(customizablePacket, (byte)(player.getLocation().getPitch() * 256 / 360));
					PacketPlayOutEntityTeleport_isOnGround.setBoolean(customizablePacket, PacketPlayOutEntity_isOnGround.getBoolean(packet));
					return customizablePacket;
				}
			}
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutEntityTeleport(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutEntityTeleport_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer)) {
			if(DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
				Object customizablePacket = clonePacket(packet);
				byte yaw = PacketPlayOutEntityTeleport_yaw.getByte(customizablePacket);
				if(yaw < 0) {
					yaw += 128;
				} else {
					yaw -= 128;
				}
				PacketPlayOutEntityTeleport_yaw.setByte(customizablePacket, yaw);
				return customizablePacket;
			} else if(DisguiseManager.getDisguise(player).getType().equals(DisguiseType.FALLING_BLOCK)) {
				if(DisguiseManager.getDisguise(player) instanceof FallingBlockDisguise && ((FallingBlockDisguise)DisguiseManager.getDisguise(player)).onlyBlockCoordinates()) {
					Object customizablePacket = clonePacket(packet);
					if(VersionHelper.require1_9()) {
						PacketPlayOutEntityTeleport_x.setDouble(customizablePacket, Math.floor(player.getLocation().getX()) + 0.5);
						PacketPlayOutEntityTeleport_y.setDouble(customizablePacket, Math.floor(player.getLocation().getY()));
						PacketPlayOutEntityTeleport_z.setDouble(customizablePacket, Math.floor(player.getLocation().getZ()) + 0.5);
					} else {
						PacketPlayOutEntityTeleport_x.setInt(customizablePacket, (int)((Math.floor(player.getLocation().getX()) + 0.5) * 32));
						PacketPlayOutEntityTeleport_y.setInt(customizablePacket, (int)(Math.floor(player.getLocation().getY()) * 32));
						PacketPlayOutEntityTeleport_z.setInt(customizablePacket, (int)((Math.floor(player.getLocation().getZ()) + 0.5) * 32));
					}
					return customizablePacket;
				}
			}
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutUpdateAttributes(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutUpdateAttributes_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof ObjectDisguise) {
			return null;
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutCollect(final Player observer, final Object packet) throws Exception {
		final Player player = EntityIdList.getPlayerByEntityId(PacketPlayOutCollect_entityId.getInt(packet));
		if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof ObjectDisguise) {
			return null;
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutNamedSoundEffect(final Player observer, final Object packet) throws Exception {
		String soundEffect = soundEffectToString(PacketPlayOutNamedSoundEffect_soundEffect.get(packet));
		if(Sounds.isSoundFromPlayer(soundEffect)) {
			Object entityHuman = VersionHelper.require1_9() ? World_findNearbyPlayer.invoke(Entity_world.get(CraftPlayer_getHandle.invoke(observer)), PacketPlayOutNamedSoundEffect_x.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_y.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_z.getInt(packet) / 8.0, 1.0, false) : World_findNearbyPlayer.invoke(Entity_world.get(CraftPlayer_getHandle.invoke(observer)), PacketPlayOutNamedSoundEffect_x.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_y.getInt(packet) / 8.0, PacketPlayOutNamedSoundEffect_z.getInt(packet) / 8.0, 1.0);
			if(EntityPlayer.isInstance(entityHuman)) {
				final Player player = (Player)EntityPlayer_getBukkitEntity.invoke(entityHuman);
				if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer)) {
					if(DisguiseManager.getDisguise(player) instanceof MobDisguise) {
						String newSoundEffect = Sounds.replaceSoundFromPlayer(soundEffect, ((MobDisguise)DisguiseManager.getDisguise(player)));
						if(newSoundEffect != null) {
							Object customizablePacket = clonePacket(packet);
							PacketPlayOutNamedSoundEffect_soundEffect.set(customizablePacket, soundEffectFromString(newSoundEffect));
							return customizablePacket;
						}
						return null;
					} else if(DisguiseManager.getDisguise(player) instanceof ObjectDisguise) {
						return null;
					}
				}
			}
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutScoreboardTeam(final Player observer, final Object packet) throws Exception {
		if(modifyScoreboardPackets && ObjectUtil.equals(PacketPlayOutScoreboardTeam_action.getInt(packet), 0, 3, 4)) {
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
			return customizablePacket;
		}
		return packet;
	}
	
	private static Object handlePacketPlayOutScoreboardScore(final Player observer, final Object packet) throws Exception {
		if(modifyScoreboardPackets) {
			Player player = Bukkit.getPlayer((String)PacketPlayOutScoreboardScore_entry.get(packet));
			if(player != null && player != observer && DisguiseManager.isDisguisedTo(player, observer) && DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
				Object customizablePacket = clonePacket(packet);
				PacketPlayOutScoreboardScore_entry.set(customizablePacket, ((PlayerDisguise)DisguiseManager.getDisguise(player)).getDisplayName());
				return customizablePacket;
			}
		}
		return packet;
	}
	
}