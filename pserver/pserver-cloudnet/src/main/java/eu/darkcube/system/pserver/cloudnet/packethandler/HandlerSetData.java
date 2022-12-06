package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetData;

public class HandlerSetData implements PacketHandler<PacketWrapperNodeSetData> {

	@Override
	public Packet handle(PacketWrapperNodeSetData packet) {
		NodePServerProvider.getInstance().setPServerData(packet.getPServerId(), packet.getData());
		return null;
	}

}
