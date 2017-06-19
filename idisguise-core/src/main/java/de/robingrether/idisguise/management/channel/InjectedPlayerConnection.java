package de.robingrether.idisguise.management.channel;

public interface InjectedPlayerConnection {
	
	public void resetToDefaultConnection() throws Exception;
	
	public void sendPacket(Object packet);
	
	public void sendPacketDirectly(Object packet);
	
}