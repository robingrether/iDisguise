package de.robingrether.idisguise.management.impl.v1_9_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.minecraft.server.v1_9_R1.CancelledPacketHandleException;
import net.minecraft.server.v1_9_R1.DataWatcher.Item;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftKey;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_9_R1.PacketPlayInUseEntity.EnumEntityUseAction;
import net.minecraft.server.v1_9_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_9_R1.PacketPlayOutBed;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_9_R1.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_9_R1.PlayerConnection;
import net.minecraft.server.v1_9_R1.SoundEffect;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.ObjectDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.ChannelRegister;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PacketHelper;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.Sounds;
import de.robingrether.util.Cloner;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.StringUtil;

public class ChannelRegisterImpl extends ChannelRegister {
	
	private final Map<Player, PlayerConnectionInjected> registeredHandlers = new ConcurrentHashMap<Player, PlayerConnectionInjected>();
	private Field fieldListInfo, fieldEntityIdBed, fieldAnimation, fieldEntityIdAnimation, fieldEntityIdMetadata, fieldEntityIdEntity, fieldYawEntity, fieldEntityIdTeleport, fieldYawTeleport, fieldYawSpawnEntityLiving, fieldListMetadata, fieldEntityIdUseEntity, fieldEntityIdNamedSpawn, fieldSoundEffect, /*fieldSoundCategory, */fieldX, fieldY, fieldZ, fieldObjectMetadata, fieldEntityIdAttributes;
	private Cloner<PacketPlayOutPlayerInfo> clonerPlayerInfo = new PlayerInfoCloner();
	private Cloner<PacketPlayOutEntityMetadata> clonerEntityMetadata = new EntityMetadataCloner();
	private Cloner<PacketPlayOutEntity> clonerEntity = new EntityCloner();
	private Cloner<PacketPlayOutEntityTeleport> clonerEntityTeleport = new EntityTeleportCloner();
	private Cloner<PacketPlayOutNamedSoundEffect> clonerSoundEffect = new SoundEffectCloner();
	
	public ChannelRegisterImpl() {
		try {
			fieldListInfo = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			fieldListInfo.setAccessible(true);
			fieldEntityIdBed = PacketPlayOutBed.class.getDeclaredField("a");
			fieldEntityIdBed.setAccessible(true);
			fieldAnimation = PacketPlayOutAnimation.class.getDeclaredField("b");
			fieldAnimation.setAccessible(true);
			fieldEntityIdAnimation = PacketPlayOutAnimation.class.getDeclaredField("a");
			fieldEntityIdAnimation.setAccessible(true);
			fieldEntityIdMetadata = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
			fieldEntityIdMetadata.setAccessible(true);
			fieldEntityIdEntity = PacketPlayOutEntity.class.getDeclaredField("a");
			fieldEntityIdEntity.setAccessible(true);
			fieldYawEntity = PacketPlayOutEntity.class.getDeclaredField("e");
			fieldYawEntity.setAccessible(true);
			fieldEntityIdTeleport = PacketPlayOutEntityTeleport.class.getDeclaredField("a");
			fieldEntityIdTeleport.setAccessible(true);
			fieldYawTeleport = PacketPlayOutEntityTeleport.class.getDeclaredField("e");
			fieldYawTeleport.setAccessible(true);
			fieldYawSpawnEntityLiving = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("j");
			fieldYawSpawnEntityLiving.setAccessible(true);
			fieldListMetadata = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
			fieldListMetadata.setAccessible(true);
			fieldEntityIdUseEntity = PacketPlayInUseEntity.class.getDeclaredField("a");
			fieldEntityIdUseEntity.setAccessible(true);
			fieldEntityIdNamedSpawn = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
			fieldEntityIdNamedSpawn.setAccessible(true);
			fieldSoundEffect = PacketPlayOutNamedSoundEffect.class.getDeclaredField("a");
			fieldSoundEffect.setAccessible(true);
			/*fieldSoundCategory = PacketPlayOutNamedSoundEffect.class.getDeclaredField("b");
			fieldSoundCategory.setAccessible(true);*/
			fieldX = PacketPlayOutNamedSoundEffect.class.getDeclaredField("c");
			fieldX.setAccessible(true);
			fieldY = PacketPlayOutNamedSoundEffect.class.getDeclaredField("d");
			fieldY.setAccessible(true);
			fieldZ = PacketPlayOutNamedSoundEffect.class.getDeclaredField("e");
			fieldZ.setAccessible(true);
			fieldObjectMetadata = Item.class.getDeclaredField("a");
			fieldObjectMetadata.setAccessible(true);
			fieldEntityIdAttributes = PacketPlayOutUpdateAttributes.class.getDeclaredField("a");
			fieldEntityIdAttributes.setAccessible(true);
		} catch(Exception e) {
		}
	}
	
