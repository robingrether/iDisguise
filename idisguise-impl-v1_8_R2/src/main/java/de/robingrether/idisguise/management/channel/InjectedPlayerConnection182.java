package de.robingrether.idisguise.management.channel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.idisguise.management.channel.InjectedPlayerConnection;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R2.PlayerConnection;

public class InjectedPlayerConnection182 extends PlayerConnection implements InjectedPlayerConnection {
	
	private final Player observer;
	
	public InjectedPlayerConnection182(Player observer, Object originalConnection) throws Exception {
		super(MinecraftServer.getServer(), ((PlayerConnection)originalConnection).networkManager, ((CraftPlayer)observer).getHandle());
		this.observer = observer;
		for(Field field : PlayerConnection.class.getDeclaredFields()) {
			if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(this, field.get(originalConnection));
			}
		}
	}
	
	public void resetToDefaultConnection() throws Exception {
		PlayerConnection defaultConnection = new PlayerConnection(MinecraftServer.getServer(), networkManager, player);
		for(Field field : PlayerConnection.class.getDeclaredFields()) {
			if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(defaultConnection, field.get(this));
			}
		}
	}
	
	public void sendPacket(Object packet) {
		if(packet instanceof Packet) sendPacket((Packet)packet);
	}
	
	public void sendPacketDirectly(Object packet) {
		if(packet instanceof Packet) super.sendPacket((Packet)packet);
	}
	
	public void a(PacketPlayInUseEntity packet) {
		try {
			packet = (PacketPlayInUseEntity)PacketHandler.handlePacketPlayInUseEntity(observer, packet);
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
		Object[] packets = PacketHandler.handlePacketOut(observer, packet);
		for(Object p : packets) {
			super.sendPacket((Packet)p);
		}
	}
	
}