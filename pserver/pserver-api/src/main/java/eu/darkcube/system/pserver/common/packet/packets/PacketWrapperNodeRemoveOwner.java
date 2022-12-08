/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import java.util.UUID;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeRemoveOwner extends Packet {
	
	private UniqueId id;
	private UUID owner;

	public PacketWrapperNodeRemoveOwner(UniqueId id, UUID owner) {
		this.id = id;
		this.owner = owner;
	}

	public UniqueId getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}
}
