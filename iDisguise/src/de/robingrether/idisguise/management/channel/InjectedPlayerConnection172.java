package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.VersionHelper;
import net.minecraft.server.v1_7_R2.MinecraftServer;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R2.PlayerConnection;

public class InjectedPlayerConnection172 extends PlayerConnection implements InjectedPlayerConnection {
	
	private final ChannelInjectorPC channelInjector;
	private final Player observer;
	private final PlayerConnection originalConnection;
	
	public InjectedPlayerConnection172(ChannelInjectorPC channelInjector, Player observer, Object originalConnection) throws Exception {
		super((MinecraftServer)MinecraftServer_getServer.invoke(null), ((PlayerConnection)originalConnection).networkManager, ((CraftPlayer)observer).getHandle());
		this.channelInjector = channelInjector;
		this.observer = observer;
		this.originalConnection = (PlayerConnection)originalConnection;
	}
	
	public PlayerConnection getOriginalConnection() {
		return originalConnection;
	}
	
	public void a(PacketPlayInUseEntity packet) {
		try {
			packet = (PacketPlayInUseEntity)PacketHandler.getInstance().handlePacketPlayInUseEntity(observer, packet);
			if(packet != null) {
				super.a(packet);
			}
		} catch(Exception e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.SEVERE, "Cannot handle packet in: " + packet.getClass().getSimpleName() + " from " + observer.getName());
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