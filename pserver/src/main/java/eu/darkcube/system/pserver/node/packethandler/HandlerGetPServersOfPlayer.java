package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerIDs;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServersOfPlayer;

public class HandlerGetPServersOfPlayer implements PacketHandler<PacketWrapperNodeGetPServersOfPlayer> {

	@Override
	public Packet handle(PacketWrapperNodeGetPServersOfPlayer packet) {
		return new PacketNodeWrapperPServerIDs(PServerProvider.getInstance().getPServerIDs(packet.getPlayer()));
	}
}
