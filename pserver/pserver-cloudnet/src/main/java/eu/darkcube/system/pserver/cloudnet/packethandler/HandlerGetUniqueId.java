/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.packets.wn.PacketGetUniqueId;
import eu.darkcube.system.pserver.common.packets.wn.PacketGetUniqueId.Response;

public class HandlerGetUniqueId implements PacketHandler<PacketGetUniqueId> {
	@Override
	public Packet handle(PacketGetUniqueId packet) throws Throwable {
		for (PServerExecutor ps : NodePServerProvider.instance().pservers().get()) {
			String serverName = ps.serverName().get();
			if (serverName != null && serverName.equals(packet.serviceId().getName())) {
				return new Response(ps.id());
			}
		}
		return new Response(null);
	}
}
