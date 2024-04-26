/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn.storage;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;

public class PacketHas extends Packet {
	private final UniqueId id;
	private final Key key;

	public PacketHas(UniqueId id, Key key) {
		this.id = id;
		this.key = key;
	}

	public UniqueId id() {
		return id;
	}

	public Key key() {
		return key;
	}

	public static class Response extends Packet {
		private boolean has;

		public Response(boolean has) {
			this.has = has;
		}

		public boolean has() {
			return has;
		}
	}
}
