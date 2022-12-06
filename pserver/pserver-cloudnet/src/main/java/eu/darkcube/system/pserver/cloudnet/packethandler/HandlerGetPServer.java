package eu.darkcube.system.pserver.cloudnet.packethandler;

import java.util.Optional;

import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerNotFound;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServer;

public class HandlerGetPServer implements PacketHandler<PacketWrapperNodeGetPServer> {

	@Override
	public Packet handle(PacketWrapperNodeGetPServer packet) {
		Optional<? extends PServer> op = PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			return new PacketNodeWrapperPServer(op.get().getSerializable());
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}
