/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketNodeWrapperDataUpdate extends Packet {
	private UniqueId pserverId;
	private JsonDocument data;

	public PacketNodeWrapperDataUpdate(UniqueId pserverId, JsonDocument data) {
		this.pserverId = pserverId;
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}

	public UniqueId getPServerId() {
		return this.pserverId;
	}
}
