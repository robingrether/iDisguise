package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.idisguise.management.channel.ChannelInjector.IChannelInjector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

class NettyChannelInjector implements IChannelInjector {
	
	NettyChannelInjector() {}
	
	private static final Map<Player, InjectedChannelHandler> channelHandlerMap = new ConcurrentHashMap<Player, InjectedChannelHandler>();
	
	public synchronized void inject(Player player) {
		if(channelHandlerMap.containsKey(player)) return;
		try {
			Channel channel = (Channel)NetworkManager_channel.get(PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(player))));
			InjectedChannelHandler channelHandler = new InjectedChannelHandler(player, channel);
			channel.pipeline().addBefore("packet_handler", "iDisguise", channelHandler);
			channelHandlerMap.put(player, channelHandler);
		} catch(Exception e) {
			iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot inject the given player connection: " + player.getName(), e);
		}
	}
	
	public synchronized void remove(Player player) {
		if(!channelHandlerMap.containsKey(player)) return;
		try {
			InjectedChannelHandler channelHandler = channelHandlerMap.remove(player);
			Channel channel = channelHandler.getChannel();
			channel.pipeline().remove(channelHandler);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot remove the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public synchronized void sendPacket(final Player observer, final Object packet) {
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
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packet: " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
	public synchronized void sendPacketUnaltered(final Player observer, final Object packet) {
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
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packet (unaltered): " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
	public class InjectedChannelHandler extends ChannelDuplexHandler {
		
		private Player player;
		private Channel channel;
		
		public InjectedChannelHandler(Player player, Channel channel) {
			this.player = player;
			this.channel = channel;
		}
		
		Channel getChannel() {
			return channel;
		}
		
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			Object[] alteredMsg = PacketHandler.handlePacket(player, msg);
			if(alteredMsg.length == 1) {
				if(alteredMsg[0] != null) super.channelRead(ctx, alteredMsg[0]);
			}/* else {
				// my in-bound handlers always return one packet (or null)
			}*/
		}
		
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			if(msg instanceof Unaltered) {
				super.write(ctx, ((Unaltered)msg).getObject(), promise);
			} else {
				Object[] alteredMsg = PacketHandler.handlePacket(player, msg);
				if(alteredMsg.length == 1) {
					if(alteredMsg[0] != null) super.write(ctx, alteredMsg[0], promise);
				} else {
					for(Object obj : alteredMsg) {
						if(obj != null) channel.pipeline().writeAndFlush(new Unaltered(obj));
					}
				}
			}
		}
		
	}
	
	public class Unaltered {
		
		private final Object object;
		
		public Unaltered(Object object) {
			this.object = object;
		}
		
		public Object getObject() {
			return object;
		}
		
	}
	
}