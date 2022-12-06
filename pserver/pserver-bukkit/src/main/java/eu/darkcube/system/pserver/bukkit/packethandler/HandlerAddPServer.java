package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddPServer;

public class HandlerAddPServer implements PacketHandler<PacketNodeWrapperAddPServer> {

	@Override
	public Packet handle(PacketNodeWrapperAddPServer packet) {
		WrapperPServerProvider.getInstance().updateAndInsertIfNecessary(packet.getPServer());
		return null;
	}
}
