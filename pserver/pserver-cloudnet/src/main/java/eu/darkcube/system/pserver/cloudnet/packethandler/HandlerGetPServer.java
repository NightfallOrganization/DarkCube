/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperPServerNotFound;
import eu.darkcube.system.pserver.common.packets.PacketWrapperNodeGetPServer;

import java.util.Optional;

public class HandlerGetPServer implements PacketHandler<PacketWrapperNodeGetPServer> {

	@Override
	public Packet handle(PacketWrapperNodeGetPServer packet) {
		Optional<? extends PServerExecutor> op =
				PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			return new PacketNodeWrapperPServer(op.get().getSerializable());
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}
