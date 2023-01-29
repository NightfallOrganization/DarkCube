/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperData extends Packet {

	private JsonDocument data;

	public PacketNodeWrapperData(JsonDocument data) {
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}
}
