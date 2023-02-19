/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketWrapperNodeStop extends Packet {

	public boolean success;
	private UniqueId id;

	public PacketWrapperNodeStop(UniqueId id) {
		this.id = id;
	}

	public UniqueId getId() {
		return id;
	}
}
