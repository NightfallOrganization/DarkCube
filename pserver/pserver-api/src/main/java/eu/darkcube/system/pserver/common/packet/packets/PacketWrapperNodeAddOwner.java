package eu.darkcube.system.pserver.common.packet.packets;

import java.util.UUID;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeAddOwner extends Packet {

	private UniqueId id;
	private UUID owner;

	public PacketWrapperNodeAddOwner(UniqueId id, UUID owner) {
		this.id = id;
		this.owner = owner;
	}

	public UniqueId getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}
}
