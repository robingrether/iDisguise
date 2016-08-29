package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.craftbukkit.v1_6_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.idisguise.management.channel.ChannelInjectorPC;
import de.robingrether.idisguise.management.channel.InjectedPlayerConnection;
import net.minecraft.server.v1_6_R1.MinecraftServer;
import net.minecraft.server.v1_6_R1.Packet;
import net.minecraft.server.v1_6_R1.Packet7UseEntity;
import net.minecraft.server.v1_6_R1.PlayerConnection;

public class InjectedPlayerConnection161 extends PlayerConnection implements InjectedPlayerConnection {
	
	private final ChannelInjectorPC channelInjector;
	private final Player observer;
	
	public InjectedPlayerConnection161(ChannelInjectorPC channelInjector, Player observer, Object originalConnection) throws Exception {
		super((MinecraftServer)MinecraftServer_getServer.invoke(null), ((PlayerConnection)originalConnection).networkManager, ((CraftPlayer)observer).getHandle());
		this.channelInjector = channelInjector;
		this.observer = observer;
		for(Field field : PlayerConnection.class.getDeclaredFields()) {
			if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(this, field.get(originalConnection));
			}
		}
	}
	
	public void resetToDefaultConnection() throws Exception {
		PlayerConnection defaultConnection = new PlayerConnection((MinecraftServer)MinecraftServer_getServer.invoke(null), networkManager, player);
		for(Field field : PlayerConnection.class.getDeclaredFields()) {
			if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(defaultConnection, field.get(this));
			}
		}
	}
	
	public void a(Packet7UseEntity packet) {
		try {
			packet = (Packet7UseEntity)PacketHandler.getInstance().handlePacketPlayInUseEntity(observer, packet);
			if(packet != null) {
				super.a(packet);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet in: " + packet.getClass().getSimpleName() + " from " + observer.getName(), e);
			}
		}
	}
	
	public void sendPacket(Packet packet) {
		Object[] packets = channelInjector.handlePacketOut(observer, packet);
		for(Object p : packets) {
			super.sendPacket((Packet)p);
		}
	}
	
}