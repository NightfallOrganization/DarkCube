/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;

public class PacketWrapperNodeAccessLevel extends Packet {
	private final AccessLevel accessLevel;

	public PacketWrapperNodeAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	public AccessLevel accessLevel() {
		return accessLevel;
	}
}
