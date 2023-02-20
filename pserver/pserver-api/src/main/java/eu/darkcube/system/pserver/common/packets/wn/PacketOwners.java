/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

import java.util.Collection;
import java.util.UUID;

public class PacketOwners extends Packet {
	private final UniqueId id;

	public PacketOwners(UniqueId id) {
		this.id = id;
	}

	public UniqueId id() {
		return id;
	}

	public static class Response extends Packet {
		private final Collection<UUID> owners;

		public Response(Collection<UUID> owners) {
			this.owners = owners;
		}

		public Collection<UUID> owners() {
			return owners;
		}
	}
}
