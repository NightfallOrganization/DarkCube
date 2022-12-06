package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemoveOwner;

public class HandlerRemoveOwner implements PacketHandler<PacketWrapperNodeRemoveOwner> {

	@Override
	public Packet handle(PacketWrapperNodeRemoveOwner packet) {
//		if (NodePServerProvider.getInstance().isPServer(packet.getId())) {
//			NodePServer ps = NodePServerProvider.getInstance().getPServer(packet.getId());
//			ps.removeOwner(packet.getOwner());
//			return new PacketNodeWrapperActionConfirm();
//		}
//		return new PacketNodeWrapperPServerNotFound(packet.getId());
		PServerProvider.getInstance().removeOwner(packet.getId(), packet.getOwner());
		return new PacketNodeWrapperActionConfirm();
	}
}
