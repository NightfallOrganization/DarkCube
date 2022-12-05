package eu.darkcube.system.packetapi;

public interface PacketHandler<T extends Packet> {
	
	Packet handle(T packet);

}
