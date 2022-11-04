package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeStart extends Packet {

	private UniqueId id;
	public boolean success;

	public PacketWrapperNodeStart(UniqueId id) {
		this.id = id;
	}

	public UniqueId getId() {
		return id;
	}
}
