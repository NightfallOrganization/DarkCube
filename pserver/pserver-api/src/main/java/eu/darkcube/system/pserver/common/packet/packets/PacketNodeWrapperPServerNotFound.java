/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperPServerNotFound extends Packet {

	private UniqueId id;

	public PacketNodeWrapperPServerNotFound(UniqueId id) {
		this.id = id;
	}
	
	public UniqueId getId() {
		return id;
	}
}
