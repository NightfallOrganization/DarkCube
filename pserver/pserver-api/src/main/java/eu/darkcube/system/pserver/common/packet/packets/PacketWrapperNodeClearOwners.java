package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeClearOwners extends Packet {

	private UniqueId id;

	public PacketWrapperNodeClearOwners(UniqueId id) {
		this.id = id;
	}
	
	public UniqueId getId() {
		return id;
	}
}
