package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperUniqueId extends Packet {

	private UniqueId id;
	
	public PacketNodeWrapperUniqueId(UniqueId id) {
		this.id = id;
	}
	
	public UniqueId getUniqueId() {
		return id;
	}
}
