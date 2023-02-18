/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.util.data;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataSet;

class HandlerSet implements PacketHandler<PacketWrapperNodeDataSet> {
	@Override
	public Packet handle(PacketWrapperNodeDataSet packet) {
		SynchronizedPersistentDataStorages.storage(packet.key()).load0(packet.data());
		return null;
	}
}
