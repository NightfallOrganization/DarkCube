package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeConnectPlayer;

public class HandlerConnectPlayer implements PacketHandler<PacketWrapperNodeConnectPlayer> {

	@Override
	public Packet handle(PacketWrapperNodeConnectPlayer packet) {
		PServerProvider.getInstance()
				.getPServerOptional(packet.getPServer())
				.ifPresent(ps -> ps.connectPlayer(packet.getPlayer()));
		return null;
	}
}
