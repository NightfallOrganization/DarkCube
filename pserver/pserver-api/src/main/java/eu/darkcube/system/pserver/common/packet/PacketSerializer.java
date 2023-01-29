/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet;

import com.google.gson.JsonSyntaxException;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public class PacketSerializer {

	public static JsonDocument insert(JsonDocument doc, Packet packet) {
		if (packet != null) {
			doc.append("packetClass", packet.getClass().getName());
			doc.append("packet", packet);
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Packet> getClass(JsonDocument doc) {
		try {
			return (Class<? extends Packet>) Class.forName(doc.getString("packetClass"));
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <T extends Packet> T getPacket(JsonDocument doc, Class<T> clazz) throws JsonSyntaxException {
		return doc.get("packet", clazz);
	}
}
