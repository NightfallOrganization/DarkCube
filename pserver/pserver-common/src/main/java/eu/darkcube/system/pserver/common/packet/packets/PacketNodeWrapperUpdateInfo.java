package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperUpdateInfo extends Packet {

	private PServerSerializable info;

	public PacketNodeWrapperUpdateInfo(PServerSerializable info) {
		this.info = info;
	}

	public PServerSerializable getInfo() {
		return info;
	}
}
