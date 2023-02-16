/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketWrapperNodeConnectPlayer;

public class HandlerConnectPlayer implements PacketHandler<PacketWrapperNodeConnectPlayer> {

	@Override
	public Packet handle(PacketWrapperNodeConnectPlayer packet) {
		PServerProvider.getInstance().getPServerOptional(packet.getPServer())
				.ifPresent(ps -> ps.connectPlayer(packet.getPlayer()));
		return null;
	}
}
