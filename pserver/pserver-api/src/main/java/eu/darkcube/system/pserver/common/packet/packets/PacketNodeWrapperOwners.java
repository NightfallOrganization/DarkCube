/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperOwners extends Packet {

	private Collection<UUID> uuids;

	public PacketNodeWrapperOwners(Collection<UUID> uuids) {
		this.uuids = uuids;
	}

	public Collection<UUID> getUuids() {
		return uuids;
	}

}
