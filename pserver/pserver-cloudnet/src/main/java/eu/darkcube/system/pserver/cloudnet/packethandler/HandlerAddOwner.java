/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeAddOwner;

public class HandlerAddOwner implements PacketHandler<PacketWrapperNodeAddOwner> {

	@Override
	public Packet handle(PacketWrapperNodeAddOwner packet) {
//		if (NodePServerProvider.getInstance().isPServer(packet.getId())) {
//			NodePServer ps = NodePServerProvider.getInstance().getPServer(packet.getId());
//			ps.addOwner(packet.getOwner());
//			return new PacketNodeWrapperActionConfirm();
//		}
//		return new PacketNodeWrapperPServerNotFound(packet.getId());
		NodePServerProvider.getInstance().addOwner(packet.getId(), packet.getOwner());
		return new PacketNodeWrapperActionConfirm();
	}
}
