package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperPServer extends Packet {
	
	private PServerSerializable pserver;

	public PacketNodeWrapperPServer(PServerSerializable pserver) {
		this.pserver = pserver;
	}

	public PServerSerializable getInfo() {
		return pserver;
	}
}
