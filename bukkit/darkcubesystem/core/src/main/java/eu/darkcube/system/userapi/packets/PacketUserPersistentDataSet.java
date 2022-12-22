/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketUserPersistentDataSet extends Packet {
	private UUID uuid;
	private JsonDocument data;

	public PacketUserPersistentDataSet(UUID uuid, JsonDocument data) {
		this.uuid = uuid;
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}

	public UUID getUniqueId() {
		return this.uuid;
	}
}
