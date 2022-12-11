/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.bukkit.event.PServerAddOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddOwner;

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
