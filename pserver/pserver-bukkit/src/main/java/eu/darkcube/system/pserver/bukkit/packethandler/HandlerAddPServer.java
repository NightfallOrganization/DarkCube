/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddPServer;

public class HandlerAddPServer implements PacketHandler<PacketNodeWrapperAddPServer> {

	@Override
	public Packet handle(PacketNodeWrapperAddPServer packet) {
		WrapperPServerProvider.getInstance().updateAndInsertIfNecessary(packet.getPServer());
		return null;
	}
}
