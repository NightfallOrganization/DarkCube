/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerSnapshot;

import java.util.Collection;
import java.util.HashSet;

public class PacketPServers extends Packet {
	public static class Response extends Packet {
		private final Collection<PServerSnapshot> snapshots;

		public Response(Collection<PServerSnapshot> snapshots) {
			this.snapshots = new HashSet<>(snapshots);
		}

		public Collection<PServerSnapshot> snapshots() {
			return snapshots;
		}
	}
}
