/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperBoolean extends Packet {

	private boolean bool;
	
	public PacketNodeWrapperBoolean(boolean bool) {
		this.bool = bool;
	}
	
	public boolean is() {
		return bool;
	}
}
