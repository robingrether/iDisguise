package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.VersionHelper;

public final class ChannelInjector {
	
	private ChannelInjector() {}
	
	private static final Map<Player, InjectedPlayerConnection> playerConnectionMap = new ConcurrentHashMap<Player, InjectedPlayerConnection>();
	private static Constructor<?> playerConnectionConstructor;
	
	public static void init() {
		try {
			playerConnectionConstructor = Class.forName("de.robingrether.idisguise.management.channel.InjectedPlayerConnection" + VersionHelper.getVersionCode().replaceAll("[^0-9]*", "")).getConstructor(Player.class, Object.class);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot find the required player connection constructor.", e);
			}
		}
	}
	
	public static synchronized void inject(Player player) {
		try {
			InjectedPlayerConnection playerConnection = (InjectedPlayerConnection)playerConnectionConstructor.newInstance(player, EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(player)));
			playerConnectionMap.put(player, playerConnection);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot inject the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public static synchronized void remove(Player player) {
		try {
			InjectedPlayerConnection playerConnection = playerConnectionMap.remove(player);
			if(playerConnection != null) {
				playerConnection.resetToDefaultConnection();
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot remove the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public static void injectOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			inject(player);
		}
	}
	
	public static void removeOnlinePlayers() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			remove(player);
		}
	}
	
}