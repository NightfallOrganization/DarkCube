package eu.darkcube.system.pserver.common.packet.packets;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperOwners extends Packet {

	private Collection<UUID> uuids;

	public PacketNodeWrapperOwners(Collection<UUID> uuids) {
		this.uuids = uuids;
	}

	public Collection<UUID> getUuids() {
		return uuids;
	}

}
