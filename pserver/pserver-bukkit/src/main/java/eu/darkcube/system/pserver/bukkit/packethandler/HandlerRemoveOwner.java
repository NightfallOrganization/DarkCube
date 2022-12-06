package eu.darkcube.system.pserver.bukkit.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemoveOwner;

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
