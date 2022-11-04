package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperString extends Packet {

	private String string;

	public PacketNodeWrapperString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

}
