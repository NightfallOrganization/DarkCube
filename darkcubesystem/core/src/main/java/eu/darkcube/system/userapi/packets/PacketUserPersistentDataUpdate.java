/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketUserPersistentDataUpdate extends Packet {

	private final UUID uuid;
	private final JsonDocument data;

	public PacketUserPersistentDataUpdate(UUID uuid, JsonDocument data) {
		this.uuid = uuid;
		this.data = data;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public JsonDocument getData() {
		return data;
	}
}
