package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperPrivate extends Packet {
	private UniqueId id;
	private boolean privateServer;

	public PacketNodeWrapperPrivate(UniqueId id, boolean privateServer) {
		this.id = id;
		this.privateServer = privateServer;
	}

	public UniqueId getId() {
		return id;
	}

	public boolean isPrivateServer() {
		return privateServer;
	}
}
