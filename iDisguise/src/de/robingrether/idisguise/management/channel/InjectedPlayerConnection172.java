package de.robingrether.idisguise.management.channel;

import static de.robingrether.idisguise.management.Reflection.*;

import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.robingrether.idisguise.management.PacketHandler;
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
		}
	}
	
	public void sendPacket(Packet packet) {
		Object[] packets = channelInjector.handlePacketOut(observer, packet);
		for(Object p : packets) {
			super.sendPacket((Packet)p);
		}
	}
	
}