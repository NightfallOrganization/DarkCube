package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeDelete;

public class HandlerDelete implements PacketHandler<PacketWrapperNodeDelete> {

	@Override
	public Packet handle(PacketWrapperNodeDelete packet) {
		PServerProvider.getInstance().delete(packet.getId());
		return new PacketNodeWrapperActionConfirm();
	}
}
