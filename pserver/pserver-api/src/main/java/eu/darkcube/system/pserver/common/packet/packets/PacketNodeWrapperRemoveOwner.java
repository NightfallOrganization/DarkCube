/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import java.util.UUID;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperRemoveOwner extends Packet {

	private UniqueId uniqueId;
	private UUID owner;

	public PacketNodeWrapperRemoveOwner(UniqueId uniqueId, UUID owner) {
		this.uniqueId = uniqueId;
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

	public UniqueId getUniqueId() {
		return uniqueId;
	}
}
