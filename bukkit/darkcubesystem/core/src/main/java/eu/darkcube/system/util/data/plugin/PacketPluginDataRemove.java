/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.plugin;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

public class PacketPluginDataRemove extends Packet {
	private String plugin;
	private Key key;

	public PacketPluginDataRemove(String plugin, Key key) {
		this.plugin = plugin;
		this.key = key;
	}

	public String getPlugin() {
		return plugin;
	}

	public Key getKey() {
		return key;
	}
}
