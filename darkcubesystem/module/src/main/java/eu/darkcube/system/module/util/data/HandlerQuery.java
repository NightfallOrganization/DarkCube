/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.util.data;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeQuery;

class HandlerQuery implements PacketHandler<PacketWrapperNodeQuery> {
	@Override
	public Packet handle(PacketWrapperNodeQuery packet) {
		return null;
	}
}
