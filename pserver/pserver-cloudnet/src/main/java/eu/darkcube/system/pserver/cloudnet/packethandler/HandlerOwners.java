/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketOwners;

import java.util.concurrent.ExecutionException;

public class HandlerOwners implements PacketHandler<PacketOwners> {
	@Override
	public Packet handle(PacketOwners packet) throws ExecutionException, InterruptedException {
		return new PacketOwners.Response(
				PServerProvider.instance().pserver(packet.id()).get().owners().get());
	}
}
