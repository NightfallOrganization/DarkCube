/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperString extends Packet {

	private String string;

	public PacketNodeWrapperString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

}
