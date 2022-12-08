/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.NodePServer;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetUniqueId;

public class HandlerGetUniqueId implements PacketHandler<PacketWrapperNodeGetUniqueId> {

	@Override
	public Packet handle(PacketWrapperNodeGetUniqueId packet) {
		for (NodePServer ps : NodePServerProvider.getInstance().getPServers()) {
			if (ps.getServiceId() != null) {
				if (ps.getServiceId().getUniqueId().equals(packet.getServiceId().getUniqueId())) {
					return new PacketNodeWrapperUniqueId(ps.getId());
				}
			}
		}
		return null;
	}
}
