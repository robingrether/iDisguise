package de.robingrether.idisguise.management.impl.v1_8_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.WatchableObject;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R1.EnumEntityUseAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R1.PacketPlayOutBed;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R1.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInfoData;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.idisguise.management.ChannelRegister;
import de.robingrether.idisguise.management.DisguiseManager;
import de.robingrether.idisguise.management.PlayerHelper;
import de.robingrether.util.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ChannelRegisterImpl extends ChannelRegister {
	
	private final Map<Player, ChannelHandler> registeredHandlers = new ConcurrentHashMap<Player, ChannelHandler>();
	private Field fieldChannel, fieldListInfo, fieldEntityIdBed, fieldAnimation, fieldEntityIdAnimation, fieldEntityIdMetadata, fieldEntityIdEntity, fieldYawEntity, fieldEntityIdTeleport, fieldYawTeleport, fieldYawSpawnEntityLiving, fieldListMetadata, fieldEntityIdUseEntity, fieldEntityIdNamedSpawn;
	
	public ChannelRegisterImpl() {
		try {
			fieldChannel = NetworkManager.class.getDeclaredField("i");
			fieldChannel.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldListInfo = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			fieldListInfo.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdBed = PacketPlayOutBed.class.getDeclaredField("a");
			fieldEntityIdBed.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldAnimation = PacketPlayOutAnimation.class.getDeclaredField("b");
			fieldAnimation.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdAnimation = PacketPlayOutAnimation.class.getDeclaredField("a");
			fieldEntityIdAnimation.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdMetadata = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
			fieldEntityIdMetadata.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdEntity = PacketPlayOutEntity.class.getDeclaredField("a");
			fieldEntityIdEntity.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldYawEntity = PacketPlayOutEntity.class.getDeclaredField("e");
			fieldYawEntity.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdTeleport = PacketPlayOutEntityTeleport.class.getDeclaredField("a");
			fieldEntityIdTeleport.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldYawTeleport = PacketPlayOutEntityTeleport.class.getDeclaredField("e");
			fieldYawTeleport.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldYawSpawnEntityLiving = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("i");
			fieldYawSpawnEntityLiving.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldListMetadata = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
			fieldListMetadata.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdUseEntity = PacketPlayInUseEntity.class.getDeclaredField("a");
			fieldEntityIdUseEntity.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldEntityIdNamedSpawn = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
			fieldEntityIdNamedSpawn.setAccessible(true);
		} catch(Exception e) {
		}
	}
	
	public synchronized void registerHandler(Player player) {
		try {
			ChannelHandler handler = new ChannelHandler(player);
			((Channel)fieldChannel.get(((CraftPlayer)player).getHandle().playerConnection.networkManager)).pipeline().addBefore("packet_handler", "iDisguise", handler);
			registeredHandlers.put(player, handler);
		} catch(Exception e) {
		}
	}
	
	public synchronized void unregisterHandler(Player player) {
		try {
			ChannelHandler handler = registeredHandlers.remove(player);
			((Channel)fieldChannel.get(((CraftPlayer)player).getHandle().playerConnection.networkManager)).pipeline().remove(handler);
		} catch(Exception e) {
		}
	}
	
	private class ChannelHandler extends ChannelDuplexHandler {
		
		private Player player;
		
		private ChannelHandler(Player player) {
			this.player = player;
		}
	
		public void channelRead(ChannelHandlerContext context, Object object) {
			try {
				if(object instanceof PacketPlayInUseEntity) {
					PacketPlayInUseEntity packet = (PacketPlayInUseEntity)object;
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
				}
				super.channelRead(context, object);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
		public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) {
			try {
				if(object instanceof PacketPlayOutNamedEntitySpawn) {
					PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdNamedSpawn.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						Packet packetSpawn = (Packet)DisguiseManager.instance.getSpawnPacket(player);
						if(packetSpawn instanceof PacketPlayOutNamedEntitySpawn) {
							super.write(context, packetSpawn, promise);
						} else {
							if(DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
								byte yaw = fieldYawSpawnEntityLiving.getByte(packetSpawn);
								if(yaw < 0) {
									yaw += 128;
								} else {
									yaw -= 128;
								}
								fieldYawSpawnEntityLiving.set(packetSpawn, yaw);
							}
							DisguiseManager.instance.sendPacketLater(this.player, packetSpawn, 1L);
						}
						DisguiseManager.instance.updateAttributes(player, this.player);
						return;
					}
				} else if(object instanceof PacketPlayOutPlayerInfo) {
					PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo)object;
					List<PlayerInfoData> list = (List<PlayerInfoData>)fieldListInfo.get(packet);
					List<PlayerInfoData> add = new ArrayList<PlayerInfoData>();
					for(Iterator<PlayerInfoData> iterator = list.iterator(); iterator.hasNext();) {
						PlayerInfoData playerInfo = iterator.next();
						Player player = Bukkit.getPlayer(playerInfo.a().getId());
						if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
							if(DisguiseManager.instance.getDisguise(player) instanceof PlayerDisguise) {
								PlayerInfoData newPlayerInfo = new PlayerInfoData(packet, (GameProfile)PlayerHelper.instance.getGameProfile(((PlayerDisguise)DisguiseManager.instance.getDisguise(player)).getName()), playerInfo.b(), playerInfo.c(), null);
								iterator.remove();
								add.add(newPlayerInfo);
							} else {
								iterator.remove();
							}
						}
					}
					list.addAll(add);
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
					PacketPlayOutEntityMetadata packet = (PacketPlayOutEntityMetadata)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdMetadata.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player)) {
						if(DisguiseManager.instance.getDisguise(player) instanceof MobDisguise) {
							List<WatchableObject> list = (List<WatchableObject>)fieldListMetadata.get(packet);
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
						}
					}
				} else if(object instanceof PacketPlayOutEntityLook) {
					PacketPlayOutEntityLook packet = (PacketPlayOutEntityLook)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawEntity.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawEntity.set(packet, yaw);
					}
				} else if(object instanceof PacketPlayOutRelEntityMoveLook) {
					PacketPlayOutRelEntityMoveLook packet = (PacketPlayOutRelEntityMoveLook)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawEntity.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawEntity.set(packet, yaw);
					}
				} else if(object instanceof PacketPlayOutEntityTeleport) {
					PacketPlayOutEntityTeleport packet = (PacketPlayOutEntityTeleport)object;
					Player player = PlayerHelper.instance.getPlayerByEntityId(fieldEntityIdTeleport.getInt(packet));
					if(player != null && player != this.player && DisguiseManager.instance.isDisguised(player) && DisguiseManager.instance.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
						byte yaw = fieldYawTeleport.getByte(packet);
						if(yaw < 0) {
							yaw += 128;
						} else {
							yaw -= 128;
						}
						fieldYawTeleport.set(packet, yaw);
					}
				}
				super.write(context, object, promise);
			} catch(Exception e) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Packet handling error!", e);
			}
		}
		
	}
	
}