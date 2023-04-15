/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.nw.PacketUpdate;

public class HandlerUpdate implements PacketHandler<PacketUpdate> {
	@Override
	public Packet handle(PacketUpdate packet) throws Throwable {
		CloudNetDriver.getInstance().getEventManager().callEvent(new PServerUpdateEvent(
				PServerProvider.instance().pserver(packet.snapshot().uniqueId()).get()));
		return null;
	}
}