	public synchronized void registerHandler(Player player) {
		try {
			PlayerConnectionInjected playerConnection = new PlayerConnectionInjected(player, ((CraftPlayer)player).getHandle().playerConnection);
			((CraftPlayer)player).getHandle().playerConnection = playerConnection;
			registeredHandlers.put(player, playerConnection);
		} catch(Exception e) {
		}
	}
	
	public synchronized void unregisterHandler(Player player) {
		try {
			PlayerConnectionInjected playerConnection = registeredHandlers.remove(player);
		} catch(Exception e) {
		}
	}
	
	public void registerOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			registerHandler(player);
		}
	}
	
	public class PlayerConnectionInjected extends PlayerConnection {
		
		private Player player;
		
		private PlayerConnectionInjected(Player player, PlayerConnection playerConnection) {
			super(((CraftServer)Bukkit.getServer()).getServer(), playerConnection.networkManager, playerConnection.player);
			this.player = player;
		}
		
		public synchronized void a(PacketPlayInUseEntity packet) {
			try {
				Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdUseEntity.getInt(packet));
				if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !packet.a().equals(EnumEntityUseAction.ATTACK)) {
					if(ObjectUtil.equals(DisguiseManager.instance.getDisguise(player).getType(), DisguiseType.SHEEP, DisguiseType.WOLF)) {
						final Player observer = this.player;
						final Player observed = player;
						BukkitRunnable runnable = new BukkitRunnable() {
							public void run() {
								DisguiseManager.instance.disguise(observed, DisguiseManager.instance.getDisguise(observed));
								observer.updateInventory();
							}
						};
						runnable.runTaskLater(Bukkit.getPluginManager().getPlugin("iDisguise"), 2L);
					}
					return;
				}
				super.a(packet);
			} catch(Exception e) {
				if(e instanceof CancelledPacketHandleException) {
					return;
				}
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
		public synchronized void sendPacketFromPlugin(Packet packet) {
			super.sendPacket(packet);
		}
		
		public synchronized void sendPacket(Packet object) {
			try {
				if(object instanceof PacketPlayOutNamedEntitySpawn) {
					PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdNamedSpawn.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						Packet<?>[] packetSpawn = (Packet<?>[])DisguiseManager.instance.getSpawnPacket(player);
						if(packetSpawn[0] instanceof PacketPlayOutSpawnEntityLiving && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
							byte yaw = fieldYawSpawnEntityLiving.getByte(packetSpawn[0]);
							if(yaw < 0) {
								yaw += 128;
							} else {
								yaw -= 128;
							}
							fieldYawSpawnEntityLiving.set(packetSpawn[0], yaw);
						}
						for(Packet<?> p : packetSpawn) {
							super.sendPacket(p);
						}
						return;
					}
				} else if(object instanceof PacketPlayOutPlayerInfo) {
					PacketPlayOutPlayerInfo packet = clonerPlayerInfo.clone((PacketPlayOutPlayerInfo)object);
					List<PlayerInfoData> list = (List<PlayerInfoData>)fieldListInfo.get(packet);
					List<PlayerInfoData> add = new ArrayList<PlayerInfoData>();
					List<PlayerInfoData> remove = new ArrayList<PlayerInfoData>();
					for(PlayerInfoData playerInfo : list) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(playerInfo.a().getId());
						if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
							PlayerInfoData newPlayerInfo = (PlayerInfoData)PacketHelper.instance.getPlayerInfo(player, packet, playerInfo.b(), playerInfo.c());
							remove.add(playerInfo);
							if(newPlayerInfo != null) {
								add.add(newPlayerInfo);
							}
						}
					}
					list.removeAll(remove);
					list.addAll(add);
					super.sendPacket(packet);
					return;
				} else if(object instanceof PacketPlayOutBed) {
					PacketPlayOutBed packet = (PacketPlayOutBed)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdBed.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
						return;
					}
				} else if(object instanceof PacketPlayOutAnimation) {
					PacketPlayOutAnimation packet = (PacketPlayOutAnimation)object;
					int animation = fieldAnimation.getInt(packet);
					if(animation == 2) {
						Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdAnimation.getInt(packet));
						if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
							return;
						}
					}
				} else if(object instanceof PacketPlayOutEntityMetadata) {
					PacketPlayOutEntityMetadata packet = clonerEntityMetadata.clone((PacketPlayOutEntityMetadata)object);
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdMetadata.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						if(!(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
							boolean living = DisguiseManager.instance.getDisguise(player) instanceof MobDisguise;
							List<Item> list = (List<Item>)fieldListMetadata.get(packet);
							List<Item> remove = new ArrayList<Item>();
							for(Item metadataItem : list) {
								DataWatcherObject metadataObject = (DataWatcherObject)fieldObjectMetadata.get(metadataItem);
								if(metadataObject.a() > 0 && !(living && metadataObject.a() >= 6 && metadataObject.a() <= 9)) {
									remove.add(metadataItem);
								}
							}
							list.removeAll(remove);
							super.sendPacket(packet);
							return;
						}
					}
				} else if(object instanceof PacketPlayOutEntityLook) {
					PacketPlayOutEntityLook packet = (PacketPlayOutEntityLook)clonerEntity.clone((PacketPlayOutEntityLook)object);
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawEntity.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawEntity.set(packet, yaw);
						super.sendPacket(packet);
						return;
					}
				} else if(object instanceof PacketPlayOutRelEntityMoveLook) {
					PacketPlayOutRelEntityMoveLook packet = (PacketPlayOutRelEntityMoveLook)clonerEntity.clone((PacketPlayOutRelEntityMoveLook)object);
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawEntity.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawEntity.set(packet, yaw);
						super.sendPacket(packet);
						return;
					}
				} else if(object instanceof PacketPlayOutEntityTeleport) {
					PacketPlayOutEntityTeleport packet = clonerEntityTeleport.clone((PacketPlayOutEntityTeleport)object);
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdTeleport.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawTeleport.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawTeleport.set(packet, yaw);
						super.sendPacket(packet);
						return;
					}
				} else if(Sounds.isEnabled() && object instanceof PacketPlayOutNamedSoundEffect) {
					PacketPlayOutNamedSoundEffect packet = clonerSoundEffect.clone((PacketPlayOutNamedSoundEffect)object);
					String soundEffect = SoundEffect.a.b((SoundEffect)fieldSoundEffect.get(packet)).a();
					if(StringUtil.equals(soundEffect, "entity.player.death", "entity.player.big_fall", "entity.player.small_fall", "entity.player.hurt", "entity.player.splash", "entity.player.swim")) {
						EntityHuman nearestHuman = ((CraftPlayer)this.player).getHandle().world.a(fieldX.getInt(packet) / 8.0, fieldY.getInt(packet) / 8.0, fieldZ.getInt(packet) / 8.0, 1.0, false);
						if(nearestHuman instanceof EntityPlayer) {
							Player player = ((EntityPlayer)nearestHuman).getBukkitEntity();
							if(player != null && player != this.player) {
								if(DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
									MobDisguise disguise = (MobDisguise)DisguiseManager.instance.getDisguise(player);
									String replacementSoundEffect = null;
									switch(soundEffect) {
										case "entity.player.death":
											replacementSoundEffect = Sounds.getDeath(disguise);
											break;
										case "entity.player.big_fall":
											replacementSoundEffect = Sounds.getFallBig(disguise);
											break;
										case "entity.player.small_fall":
											replacementSoundEffect = Sounds.getFallSmall(disguise);
											break;
										case "entity.player.hurt":
											replacementSoundEffect = Sounds.getHit(disguise);
											break;
										case "entity.player.splash":
											replacementSoundEffect = Sounds.getSplash(disguise);
											break;
										case "entity.player.swim":
											replacementSoundEffect = Sounds.getSwim(disguise);
											break;
									}
									if(replacementSoundEffect != null) {
										fieldSoundEffect.set(packet, SoundEffect.a.get(new MinecraftKey(replacementSoundEffect.replace("mob", "entity"))));
										super.sendPacket(packet);
									}
									return;
								} else if(DisguiseManager.instance.getDisguise(player) instanceof ObjectDisguise) {
									return;
								}
							}
						}
					}
				} else if(object instanceof PacketPlayOutUpdateAttributes) {
					PacketPlayOutUpdateAttributes packet = (PacketPlayOutUpdateAttributes)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdAttributes.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.getDisguise(player) instanceof ObjectDisguise) {
						return;
					}
				}
				super.sendPacket(object);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
	}
	
	private class PlayerInfoCloner extends Cloner<PacketPlayOutPlayerInfo> {
		
		private Field a, b;
		
		private PlayerInfoCloner() {
			try {
				a = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
				a.setAccessible(true);
				b = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
				b.setAccessible(true);
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutPlayerInfo clone(PacketPlayOutPlayerInfo original) {
			PacketPlayOutPlayerInfo clone = new PacketPlayOutPlayerInfo();
			try {
				a.set(clone, a.get(original));
				b.set(clone, ((ArrayList<PlayerInfoData>)b.get(original)).clone());
			} catch(Exception e) {
			}
			return clone;
		}
		
	}
	
	private class EntityMetadataCloner extends Cloner<PacketPlayOutEntityMetadata> {
		
		private Field a, b;
		
		private EntityMetadataCloner() {
			try {
				a = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
				a.setAccessible(true);
				b = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
				b.setAccessible(true);
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutEntityMetadata clone(PacketPlayOutEntityMetadata original) {
			PacketPlayOutEntityMetadata clone = new PacketPlayOutEntityMetadata();
			try {
				a.set(clone, a.get(original));
				b.set(clone, ((ArrayList<Item>)b.get(original)).clone());
			} catch(Exception e) {
			}
			return clone;
		}
		
	}
	
	private class EntityCloner extends Cloner<PacketPlayOutEntity> {
		
		private Field[] fields;
		
		private EntityCloner() {
			try {
				fields = PacketPlayOutEntity.class.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
				}
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutEntity clone(PacketPlayOutEntity original) {
			try {
				PacketPlayOutEntity clone = original.getClass().newInstance();
				for(Field field : fields) {
					field.set(clone, field.get(original));
				}
				return clone;
			} catch(Exception e) {
				return null;
			}
		}
		
	}
	
	private class EntityTeleportCloner extends Cloner<PacketPlayOutEntityTeleport> {
		
		private Field[] fields;
		
		private EntityTeleportCloner() {
			try {
				fields = PacketPlayOutEntityTeleport.class.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
				}
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutEntityTeleport clone(PacketPlayOutEntityTeleport original) {
			PacketPlayOutEntityTeleport clone = new PacketPlayOutEntityTeleport();
			try {
				for(Field field : fields) {
					field.set(clone, field.get(original));
				}
			} catch(Exception e) {
			}
			return clone;
		}
		
	}
	
	private class SoundEffectCloner extends Cloner<PacketPlayOutNamedSoundEffect> {
		
		private Field[] fields;
		
		private SoundEffectCloner() {
			try {
				fields = PacketPlayOutNamedSoundEffect.class.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
				}
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutNamedSoundEffect clone(PacketPlayOutNamedSoundEffect original) {
			PacketPlayOutNamedSoundEffect clone = new PacketPlayOutNamedSoundEffect();
			try {
				for(Field field : fields) {
					field.set(clone, field.get(original));
				}
			} catch(Exception e) {
			}
			return clone;
		}
		
	}
	
}