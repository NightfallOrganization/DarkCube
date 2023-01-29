/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import java.util.Collection;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperPServerIDs extends Packet {

	private Collection<UniqueId> ids;
	
	public PacketNodeWrapperPServerIDs(Collection<UniqueId> ids) {
		this.ids = ids;
	}
	
	public Collection<UniqueId> getIds() {
		return ids;
	}
}
