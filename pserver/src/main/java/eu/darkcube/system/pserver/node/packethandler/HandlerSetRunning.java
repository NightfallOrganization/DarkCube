package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetRunning;
import eu.darkcube.system.pserver.node.NodePServerProvider;

public class HandlerSetRunning implements PacketHandler<PacketWrapperNodeSetRunning> {

	@Override
	public Packet handle(PacketWrapperNodeSetRunning packet) {
		NodePServerProvider.getInstance().getPServerOptional(packet.getId()).ifPresent(ps -> {
			ps.setState(State.RUNNING);
		});
		return null;
	}
}
