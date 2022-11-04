package eu.darkcube.system.pserver.node.packethandler;

import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetOwners;
import eu.darkcube.system.pserver.node.database.DatabaseProvider;
import eu.darkcube.system.pserver.node.database.PServerDatabase;

public class HandlerGetOwners implements PacketHandler<PacketWrapperNodeGetOwners> {

	@Override
	public Packet handle(PacketWrapperNodeGetOwners packet) {
		return new PacketNodeWrapperOwners(
				DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(packet.getPServer()));
	}
}
