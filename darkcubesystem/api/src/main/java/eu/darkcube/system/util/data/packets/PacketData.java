/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

public class PacketData extends Packet {
	private final JsonDocument data;

	public PacketData(JsonDocument data) {
		this.data = data;
	}

	public JsonDocument data() {
		return data;
	}
}
