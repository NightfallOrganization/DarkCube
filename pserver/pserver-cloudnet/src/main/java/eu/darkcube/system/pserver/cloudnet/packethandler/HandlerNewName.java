/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperString;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewName;

public class HandlerNewName implements PacketHandler<PacketWrapperNodeNewName> {

	@Override
	public Packet handle(PacketWrapperNodeNewName packet) {
		return new PacketNodeWrapperString(PServerProvider.getInstance().newName());
	}
}
