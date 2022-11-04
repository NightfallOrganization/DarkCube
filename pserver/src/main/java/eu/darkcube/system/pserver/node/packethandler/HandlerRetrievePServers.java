package eu.darkcube.system.pserver.node.packethandler;

import java.util.stream.Collectors;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRetrievePServers;
import eu.darkcube.system.pserver.node.NodePServerProvider;

public class HandlerRetrievePServers implements PacketHandler<PacketWrapperNodeRetrievePServers> {

	@Override
	public Packet handle(PacketWrapperNodeRetrievePServers packet) {
		return new PacketNodeWrapperPServers(NodePServerProvider.getInstance().getPServers().stream().map(s -> s.getSerializable())
				.collect(Collectors.toSet()));
	}
}
