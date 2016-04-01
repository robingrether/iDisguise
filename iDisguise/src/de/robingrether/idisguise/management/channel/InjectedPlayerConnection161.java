package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import org.bukkit.craftbukkit.v1_6_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.PacketHandler;
import net.minecraft.server.v1_6_R1.MinecraftServer;
import net.minecraft.server.v1_6_R1.Packet;
import net.minecraft.server.v1_6_R1.Packet7UseEntity;
import net.minecraft.server.v1_6_R1.PlayerConnection;

public class InjectedPlayerConnection161 extends PlayerConnection implements InjectedPlayerConnection {
	
	private final ChannelInjectorPC channelInjector;
	private final Player observer;
	private final PlayerConnection originalConnection;
	
	public InjectedPlayerConnection161(ChannelInjectorPC channelInjector, Player observer, Object originalConnection) throws Exception {
		super((MinecraftServer)MinecraftServer_getServer.invoke(null), ((PlayerConnection)originalConnection).networkManager, ((CraftPlayer)observer).getHandle());
		this.channelInjector = channelInjector;
		this.observer = observer;
		this.originalConnection = (PlayerConnection)originalConnection;
	}
	
	public PlayerConnection getOriginalConnection() {
		return originalConnection;
	}
	
	public void a(Packet7UseEntity packet) {
		try {
			packet = (Packet7UseEntity)PacketHandler.getInstance().handlePacketPlayInUseEntity(observer, packet);
			if(packet != null) {
				super.a(packet);
			}
		} catch(Exception e) {
		}
	}
	
	public void sendPacket(Packet packet) {
		Object[] packets = channelInjector.handlePacketOut(observer, packet);
		for(Object p : packets) {
			super.sendPacket((Packet)p);
		}
	}
	
}