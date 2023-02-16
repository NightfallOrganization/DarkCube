/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.pserver.common.UniqueId;

import java.util.UUID;

public class PacketWrapperNodeConnectPlayer extends Packet {

	private UUID player;
	private UniqueId pserver;

	public PacketWrapperNodeConnectPlayer(UUID player, UniqueId pserver) {
		this.player = player;
		this.pserver = pserver;
	}

	public UUID getPlayer() {
		return player;
	}

	public UniqueId getPServer() {
		return pserver;
	}
}
