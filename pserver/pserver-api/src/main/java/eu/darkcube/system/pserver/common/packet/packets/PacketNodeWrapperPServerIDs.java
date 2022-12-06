package eu.darkcube.system.pserver.common.packet.packets;

import java.util.Collection;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperPServerIDs extends Packet {

	private Collection<UniqueId> ids;
	
	public PacketNodeWrapperPServerIDs(Collection<UniqueId> ids) {
		this.ids = ids;
	}
	
	public Collection<UniqueId> getIds() {
		return ids;
	}
}
