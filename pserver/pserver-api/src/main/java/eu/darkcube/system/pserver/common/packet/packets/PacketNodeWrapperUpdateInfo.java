/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperUpdateInfo extends Packet {

	private PServerSerializable info;

	public PacketNodeWrapperUpdateInfo(PServerSerializable info) {
		this.info = info;
	}

	public PServerSerializable getInfo() {
		return info;
	}
}
