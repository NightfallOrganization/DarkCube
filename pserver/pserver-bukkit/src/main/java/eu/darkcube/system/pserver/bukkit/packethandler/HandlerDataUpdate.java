/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperDataUpdate;

public class HandlerDataUpdate implements PacketHandler<PacketNodeWrapperDataUpdate> {

	@Override
	public Packet handle(PacketNodeWrapperDataUpdate packet) {
		WrapperPServerProvider.getInstance().update(packet.getPServerId(), packet.getData());
		return null;
	}

}
