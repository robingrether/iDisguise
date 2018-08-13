package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;
import io.netty.channel.Channel;

public final class ChannelInjector {
	
	private ChannelInjector() {}
	
	private static final Map<Player, InjectedChannelHandler> channelHandlerMap = new ConcurrentHashMap<Player, InjectedChannelHandler>();
	
	public static void init() {}
	
	public static synchronized void inject(Player player) {
		if(channelHandlerMap.containsKey(player)) return;
		try {
			Channel channel = (Channel)NetworkManager_channel.get(PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(player))));
			InjectedChannelHandler channelHandler = new InjectedChannelHandler(player, channel);
			channel.pipeline().addBefore("packet_handler", "iDisguise", channelHandler);
			channelHandlerMap.put(player, channelHandler);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot inject the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public static synchronized void remove(Player player) {
		if(!channelHandlerMap.containsKey(player)) return;
		try {
			InjectedChannelHandler channelHandler = channelHandlerMap.remove(player);
			Channel channel = channelHandler.getChannel();
			channel.pipeline().remove(channelHandler);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot remove the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public static synchronized void injectOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			inject(player);
		}
	}
	
	public static synchronized void removeOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			remove(player);
		}
	}
	
}