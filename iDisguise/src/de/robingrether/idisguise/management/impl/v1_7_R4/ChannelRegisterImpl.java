package de.robingrether.idisguise.management.impl.v1_7_R4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.WatchableObject;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R4.EnumEntityUseAction;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayOutBed;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityLook;
import net.minecraft.server.v1_7_R4.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.ChannelRegister;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.idisguise.management.Sounds;
import de.robingrether.util.Cloner;
import de.robingrether.util.ObjectUtil;
import de.robingrether.util.StringUtil;

public class ChannelRegisterImpl extends ChannelRegister {
	
	private final Map<Player, PlayerConnectionInjected> registeredHandlers = new ConcurrentHashMap<Player, PlayerConnectionInjected>();
	private Field fieldName, fieldEntityIdBed, fieldAnimation, fieldEntityIdAnimation, fieldEntityIdMetadata, fieldEntityIdEntity, fieldYawEntity, fieldEntityIdTeleport, fieldYawTeleport, fieldYawSpawnEntityLiving, fieldListMetadata, fieldEntityIdUseEntity, fieldEntityIdNamedSpawn, fieldSoundEffect, fieldX, fieldY, fieldZ;
	private Cloner<PacketPlayOutPlayerInfo> clonerPlayerInfo = new PlayerInfoCloner();
	private Cloner<PacketPlayOutEntityMetadata> clonerEntityMetadata = new EntityMetadataCloner();
	private Cloner<PacketPlayOutEntity> clonerEntity = new EntityCloner();
	private Cloner<PacketPlayOutEntityTeleport> clonerEntityTeleport = new EntityTeleportCloner();
	private Cloner<PacketPlayOutNamedSoundEffect> clonerSoundEffect = new SoundEffectCloner();
	
	public ChannelRegisterImpl() {
		try {
			fieldName = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
			fieldName.setAccessible(true);
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
			fieldYawSpawnEntityLiving = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("i");
			fieldYawSpawnEntityLiving.setAccessible(true);
			fieldListMetadata = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
			fieldListMetadata.setAccessible(true);
			fieldEntityIdUseEntity = PacketPlayInUseEntity.class.getDeclaredField("a");
			fieldEntityIdUseEntity.setAccessible(true);
			fieldEntityIdNamedSpawn = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
			fieldEntityIdNamedSpawn.setAccessible(true);
			fieldSoundEffect = PacketPlayOutNamedSoundEffect.class.getDeclaredField("a");
			fieldSoundEffect.setAccessible(true);
			fieldX = PacketPlayOutNamedSoundEffect.class.getDeclaredField("b");
			fieldX.setAccessible(true);
			fieldY = PacketPlayOutNamedSoundEffect.class.getDeclaredField("c");
			fieldY.setAccessible(true);
			fieldZ = PacketPlayOutNamedSoundEffect.class.getDeclaredField("d");
			fieldZ.setAccessible(true);
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
			super(MinecraftServer.getServer(), playerConnection.networkManager, playerConnection.player);
			this.player = player;
		}
	
		public synchronized void a(PacketPlayInUseEntity packet) {
			try {
				Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdUseEntity.getInt(packet));
				if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !packet.c().equals(EnumEntityUseAction.ATTACK)) {
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
						Packet packetSpawn = (Packet)DisguiseManager.instance.getSpawnPacket(player);
						if(packetSpawn instanceof PacketPlayOutSpawnEntityLiving && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
							byte yaw = fieldYawSpawnEntityLiving.getByte(packetSpawn);
							if(yaw < 0) {
								yaw += 128;
							} else {
								yaw -= 128;
							}
							fieldYawSpawnEntityLiving.set(packetSpawn, yaw);
						}
						super.sendPacket(packetSpawn);
						DisguiseManager.instance.updateAttributes(player, this.player);
						return;
					}
				} else if(object instanceof PacketPlayOutPlayerInfo) {
					PacketPlayOutPlayerInfo packet = clonerPlayerInfo.clone((PacketPlayOutPlayerInfo)object);
					Player player = Bukkit.getPlayer((String)fieldName.get(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						if(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise) {
							fieldName.set(packet, ((PlayerDisguise)DisguiseManager.instance.getDisguise(player)).getName());
							super.sendPacket(packet);
							return;
						} else {
							return;
						}
					}
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
						if(DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
							List<WatchableObject> list = (List<WatchableObject>)fieldListMetadata.get(packet);
							List<WatchableObject> remove = new ArrayList<WatchableObject>();
							for(WatchableObject metadata : list) {
								if(metadata.a() > 9) {
									remove.add(metadata);
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
				} else if(object instanceof PacketPlayOutNamedSoundEffect) {
					PacketPlayOutNamedSoundEffect packet = clonerSoundEffect.clone((PacketPlayOutNamedSoundEffect)object);
					String soundEffect = (String)fieldSoundEffect.get(packet);
					if(StringUtil.equals(soundEffect, "game.player.die", "game.player.hurt.fall.big", "game.player.hurt.fall.small", "game.player.hurt", "game.player.swim.splash", "game.player.swim")) {
						EntityHuman nearestHuman = ((CraftPlayer)this.player).getHandle().world.findNearbyPlayer(fieldX.getInt(packet) / 8.0, fieldY.getInt(packet) / 8.0, fieldZ.getInt(packet) / 8.0, 1.0);
						if(nearestHuman instanceof EntityPlayer) {
							Player player = ((EntityPlayer)nearestHuman).getBukkitEntity();
							if(player != null && player != this.player && DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
								MobDisguise disguise = (MobDisguise)DisguiseManager.instance.getDisguise(player);
								String replacementSoundEffect = null;
								switch(soundEffect) {
									case "game.player.die":
										replacementSoundEffect = Sounds.getDeath(disguise);
										break;
									case "game.player.hurt.fall.big":
										replacementSoundEffect = Sounds.getFallBig(disguise);
										break;
									case "game.player.hurt.fall.small":
										replacementSoundEffect = Sounds.getFallSmall(disguise);
										break;
									case "game.player.hurt":
										replacementSoundEffect = Sounds.getHit(disguise);
										break;
									case "game.player.swim.splash":
										replacementSoundEffect = Sounds.getSplash(disguise);
										break;
									case "game.player.swim":
										replacementSoundEffect = Sounds.getSwim(disguise);
										break;
								}
								if(replacementSoundEffect != null) {
									fieldSoundEffect.set(packet, replacementSoundEffect);
									super.sendPacket(packet);
								}
								return;
							}
						}
					}
				}
				super.sendPacket(object);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
	}
	
	private class PlayerInfoCloner extends Cloner<PacketPlayOutPlayerInfo> {
		
		private Field[] fields;
		
		private PlayerInfoCloner() {
			try {
				fields = PacketPlayOutPlayerInfo.class.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
				}
			} catch(Exception e) {
			}
		}
		
		public PacketPlayOutPlayerInfo clone(PacketPlayOutPlayerInfo original) {
			PacketPlayOutPlayerInfo clone = new PacketPlayOutPlayerInfo();
			try {
				for(Field field : fields) {
					field.set(clone, field.get(original));
				}
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
				b.set(clone, ((ArrayList<WatchableObject>)b.get(original)).clone());
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