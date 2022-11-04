package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeGetOwners extends Packet {

	private UniqueId pserver;

	public PacketWrapperNodeGetOwners(UniqueId pserver) {
		this.pserver = pserver;
	}

	public UniqueId getPServer() {
		return pserver;
	}

}
