/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import java.util.stream.Collectors;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRetrievePServers;

public class HandlerRetrievePServers implements PacketHandler<PacketWrapperNodeRetrievePServers> {

	@Override
	public Packet handle(PacketWrapperNodeRetrievePServers packet) {
		return new PacketNodeWrapperPServers(NodePServerProvider.getInstance().getPServers().stream().map(s -> s.getSerializable())
				.collect(Collectors.toSet()));
	}
}
