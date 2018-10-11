package de.robingrether.idisguise.management.channel;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import de.robingrether.idisguise.iDisguise;
import de.robingrether.idisguise.management.PacketHandler;
import de.robingrether.idisguise.management.VersionHelper;
import de.robingrether.idisguise.management.channel.ChannelInjector.IChannelInjector;

class ProtocolLibChannelInjector implements IChannelInjector {
	
	ProtocolLibChannelInjector() {}
	
	public void init() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(iDisguise.getInstance(), ListenerPriority.NORMAL,
				Arrays.asList(PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.BED,
						PacketType.Play.Server.ANIMATION, PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY, PacketType.Play.Server.ENTITY_LOOK, PacketType.Play.Server.REL_ENTITY_MOVE,
						PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.UPDATE_ATTRIBUTES, PacketType.Play.Server.COLLECT,
						PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.SCOREBOARD_TEAM, PacketType.Play.Server.SCOREBOARD_SCORE, PacketType.Play.Server.ENTITY_DESTROY,
						PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Client.USE_ENTITY),
				ListenerOptions.ASYNC) {
			
			public void onPacketSending(PacketEvent event) {
				if(event.isCancelled()) return;
				
				Object[] alteredMsg = PacketHandler.handlePacket(event.getPlayer(), event.getPacket().getHandle());
				if(alteredMsg.length == 1) {
					if(alteredMsg[0] != null) {
						PacketContainer alteredPacket = PacketContainer.fromPacket(alteredMsg[0]);
						if(event.getPacketType().equals(alteredPacket.getType())) {
							event.setPacket(alteredPacket);
						} else {
							try {
								ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), alteredPacket, false);
							} catch(InvocationTargetException e) {
								if(VersionHelper.debug()) {
									iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packet: " + alteredMsg[0].getClass().getSimpleName() + " to " + event.getPlayer().getName(), e);
								}
							}
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				} else {
					try {
						for(Object obj : alteredMsg) {
							ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), PacketContainer.fromPacket(obj), false);
						}
					} catch(InvocationTargetException e) {
						if(VersionHelper.debug()) {
							iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packets to " + event.getPlayer().getName(), e);
						}
					}
					event.setCancelled(true);
				}
			}
			
			public void onPacketReceiving(PacketEvent event) {
				if(event.isCancelled()) return;
				
				Object[] alteredMsg = PacketHandler.handlePacket(event.getPlayer(), event.getPacket().getHandle());
				if(alteredMsg.length == 1) {
					if(alteredMsg[0] != null) {
						event.setPacket(PacketContainer.fromPacket(alteredMsg[0]));
					} else {
						event.setCancelled(true);
					}
				}/* else {
					// my in-bound handlers always return one packet (or null)
				}*/
			}
			
		});
	}
	
	public void terminate() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(iDisguise.getInstance());
	}
	
	public void sendPacket(Player observer, Object packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(observer, PacketContainer.fromPacket(packet));
		} catch(InvocationTargetException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packet: " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
	public void sendPacketUnaltered(Player observer, Object packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(observer, PacketContainer.fromPacket(packet), false);
		} catch(InvocationTargetException e) {
			if(VersionHelper.debug()) {
				iDisguise.getInstance().getLogger().log(Level.INFO, "Cannot send packet (unaltered): " + packet.getClass().getSimpleName() + " to " + observer.getName(), e);
			}
		}
	}
	
}