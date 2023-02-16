/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperRemoveOwner;

public class HandlerRemoveOwner implements PacketHandler<PacketNodeWrapperRemoveOwner> {

	@Override
	public Packet handle(PacketNodeWrapperRemoveOwner packet) {
		PServerProvider.getInstance().getPServerOptional(packet.getUniqueId()).ifPresent(ps -> {
			ps.getOwners().remove(packet.getOwner());
		});
		CloudNetDriver.getInstance().getEventManager().callEvent(new PServerRemoveOwnerEvent(
				PServerProvider.getInstance().getPServer(packet.getUniqueId()), packet.getOwner()));
		return null;
	}

}
