/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetRunning;

public class HandlerSetRunning implements PacketHandler<PacketWrapperNodeSetRunning> {

	@Override
	public Packet handle(PacketWrapperNodeSetRunning packet) {
		NodePServerProvider.getInstance().getPServerOptional(packet.getId()).ifPresent(ps -> {
			ps.setState(State.RUNNING);
		});
		return null;
	}
}
