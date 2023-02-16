/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import java.util.Collection;
import java.util.UUID;

public class PacketNodeWrapperOwners extends Packet {

	private Collection<UUID> uuids;

	public PacketNodeWrapperOwners(Collection<UUID> uuids) {
		this.uuids = uuids;
	}

	public Collection<UUID> getUuids() {
		return uuids;
	}

}
