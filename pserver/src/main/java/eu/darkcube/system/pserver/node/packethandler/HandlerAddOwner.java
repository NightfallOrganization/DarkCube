package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeAddOwner;
import eu.darkcube.system.pserver.node.NodePServerProvider;

public class HandlerAddOwner implements PacketHandler<PacketWrapperNodeAddOwner> {

	@Override
	public Packet handle(PacketWrapperNodeAddOwner packet) {
//		if (NodePServerProvider.getInstance().isPServer(packet.getId())) {
//			NodePServer ps = NodePServerProvider.getInstance().getPServer(packet.getId());
//			ps.addOwner(packet.getOwner());
//			return new PacketNodeWrapperActionConfirm();
//		}
//		return new PacketNodeWrapperPServerNotFound(packet.getId());
		NodePServerProvider.getInstance().addOwner(packet.getId(), packet.getOwner());
		return new PacketNodeWrapperActionConfirm();
	}
}