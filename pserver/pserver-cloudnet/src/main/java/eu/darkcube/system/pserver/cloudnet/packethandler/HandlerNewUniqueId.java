package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewUniqueId;

public class HandlerNewUniqueId implements PacketHandler<PacketWrapperNodeNewUniqueId> {

	@Override
	public Packet handle(PacketWrapperNodeNewUniqueId packet) {
		UniqueId id = UniqueIdProvider.getInstance().newUniqueId();
		return new PacketNodeWrapperUniqueId(id);
	}
}
