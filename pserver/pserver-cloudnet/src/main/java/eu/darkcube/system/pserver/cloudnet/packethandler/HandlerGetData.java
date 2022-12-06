package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperData;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetData;

public class HandlerGetData implements PacketHandler<PacketWrapperNodeGetData> {

	@Override
	public Packet handle(PacketWrapperNodeGetData packet) {
		return new PacketNodeWrapperData(
				PServerProvider.getInstance().getPServerData(packet.getPServerId()));
	}

}
