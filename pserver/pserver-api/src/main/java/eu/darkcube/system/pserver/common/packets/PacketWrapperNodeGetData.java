/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.pserver.common.UniqueId;

public class PacketWrapperNodeGetData extends Packet {

	private UniqueId pserverId;

	public PacketWrapperNodeGetData(UniqueId pserverId) {
		this.pserverId = pserverId;
	}

	public UniqueId getPServerId() {
		return this.pserverId;
	}

}
