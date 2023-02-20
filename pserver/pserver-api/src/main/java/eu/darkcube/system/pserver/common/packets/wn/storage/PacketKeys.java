/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn.storage;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;

import java.util.Collection;

public class PacketKeys extends Packet {
	private final UniqueId id;

	public PacketKeys(UniqueId id) {
		this.id = id;
	}

	public UniqueId id() {
		return id;
	}

	public static class Response extends Packet {
		private final Collection<Key> keys;

		public Response(Collection<Key> keys) {
			this.keys = keys;
		}

		public Collection<Key> keys() {
			return keys;
		}
	}
}
