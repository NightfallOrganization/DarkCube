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
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketHas;

public class HandlerHas implements PacketHandler<PacketHas> {
	@Override
	public Packet handle(PacketHas packet) throws Throwable {
		return new PacketHas.Response(
				PServerProvider.instance().pserver(packet.id()).get().storage().has(packet.key()));
	}
}
