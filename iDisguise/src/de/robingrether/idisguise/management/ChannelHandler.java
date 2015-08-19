package de.robingrether.idisguise.management;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.PlayerDisguise;
import de.robingrether.util.ObjectUtil;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ChannelHandler extends ChannelDuplexHandler {
	
	private static final ConcurrentHashMap<Player, ChannelHandler> handlerMap = new ConcurrentHashMap<Player, ChannelHandler>();
	private Player player;
	
	private ChannelHandler(Player player) {
		this.player = player;
	}
	
	public void channelRead(ChannelHandlerContext context, Object object) throws Exception {
		super.channelRead(context, object);
	}
	
	public void write(ChannelHandlerContext context, Object object, ChannelPromise promise) {
		try {
			if(object instanceof PacketPlayOutNamedEntitySpawn) {
				PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn)object;
				Player player = Bukkit.getPlayer((UUID)fieldUUID.get(packet));
				if(player != this.player && DisguiseManager.isDisguised(player)) {
					Packet<?> packetSpawn = DisguiseManager.getSpawnPacket(player);
					if(packetSpawn instanceof PacketPlayOutNamedEntitySpawn) {
						// if(isGhost)
						super.write(context, packetSpawn, promise);
					} else {
						if(DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
							byte yaw = fieldYawSpawnEntityLiving.getByte(packetSpawn);
							if(yaw < 0) {
								yaw += 128;
							} else {
								yaw -= 128;
							}
							fieldYawSpawnEntityLiving.set(packetSpawn, yaw);
						}
						DisguiseManager.sendPacketLater(this.player, packetSpawn, 1L);
					}
					DisguiseManager.updateAttributes(player, this.player);
					return;
				}
			} else if(object instanceof PacketPlayOutPlayerInfo) {
				PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo)object;
				if(((EnumPlayerInfoAction)fieldAction.get(packet)) == EnumPlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> list = (List<PlayerInfoData>)fieldList.get(packet);
					List<PlayerInfoData> add = new ArrayList<PlayerInfoData>();
					List<PlayerInfoData> remove = new ArrayList<PlayerInfoData>();
					for(PlayerInfoData playerInfo : list) {
						Player player = Bukkit.getPlayer(playerInfo.a().getId());
						if(player != null && player != this.player && DisguiseManager.isDisguised(player)) {
							if(DisguiseManager.getDisguise(player) instanceof PlayerDisguise) {
								PlayerInfoData newPlayerInfo = packet.new PlayerInfoData(ProfileUtil.getGameProfile(((PlayerDisguise)DisguiseManager.getDisguise(player)).getName()), playerInfo.b(), playerInfo.c(), playerInfo.d());
								remove.add(playerInfo);
								add.add(newPlayerInfo);
							} else {
								remove.add(playerInfo);
								DisguiseManager.sendPacketLater(this.player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()), 1L);
							}
						}
					}
					list.removeAll(remove);
					list.addAll(add);
				}
			} else if(object instanceof PacketPlayOutBed) {
				PacketPlayOutBed packet = (PacketPlayOutBed)object;
				Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdBed.getInt(packet));
				if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
					return;
				}
			} else if(object instanceof PacketPlayOutAnimation) {
				PacketPlayOutAnimation packet = (PacketPlayOutAnimation)object;
				int animation = fieldAnimation.getInt(packet);
				if(animation == 2) {
					Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdAnimation.getInt(packet));
					if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && !(DisguiseManager.getDisguise(player) instanceof PlayerDisguise)) {
						return;
					}
				}
			} else if(object instanceof PacketPlayOutEntityMetadata) {
				PacketPlayOutEntityMetadata packet = (PacketPlayOutEntityMetadata)object;
				Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdMetadata.getInt(packet));
				if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && ObjectUtil.equals(DisguiseManager.getDisguise(player).getType(), DisguiseType.CREEPER, DisguiseType.ENDERMAN)) {
					return;
				}
			} else if(object instanceof PacketPlayOutEntityLook) {
				PacketPlayOutEntityLook packet = (PacketPlayOutEntityLook)object;
				Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
				if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
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
				Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdEntity.getInt(packet));
				if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
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
				Player player = PlayerUtil.getPlayerByEntityId(fieldEntityIdTeleport.getInt(packet));
				if(player != null && !(player == this.player) && DisguiseManager.isDisguised(player) && DisguiseManager.getDisguise(player).getType().equals(DisguiseType.ENDER_DRAGON)) {
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
			e.printStackTrace();
		}
	}
	
	public static synchronized void addHandler(Player player) {
		try {
			ChannelHandler handler = new ChannelHandler(player);
			handlerMap.put(player, handler);
			((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", "iDisguise", handler);
		} catch(Exception e) {
		}
	}
	
	public static synchronized void removeHandler(Player player) {
		try {
			ChannelHandler handler = handlerMap.remove(player);
			((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline().remove(handler);
		} catch(Exception e) {
		}
	}
	
	private static Field fieldUUID, fieldAction, fieldList, fieldEntityIdBed, fieldAnimation, fieldEntityIdAnimation, fieldEntityIdMetadata, fieldEntityIdEntity, fieldYawEntity, fieldEntityIdTeleport, fieldYawTeleport, fieldYawSpawnEntityLiving;
	
	static {
		try {
			fieldUUID = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
			fieldUUID.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldAction = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
			fieldAction.setAccessible(true);
		} catch(Exception e) {
		}
		try {
			fieldList = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
			fieldList.setAccessible(true);
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
	}
	
	/*private static void logPacket(Packet<?> packet) {
		try {
			Field[] fields = packet.getClass().getDeclaredFields();
			StringBuilder builder = new StringBuilder("[iDisguise] Packet sent: ");
			for(Field field : fields) {
				field.setAccessible(true);
				builder.append(field.getName() + "=" + (field.get(packet) != null ? field.get(packet).toString() : "null") + "; ");
			}
			System.out.println(builder.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
}