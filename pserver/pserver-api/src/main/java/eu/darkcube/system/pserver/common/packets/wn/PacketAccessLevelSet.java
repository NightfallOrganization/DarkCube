/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketAccessLevelSet extends Packet {
	private final UniqueId id;
	private final AccessLevel accessLevel;

	public PacketAccessLevelSet(UniqueId id, AccessLevel accessLevel) {
		this.id = id;
		this.accessLevel = accessLevel;
	}

	public UniqueId id() {
		return id;
	}

	public AccessLevel accessLevel() {
		return accessLevel;
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
