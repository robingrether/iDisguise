package de.robingrether.idisguise.management.channel;

import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.PacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

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