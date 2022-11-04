package eu.darkcube.system.pserver.node.packethandler;

import java.util.Optional;

import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperBoolean;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerNotFound;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStop;

public class HandlerStop implements PacketHandler<PacketWrapperNodeStop> {

	@Override
	public Packet handle(PacketWrapperNodeStop packet) {
		Optional<? extends PServer> op = PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			boolean suc = op.get().stop();
			return new PacketNodeWrapperBoolean(suc);
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}