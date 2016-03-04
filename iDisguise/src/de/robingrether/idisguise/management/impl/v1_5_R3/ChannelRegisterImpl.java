package de.robingrether.idisguise.management.impl.v1_5_R3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.minecraft.server.v1_5_R3.MinecraftServer;
import net.minecraft.server.v1_5_R3.Packet18ArmAnimation;
import net.minecraft.server.v1_5_R3.Packet201PlayerInfo;
import net.minecraft.server.v1_5_R3.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_5_R3.Packet24MobSpawn;
import net.minecraft.server.v1_5_R3.Packet30Entity;
import net.minecraft.server.v1_5_R3.Packet31RelEntityMove;
import net.minecraft.server.v1_5_R3.Packet32EntityLook;
import net.minecraft.server.v1_5_R3.Packet33RelEntityMoveLook;
import net.minecraft.server.v1_5_R3.Packet34EntityTeleport;
import net.minecraft.server.v1_5_R3.Packet40EntityMetadata;
import net.minecraft.server.v1_5_R3.Packet62NamedSoundEffect;
import net.minecraft.server.v1_5_R3.Packet70Bed;
import net.minecraft.server.v1_5_R3.Packet7UseEntity;
import net.minecraft.server.v1_5_R3.PlayerConnection;
import net.minecraft.server.v1_5_R3.WatchableObject;
import net.minecraft.server.v1_5_R3.EntityHuman;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.Packet;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
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
	private Field fieldListMetadata, fieldSoundEffect, fieldX, fieldY, fieldZ;
	private Cloner<Packet201PlayerInfo> clonerPlayerInfo = new PlayerInfoCloner();
	private Cloner<Packet40EntityMetadata> clonerEntityMetadata = new EntityMetadataCloner();
	private Cloner<Packet30Entity> clonerEntity = new EntityCloner();
	private Cloner<Packet34EntityTeleport> clonerEntityTeleport = new EntityTeleportCloner();
	private Cloner<Packet62NamedSoundEffect> clonerSoundEffect = new SoundEffectCloner();
	
	public ChannelRegisterImpl() {
		try {
			fieldListMetadata = Packet40EntityMetadata.class.getDeclaredField("b");
			fieldListMetadata.setAccessible(true);
			fieldSoundEffect = Packet62NamedSoundEffect.class.getDeclaredField("a");
			fieldSoundEffect.setAccessible(true);
			fieldX = Packet62NamedSoundEffect.class.getDeclaredField("b");
			fieldX.setAccessible(true);
			fieldY = Packet62NamedSoundEffect.class.getDeclaredField("c");
			fieldY.setAccessible(true);
			fieldZ = Packet62NamedSoundEffect.class.getDeclaredField("d");
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
		
		public synchronized void a(Packet7UseEntity packet7) {
			try {
				Player player = PlayerHelper.instance.getPlayerByEntityId(packet7.target);
				if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && packet7.action == 0) {
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
				super.a(packet7);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
		public synchronized void sendPacketFromPlugin(Packet packet) {
			super.sendPacket(packet);
		}
		
		public synchronized void sendPacket(Packet packet) {
			try {
				if(packet instanceof Packet20NamedEntitySpawn) {
					Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn)packet;
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet20.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						Packet[] packetSpawn = (Packet[])PacketHelper.instance.getPackets(player);
						if(packetSpawn[0] instanceof Packet24MobSpawn && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
							byte yaw = ((Packet24MobSpawn)packetSpawn[0]).i;
							if(yaw < 0) {
								yaw += 128;
							} else {
								yaw -= 128;
							}
							((Packet24MobSpawn)packetSpawn[0]).i = yaw;
						}
						for(Packet p : packetSpawn) {
							super.sendPacket(p);
						}
						return;
					}
				} else if(packet instanceof Packet201PlayerInfo) {
					Packet201PlayerInfo packet201 = clonerPlayerInfo.clone((Packet201PlayerInfo)packet);
					OfflinePlayer player = Bukkit.getOfflinePlayer(packet201.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						String name = (String)PacketHelper.instance.getPlayerInfo(player, null, 0, null);
						if(name != null) {
							packet201.a = name;
							super.sendPacket(packet201);
						}
						return;
					}
				} else if(packet instanceof Packet70Bed) {
					Packet70Bed packet70 = (Packet70Bed)packet;
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet70.b);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
						return;
					}
				} else if(packet instanceof Packet18ArmAnimation) {
					Packet18ArmAnimation packet18 = (Packet18ArmAnimation)packet;
					if(packet18.b == 3) {
						Player player = PlayerHelper.instance.getPlayerByEntityId(packet18.a);
						if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && !(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
							return;
						}
					}
				} else if(packet instanceof Packet40EntityMetadata) {
					Packet40EntityMetadata packet40 = clonerEntityMetadata.clone((Packet40EntityMetadata)packet);
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet40.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						if(!(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise)) {
							boolean living = DisguiseManager.instance.getDisguise(player) instanceof MobDisguise;
							List<WatchableObject> list = (List<WatchableObject>)fieldListMetadata.get(packet40);
							List<WatchableObject> remove = new ArrayList<WatchableObject>();
							for(WatchableObject metadata : list) {
								if(metadata.a() > 0 && !(living && metadata.a() >= 6 && metadata.a() <= 9)) {
									remove.add(metadata);
								}
							}
							list.removeAll(remove);
							super.sendPacket(packet40);
							return;
						}
					}
				} else if(packet instanceof Packet32EntityLook) {
					Packet32EntityLook packet32 = (Packet32EntityLook)clonerEntity.clone((Packet32EntityLook)packet);
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet32.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = packet32.e;
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						packet32.e = yaw;
						super.sendPacket(packet32);
						return;
					}
				} else if(packet instanceof Packet33RelEntityMoveLook) {
					Packet33RelEntityMoveLook packet33 = (Packet33RelEntityMoveLook)clonerEntity.clone((Packet33RelEntityMoveLook)packet);
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet33.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = packet33.e;
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						packet33.e = yaw;
						super.sendPacket(packet33);
						return;
					}
				} else if(packet instanceof Packet34EntityTeleport) {
					Packet34EntityTeleport packet34 = clonerEntityTeleport.clone((Packet34EntityTeleport)packet);
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet34.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = packet34.e;
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						packet34.e = yaw;
						super.sendPacket(packet34);
						return;
					}
				} else if(Sounds.isEnabled() && packet instanceof Packet62NamedSoundEffect) {
					Packet62NamedSoundEffect packet62 = clonerSoundEffect.clone((Packet62NamedSoundEffect)packet);
					String soundEffect = (String)fieldSoundEffect.get(packet62);
					if(StringUtil.equals(soundEffect, "game.player.die", "damage.fallbig", "damage.fallsmall", "damage.hit", "liquid.splash", "liquid.swim")) {
						EntityHuman nearestHuman = ((CraftPlayer)this.player).getHandle().world.findNearbyPlayer(fieldX.getInt(packet62) / 8.0, fieldY.getInt(packet62) / 8.0, fieldZ.getInt(packet62) / 8.0, 1.0);
						if(nearestHuman instanceof EntityPlayer) {
							Player player = ((EntityPlayer)nearestHuman).getBukkitEntity();
							if(player != null && player != this.player) {
								if(DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
									MobDisguise disguise = (MobDisguise)DisguiseManager.instance.getDisguise(player);
									String replacementSoundEffect = null;
									switch(soundEffect) {
										case "game.player.die":
											replacementSoundEffect = Sounds.getDeath(disguise);
											break;
										case "damage.fallbig":
											replacementSoundEffect = Sounds.getFallBig(disguise);
											break;
										case "damage.fallsmall":
											replacementSoundEffect = Sounds.getFallSmall(disguise);
											break;
										case "damage.hit":
											replacementSoundEffect = Sounds.getHit(disguise);
											break;
										case "liquid.splash":
											replacementSoundEffect = Sounds.getSplash(disguise);
											break;
										case "liquid.swim":
											replacementSoundEffect = Sounds.getSwim(disguise);
											break;
									}
									if(replacementSoundEffect != null) {
										fieldSoundEffect.set(packet62, replacementSoundEffect);
										super.sendPacket(packet62);
									}
									return;
								} else if(DisguiseManager.instance.getDisguise(player) instanceof ObjectDisguise) {
									return;
								}
							}
						}
					}
				}
				super.sendPacket(packet);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
	}
	
	private class PlayerInfoCloner extends Cloner<Packet201PlayerInfo> {
		
		private PlayerInfoCloner() {
		}
		
		public Packet201PlayerInfo clone(Packet201PlayerInfo original) {
			return new Packet201PlayerInfo(original.a, original.b, original.c);
		}
		
	}
	
	private class EntityMetadataCloner extends Cloner<Packet40EntityMetadata> {
		
		private Field b;
		
		private EntityMetadataCloner() {
			try {
				b = Packet40EntityMetadata.class.getDeclaredField("b");
				b.setAccessible(true);
			} catch(Exception e) {
			}
		}
		
		public Packet40EntityMetadata clone(Packet40EntityMetadata original) {
			Packet40EntityMetadata clone = new Packet40EntityMetadata();
			clone.a = original.a;
			try {
				b.set(clone, ((ArrayList<WatchableObject>)b.get(original)).clone());
			} catch(Exception e) {
			}
			return clone;
		}
		
	}
	
	private class EntityCloner extends Cloner<Packet30Entity> {
		
		private EntityCloner() {
		}
		
		public Packet30Entity clone(Packet30Entity original) {
			if(original instanceof Packet31RelEntityMove) {
				return new Packet31RelEntityMove(original.a, original.b, original.c, original.d);
			} else if(original instanceof Packet32EntityLook) {
				return new Packet32EntityLook(original.a, original.e, original.f);
			} else if(original instanceof Packet33RelEntityMoveLook) {
				return new Packet33RelEntityMoveLook(original.a, original.b, original.c, original.d, original.e, original.f);
			} else {
				return new Packet30Entity(original.a);
			}
		}
		
	}
	
	private class EntityTeleportCloner extends Cloner<Packet34EntityTeleport> {
		
		private EntityTeleportCloner() {
		}
		
		public Packet34EntityTeleport clone(Packet34EntityTeleport original) {
			return new Packet34EntityTeleport(original.a, original.b, original.c, original.d, original.e, original.f);
		}
		
	}
	
	private class SoundEffectCloner extends Cloner<Packet62NamedSoundEffect> {
		
		private Field[] fields;
		
		private SoundEffectCloner() {
			try {
				fields = Packet62NamedSoundEffect.class.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
				}
			} catch(Exception e) {
			}
		}
		
		public Packet62NamedSoundEffect clone(Packet62NamedSoundEffect original) {
			Packet62NamedSoundEffect clone = new Packet62NamedSoundEffect();
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