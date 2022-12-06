package eu.darkcube.system.pserver.cloudnet.packethandler;

import java.util.Optional;

import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperBoolean;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerNotFound;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStart;

public class HandlerStart implements PacketHandler<PacketWrapperNodeStart> {

	@Override
	public Packet handle(PacketWrapperNodeStart packet) {
		Optional<? extends PServer> op = PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			boolean suc = op.get().start();
			return new PacketNodeWrapperBoolean(suc);
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}
