/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.pserver.common.PServerSerializable;

public class PacketNodeWrapperPServer extends Packet {

	private PServerSerializable pserver;

	public PacketNodeWrapperPServer(PServerSerializable pserver) {
		this.pserver = pserver;
	}

	public PServerSerializable getInfo() {
		return pserver;
	}
}