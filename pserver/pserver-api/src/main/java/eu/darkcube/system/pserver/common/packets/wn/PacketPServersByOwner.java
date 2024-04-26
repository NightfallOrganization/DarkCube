/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PacketPServersByOwner extends Packet {
	private final UUID owner;

	public PacketPServersByOwner(UUID owner) {
		this.owner = owner;
	}

	public UUID owner() {
		return owner;
	}

	public static class Response extends Packet {
		private final Collection<UniqueId> ids;

		public Response(Collection<UniqueId> ids) {
			this.ids = new HashSet<>(ids);
		}

		public Collection<UniqueId> ids() {
			return ids;
		}
	}
}
