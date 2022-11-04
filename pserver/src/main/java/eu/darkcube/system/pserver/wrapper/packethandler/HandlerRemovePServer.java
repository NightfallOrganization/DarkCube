package eu.darkcube.system.pserver.wrapper.packethandler;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemovePServer;
import eu.darkcube.system.pserver.wrapper.WrapperPServerProvider;

public class HandlerRemovePServer implements PacketHandler<PacketNodeWrapperRemovePServer> {

	@Override
	public Packet handle(PacketNodeWrapperRemovePServer packet) {
		WrapperPServerProvider.getInstance().remove(packet.getId());
		return null;
	}
}
