/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.plugin;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

public class PacketPluginDataSet extends Packet {
	private String plugin;
	private JsonDocument data;

	public PacketPluginDataSet(String plugin, JsonDocument data) {
		this.plugin = plugin;
		this.data = data;
	}

	public String getPlugin() {
		return plugin;
	}

	public JsonDocument getData() {
		return data;
	}
}
