package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperString;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewName;

public class HandlerNewName implements PacketHandler<PacketWrapperNodeNewName> {

	@Override
	public Packet handle(PacketWrapperNodeNewName packet) {
		return new PacketNodeWrapperString(PServerProvider.getInstance().newName());
	}
}
