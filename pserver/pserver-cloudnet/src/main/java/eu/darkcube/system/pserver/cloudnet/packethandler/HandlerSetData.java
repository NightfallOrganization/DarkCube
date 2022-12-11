/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetData;

import java.util.concurrent.ExecutionException;

public class HandlerSetData implements PacketHandler<PacketWrapperNodeSetData> {

	@Override
	public Packet handle(PacketWrapperNodeSetData packet) {
		try {
			NodePServerProvider.getInstance().setPServerData(packet.getPServerId(), packet.getData()).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		return new PacketNodeWrapperActionConfirm();
	}

}
