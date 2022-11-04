package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodePrivate;

public class HandlerPrivate implements PacketHandler<PacketWrapperNodePrivate> {

	@Override
	public Packet handle(PacketWrapperNodePrivate packet) {
		PServerProvider.getInstance().getPServerOptional(packet.getId()).ifPresent(ps -> {
			ps.setPrivate(packet.isPrivateServer());
		});
		return new PacketNodeWrapperActionConfirm();
	}
}
