package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperAddPServer extends Packet {
	
	private PServerSerializable pserver;

	public PacketNodeWrapperAddPServer(PServerSerializable pserver) {
		this.pserver = pserver;
	}
	
	public PServerSerializable getPServer() {
		return pserver;
	}
}
