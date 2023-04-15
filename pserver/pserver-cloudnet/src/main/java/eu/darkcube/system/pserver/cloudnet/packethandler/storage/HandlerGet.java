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
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketGet;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketGet.Response;

public class HandlerGet implements PacketHandler<PacketGet> {
	@Override
	public Packet handle(PacketGet packet) throws Throwable {
		return new Response(NodePServerProvider.instance().pserver(packet.id()).get().storage()
				.get(packet.key()));
	}
}
