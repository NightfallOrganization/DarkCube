/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.PacketNodeWrapperActionConfirm;
import eu.darkcube.system.pserver.common.packets.PacketWrapperNodeDelete;

public class HandlerDelete implements PacketHandler<PacketWrapperNodeDelete> {

	@Override
	public Packet handle(PacketWrapperNodeDelete packet) {
		PServerProvider.getInstance().delete(packet.getId());
		return new PacketNodeWrapperActionConfirm();
	}
}
