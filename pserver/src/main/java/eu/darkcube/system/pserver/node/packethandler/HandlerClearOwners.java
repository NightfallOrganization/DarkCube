package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeClearOwners;

public class HandlerClearOwners implements PacketHandler<PacketWrapperNodeClearOwners> {

	@Override
	public Packet handle(PacketWrapperNodeClearOwners packet) {
		PServerProvider.getInstance().clearOwners(packet.getId());
		return null;
	}
}
