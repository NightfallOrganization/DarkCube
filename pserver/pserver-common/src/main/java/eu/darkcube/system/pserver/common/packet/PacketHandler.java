package eu.darkcube.system.pserver.common.packet;

public interface PacketHandler<T extends Packet> {
	
	Packet handle(T packet);

}
