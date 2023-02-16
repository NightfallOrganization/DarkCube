/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import java.util.UUID;

public class PacketWrapperNodeGetPServersOfPlayer extends Packet {

	private UUID player;

	public PacketWrapperNodeGetPServersOfPlayer(UUID player) {
		this.player = player;
	}

	public UUID getPlayer() {
		return player;
	}
}