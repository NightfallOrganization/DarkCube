/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.packet;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketLobbySwitch extends Packet {

	private final UUID uuid;
	private final JsonDocument location;

	public PacketLobbySwitch(UUID uuid, JsonDocument location) {
		this.uuid = uuid;
		this.location = location;
	}

	public JsonDocument getLocation() {
		return location;
	}

	public UUID getUniqueId() {
		return uuid;
	}
}
