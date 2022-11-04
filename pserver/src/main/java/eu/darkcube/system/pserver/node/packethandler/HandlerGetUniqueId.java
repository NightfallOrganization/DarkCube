package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetUniqueId;
import eu.darkcube.system.pserver.node.NodePServer;
import eu.darkcube.system.pserver.node.NodePServerProvider;

public class HandlerGetUniqueId implements PacketHandler<PacketWrapperNodeGetUniqueId> {

	@Override
	public Packet handle(PacketWrapperNodeGetUniqueId packet) {
		for (NodePServer ps : NodePServerProvider.getInstance().getPServers()) {
			if (ps.getServiceId() != null) {
				if (ps.getServiceId().getUniqueId().equals(packet.getServiceId().getUniqueId())) {
					return new PacketNodeWrapperUniqueId(ps.getId());
				}
			}
		}
		return null;
	}
}
