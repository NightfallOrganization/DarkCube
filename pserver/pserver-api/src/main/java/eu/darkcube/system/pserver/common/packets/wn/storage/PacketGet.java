/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn.storage;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;

public class PacketGet extends Packet {
	private final UniqueId id;
	private final Key key;

	public PacketGet(UniqueId id, Key key) {
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
		private final JsonDocument data;

		public Response(JsonDocument data) {
			this.data = data;
		}

		public JsonDocument data() {
			return data;
		}
	}
}
