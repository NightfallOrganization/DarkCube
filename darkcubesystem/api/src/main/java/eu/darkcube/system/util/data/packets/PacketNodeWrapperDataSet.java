/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

public class PacketNodeWrapperDataSet extends Packet {
	private final Key key;
	private final JsonDocument data;

	public PacketNodeWrapperDataSet(Key key, JsonDocument data) {
		this.key = key;
		this.data = data;
	}

	public JsonDocument data() {
		return data;
	}

	public Key key() {
		return key;
	}
}
