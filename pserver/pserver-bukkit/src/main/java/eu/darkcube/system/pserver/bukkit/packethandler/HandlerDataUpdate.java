package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperDataUpdate;

public class HandlerDataUpdate implements PacketHandler<PacketNodeWrapperDataUpdate> {

	@Override
	public Packet handle(PacketNodeWrapperDataUpdate packet) {
		WrapperPServerProvider.getInstance().getPServerOptional(packet.getPServerId())
				.ifPresent(ps -> {
					ps.update(ps.getSerializable(), ps.getData());
				});
		return null;
	}

}
