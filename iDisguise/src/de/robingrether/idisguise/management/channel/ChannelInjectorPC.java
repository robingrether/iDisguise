package de.robingrether.idisguise.management.channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.ChannelInjector;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.Sounds;
import de.robingrether.idisguise.management.VersionHelper;

import static de.robingrether.idisguise.management.Reflection.*;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ChannelInjectorPC extends ChannelInjector {
	
	private final Map<Player, InjectedPlayerConnection> playerConnectionMap = new ConcurrentHashMap<Player, InjectedPlayerConnection>();
	
	private Constructor<?> playerConnectionConstructor;
	
	public ChannelInjectorPC() {
		try {
			playerConnectionConstructor = Class.forName("de.robingrether.idisguise.management.channel.InjectedPlayerConnection" + VersionHelper.getVersionCode().replaceAll("[^0-9]*", "")).getConstructor(getClass(), Player.class, Object.class);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot find the required player connection constructor.", e);
			}
		}
	}
	
	public synchronized void inject(Player player) {
		try {
			InjectedPlayerConnection playerConnection = (InjectedPlayerConnection)playerConnectionConstructor.newInstance(this, player, EntityPlayer_playerConnection.get(CraftPlayer_getHandle.invoke(player)));
			EntityPlayer_playerConnection.set(CraftPlayer_getHandle.invoke(player), playerConnection);
			playerConnectionMap.put(player, playerConnection);
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot inject the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public synchronized void remove(Player player) {
		try {
			InjectedPlayerConnection playerConnection = playerConnectionMap.remove(player);
			if(playerConnection != null) {
				EntityPlayer_playerConnection.set(CraftPlayer_getHandle.invoke(player), playerConnection.getOriginalConnection());
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot remove the given player connection: " + player.getName(), e);
			}
		}
	}
	
	public Object[] handlePacketOut(final Player observer, final Object packet) {
		try {
			if(PacketPlayOutNamedEntitySpawn.isInstance(packet)) {
				return PacketHandler.getInstance().handlePacketPlayOutNamedEntitySpawn(observer, packet);
			} else if(PacketPlayOutPlayerInfo.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutPlayerInfo(observer, packet)};
			} else if(PacketPlayOutBed.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutBed(observer, packet)};
			} else if(PacketPlayOutAnimation.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutAnimation(observer, packet)};
			} else if(PacketPlayOutEntityMetadata.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutEntityMetadata(observer, packet)};
			} else if(PacketPlayOutEntityLook.isInstance(packet) || PacketPlayOutRelEntityMoveLook.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutEntity(observer, packet)};
			} else if(PacketPlayOutEntityTeleport.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutEntityTeleport(observer, packet)};
			} else if(PacketPlayOutUpdateAttributes.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutUpdateAttributes(observer, packet)};
			} else if(PacketPlayOutCollect.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutCollect(observer, packet)};
			} else if(Sounds.isEnabled() && PacketPlayOutNamedSoundEffect.isInstance(packet)) {
				return new Object[] {PacketHandler.getInstance().handlePacketPlayOutNamedSoundEffect(observer, packet)};
			}
			return new Object[] {packet};
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				Bukkit.getPluginManager().getPlugin("iDisguise").getLogger().log(Level.SEVERE, "Cannot handle packet out: " + packet.getClass().getSimpleName() + " to " + observer.getName());
			}
		}
		return new Object[0];
	}
	
}