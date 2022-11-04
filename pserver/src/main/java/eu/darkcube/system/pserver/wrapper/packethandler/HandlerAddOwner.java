package eu.darkcube.system.pserver.wrapper.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddOwner;
import eu.darkcube.system.pserver.wrapper.event.PServerAddOwnerEvent;

public class HandlerAddOwner implements PacketHandler<PacketNodeWrapperAddOwner> {

	@Override
	public Packet handle(PacketNodeWrapperAddOwner packet) {
		PServerProvider.getInstance().getPServerOptional(packet.getUniqueId()).ifPresent(ps -> {
			ps.getOwners().add(packet.getOwner());
		});
		CloudNetDriver.getInstance()
				.getEventManager()
				.callEvent(new PServerAddOwnerEvent(PServerProvider.getInstance().getPServer(packet.getUniqueId()),
						packet.getOwner()));
		return null;
	}
}
