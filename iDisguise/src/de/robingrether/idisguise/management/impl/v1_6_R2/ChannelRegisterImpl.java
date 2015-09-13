package de.robingrether.idisguise.management.impl.v1_6_R2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.Packet18ArmAnimation;
import net.minecraft.server.v1_6_R2.Packet201PlayerInfo;
import net.minecraft.server.v1_6_R2.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;
import net.minecraft.server.v1_6_R2.Packet30Entity;
import net.minecraft.server.v1_6_R2.Packet31RelEntityMove;
import net.minecraft.server.v1_6_R2.Packet32EntityLook;
import net.minecraft.server.v1_6_R2.Packet33RelEntityMoveLook;
import net.minecraft.server.v1_6_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_6_R2.Packet70Bed;
import net.minecraft.server.v1_6_R2.Packet7UseEntity;
import net.minecraft.server.v1_6_R2.PlayerConnection;
import net.minecraft.server.v1_6_R2.WatchableObject;
import net.minecraft.server.v1_6_R2.Packet;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.ChannelRegister;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.util.Cloner;
import de.robingrether.util.ObjectUtil;

public class ChannelRegisterImpl extends ChannelRegister {
	
	private final Map<Player, PlayerConnectionInjected> registeredHandlers = new ConcurrentHashMap<Player, PlayerConnectionInjected>();
	private Field fieldListMetadata;
	private Cloner<Packet201PlayerInfo> clonerPlayerInfo = new PlayerInfoCloner();
	private Cloner<Packet40EntityMetadata> clonerEntityMetadata = new EntityMetadataCloner();
	private Cloner<Packet30Entity> clonerEntity = new EntityCloner();
	private Cloner<Packet34EntityTeleport> clonerEntityTeleport = new EntityTeleportCloner();
	
	public ChannelRegisterImpl() {
		try {
			fieldListMetadata = Packet40EntityMetadata.class.getDeclaredField("b");
			fieldListMetadata.setAccessible(true);
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
		
		public synchronized void sendPacket(Packet packet) {
			sendPacket(packet, false);
		}
		
		public synchronized void sendPacket(Packet packet, boolean fromPlugin) {
			if(fromPlugin) {
				super.sendPacket(packet);
				return;
			}
			try {
				if(packet instanceof Packet20NamedEntitySpawn) {
					Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn)packet;
					Player player = PlayerHelper.instance.getPlayerByEntityId(packet20.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						Packet packetSpawn = (Packet)DisguiseManager.instance.getSpawnPacket(player);
						if(packetSpawn instanceof Packet24MobSpawn && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
							byte yaw = ((Packet24MobSpawn)packetSpawn).i;
							if(yaw < 0) {
								yaw += 128;
							} else {
								yaw -= 128;
							}
							((Packet24MobSpawn)packetSpawn).i = yaw;
						}
						super.sendPacket(packetSpawn);
						DisguiseManager.instance.updateAttributes(player, this.player);
						return;
					}
				} else if(packet instanceof Packet201PlayerInfo) {
					Packet201PlayerInfo packet201 = clonerPlayerInfo.clone((Packet201PlayerInfo)packet);
					Player player = Bukkit.getPlayer(packet201.a);
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						if(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise) {
							packet201.a = ((PlayerDisguise)DisguiseManager.instance.getDisguise(player)).getName();
							super.sendPacket(packet201);
							return;
						} else {
							return;
						}
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
						if(DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
							List<WatchableObject> list = (List<WatchableObject>)fieldListMetadata.get(packet40);
							List<WatchableObject> remove = new ArrayList<WatchableObject>();
							for(WatchableObject metadata : list) {
								if(metadata.a() == 6) {
									if(DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
										metadata.a((Float)((Float)metadata.b() * 10));
									} else if(DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.WITHER)) {
										metadata.a((Float)((Float)metadata.b() * 15));
									}
								} else if(metadata.a() > 9) {
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
	
}