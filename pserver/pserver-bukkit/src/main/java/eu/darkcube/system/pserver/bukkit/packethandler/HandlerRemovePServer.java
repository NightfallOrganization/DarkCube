package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemovePServer;

public class HandlerRemovePServer implements PacketHandler<PacketNodeWrapperRemovePServer> {

	@Override
	public Packet handle(PacketNodeWrapperRemovePServer packet) {
		WrapperPServerProvider.getInstance().remove(packet.getId());
		return null;
	}
}
