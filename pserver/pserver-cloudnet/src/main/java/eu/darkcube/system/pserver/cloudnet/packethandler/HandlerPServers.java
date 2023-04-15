/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServers;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServers.Response;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class HandlerPServers implements PacketHandler<PacketPServers> {
	@Override
	public Packet handle(PacketPServers packet) throws Throwable {
		return new Response(PServerProvider.instance().pservers().get().stream()
				.map(PServerExecutor::createSnapshot).map(f -> {
					try {
						return f.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList()));
	}
}
