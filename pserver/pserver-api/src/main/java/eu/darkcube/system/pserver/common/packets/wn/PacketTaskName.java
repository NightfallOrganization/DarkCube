/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketTaskName extends Packet {

	private final UniqueId id;

	public PacketTaskName(UniqueId id) {
		this.id = id;
	}

	public UniqueId id() {
		return id;
	}

	public static class Response extends Packet {
		private final String taskName;

		public Response(String taskName) {
			this.taskName = taskName;
		}

		public String taskName() {
			return taskName;
		}
	}
}
