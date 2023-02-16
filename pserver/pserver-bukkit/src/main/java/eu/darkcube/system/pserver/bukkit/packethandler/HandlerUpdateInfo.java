/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.bukkit.WrapperPServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperUpdateInfo;

public class HandlerUpdateInfo implements PacketHandler<PacketNodeWrapperUpdateInfo> {
	@Override
	public Packet handle(PacketNodeWrapperUpdateInfo packet) {
		WrapperPServerProvider.getInstance().update(packet.getInfo());
		return null;
	}
}
