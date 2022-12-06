package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;

public class HandlerUpdateInfo implements PacketHandler<PacketNodeWrapperUpdateInfo> {
	@Override
	public Packet handle(PacketNodeWrapperUpdateInfo packet) {
		WrapperPServerProvider.getInstance().update(packet.getInfo());
		return null;
	}
}
