package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeGetData extends Packet {

	private UniqueId pserverId;

	public PacketWrapperNodeGetData(UniqueId pserverId) {
		this.pserverId = pserverId;
	}

	public UniqueId getPServerId() {
		return this.pserverId;
	}

}
