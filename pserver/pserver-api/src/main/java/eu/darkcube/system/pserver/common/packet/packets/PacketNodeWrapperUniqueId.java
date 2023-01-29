/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperUniqueId extends Packet {

	private UniqueId id;
	
	public PacketNodeWrapperUniqueId(UniqueId id) {
		this.id = id;
	}
	
	public UniqueId getUniqueId() {
		return id;
	}
}
