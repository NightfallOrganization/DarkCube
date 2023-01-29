/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeCreatePServer;

public class HandlerCreatePServer implements PacketHandler<PacketWrapperNodeCreatePServer> {

	@Override
	public Packet handle(PacketWrapperNodeCreatePServer packet) {
		//		Packet ps = new PacketNodeWrapperPServer(NodePServerProvider.getInstance()
		//				.createPServer(packet.getInfo(), packet.getTask() == null ? null
		//						: CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(packet.getTask()))
		//				.getSerializable());
		return new PacketNodeWrapperPServer(
				NodePServerProvider.getInstance().createPServer(packet.getInfo())
						.getSerializable());
	}
}
