package eu.darkcube.system.pserver.node.packethandler;

import java.util.Optional;

import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerNotFound;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemove;

public class HandlerRemove implements PacketHandler<PacketWrapperNodeRemove> {

	@Override
	public Packet handle(PacketWrapperNodeRemove packet) {
		Optional<? extends PServer> op = PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			op.get().remove();
			return new PacketNodeWrapperActionConfirm();
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}
