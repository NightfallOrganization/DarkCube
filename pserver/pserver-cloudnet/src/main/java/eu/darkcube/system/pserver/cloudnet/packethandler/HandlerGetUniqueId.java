/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerExecutor;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packets.PacketWrapperNodeGetUniqueId;

public class HandlerGetUniqueId implements PacketHandler<PacketWrapperNodeGetUniqueId> {

	@Override
	public Packet handle(PacketWrapperNodeGetUniqueId packet) {
		for (NodePServerExecutor ps : NodePServerProvider.getInstance().getPServers()) {
			if (ps.getServiceId() != null) {
				if (ps.getServiceId().getUniqueId().equals(packet.getServiceId().getUniqueId())) {
					return new PacketNodeWrapperUniqueId(ps.getId());
				}
			}
		}
		return null;
	}
}
