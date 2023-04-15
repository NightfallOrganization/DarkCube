/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

import java.util.UUID;

public class PacketConnectPlayer extends Packet {
	private final UniqueId id;
	private final UUID player;

	public PacketConnectPlayer(UniqueId id, UUID player) {
		this.id = id;
		this.player = player;
	}

	public UUID player() {
		return player;
	}

	public UniqueId id() {
		return id;
	}

	public static class Response extends Packet {
		private final boolean success;

		public Response(boolean success) {
			this.success = success;
		}

		public boolean success() {
			return success;
		}
	}
}
