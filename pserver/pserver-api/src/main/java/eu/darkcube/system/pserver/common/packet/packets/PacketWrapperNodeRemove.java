package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeRemove extends Packet {

	private UniqueId id;

	public PacketWrapperNodeRemove(UniqueId id) {
		this.id = id;
	}
	
	public UniqueId getId() {
		return id;
	}
	
}
