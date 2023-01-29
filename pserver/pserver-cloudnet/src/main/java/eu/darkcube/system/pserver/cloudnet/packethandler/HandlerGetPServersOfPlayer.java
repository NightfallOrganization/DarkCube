/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerIDs;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServersOfPlayer;

public class HandlerGetPServersOfPlayer implements PacketHandler<PacketWrapperNodeGetPServersOfPlayer> {

	@Override
	public Packet handle(PacketWrapperNodeGetPServersOfPlayer packet) {
		return new PacketNodeWrapperPServerIDs(PServerProvider.getInstance().getPServerIDs(packet.getPlayer()));
	}
}
