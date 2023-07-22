/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.packetapi;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.com.google.gson.JsonSyntaxException;

public class PacketSerializer {

	public static JsonDocument serialize(Packet packet) {
		if (packet != null) {
			return JsonDocument.newDocument().append("packetClass", packet.getClass().getName())
					.append("packet", packet);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Packet> getClass(JsonDocument doc) {
		try {
			return (Class<? extends Packet>) Class.forName(doc.getString("packetClass"));
		} catch (ClassNotFoundException ignored) {
		}
		return null;
	}

	public static <T extends Packet> T getPacket(JsonDocument doc, Class<T> clazz)
	throws JsonSyntaxException {
		return doc.get("packet", clazz);
	}
}
