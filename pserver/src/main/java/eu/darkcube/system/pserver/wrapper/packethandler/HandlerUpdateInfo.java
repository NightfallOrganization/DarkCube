package eu.darkcube.system.pserver.wrapper.packethandler;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;
import eu.darkcube.system.pserver.wrapper.WrapperPServerProvider;

public class HandlerUpdateInfo implements PacketHandler<PacketNodeWrapperUpdateInfo> {
	@Override
	public Packet handle(PacketNodeWrapperUpdateInfo packet) {
		WrapperPServerProvider.getInstance().update(packet.getInfo());
		return null;
	}
}
