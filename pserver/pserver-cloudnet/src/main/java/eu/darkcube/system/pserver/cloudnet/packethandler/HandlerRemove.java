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
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperPServerNotFound;

import java.util.Optional;

public class HandlerRemove implements PacketHandler<PacketWrapperNodeRemove> {

	@Override
	public Packet handle(PacketWrapperNodeRemove packet) {
		Optional<? extends PServerExecutor> op =
				PServerProvider.getInstance().getPServerOptional(packet.getId());
		if (op.isPresent()) {
			op.get().remove();
			return new PacketNodeWrapperActionConfirm();
		}
		return new PacketNodeWrapperPServerNotFound(packet.getId());
	}
}
