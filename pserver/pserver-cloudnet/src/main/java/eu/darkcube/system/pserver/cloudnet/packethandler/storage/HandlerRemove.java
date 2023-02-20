/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler.storage;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketRemove;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketRemove.Response;

public class HandlerRemove implements PacketHandler<PacketRemove> {
	@Override
	public Packet handle(PacketRemove packet) throws Throwable {
		return new Response(NodePServerProvider.instance().pserver(packet.id()).get().storage()
				.remove(packet.key()));
	}
}
