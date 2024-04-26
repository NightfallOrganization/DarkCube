/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler.storage;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketClear;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketClear.Response;

public class HandlerClear implements PacketHandler<PacketClear> {
	@Override
	public Packet handle(PacketClear packet) throws Throwable {
		PServerProvider.instance().pserver(packet.id()).get().storage().clear();
		return new Response();
	}
}
