package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperBoolean extends Packet {

	private boolean bool;
	
	public PacketNodeWrapperBoolean(boolean bool) {
		this.bool = bool;
	}
	
	public boolean is() {
		return bool;
	}
}
