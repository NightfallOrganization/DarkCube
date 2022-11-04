package eu.darkcube.system.pserver.wrapper.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemoveOwner;
import eu.darkcube.system.pserver.wrapper.event.PServerRemoveOwnerEvent;

public class HandlerRemoveOwner implements PacketHandler<PacketNodeWrapperRemoveOwner> {

	@Override
	public Packet handle(PacketNodeWrapperRemoveOwner packet) {
		PServerProvider.getInstance().getPServerOptional(packet.getUniqueId()).ifPresent(ps -> {
			ps.getOwners().remove(packet.getOwner());
		});
		CloudNetDriver.getInstance()
				.getEventManager()
				.callEvent(new PServerRemoveOwnerEvent(PServerProvider.getInstance().getPServer(packet.getUniqueId()),
						packet.getOwner()));
		return null;
	}

}
