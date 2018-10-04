package de.robingrether.idisguise.management.channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChannelInjector {
	
	private static IChannelInjector channelInjector;
	
	public static void init() {
		if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			channelInjector = new ProtocolLibChannelInjector();
		} else {
			channelInjector = new NettyChannelInjector();
		}
		channelInjector.init();
	}
	
	public static void terminate() {
		channelInjector.terminate();
	}
	
	public static void inject(Player player) {
		channelInjector.inject(player);
	}
	
	public static void remove(Player player) {
		channelInjector.remove(player);
	}
	
	public static void sendPacket(Player observer, Object packet) {
		channelInjector.sendPacket(observer, packet);
	}
	
	public static void sendPacketUnaltered(Player observer, Object packet) {
		channelInjector.sendPacketUnaltered(observer, packet);
	}
	
	public static void injectOnlinePlayers() {
		channelInjector.injectOnlinePlayers();
	}
	
	public static void removeOnlinePlayers() {
		channelInjector.removeOnlinePlayers();
	}
	
	static interface IChannelInjector {
		
		default void init() {}
		
		default void terminate() {}
		
		default void inject(Player player) {}
		
		default void remove(Player player) {}
		
		void sendPacket(Player observer, Object packet);
		
		void sendPacketUnaltered(Player observer, Object packet);
		
		default void injectOnlinePlayers() {
			for(Player player : Bukkit.getOnlinePlayers()) {
				inject(player);
			}
		}
		
		default void removeOnlinePlayers() {
			for(Player player : Bukkit.getOnlinePlayers()) {
				remove(player);
			}
		}
		
	}
	
}