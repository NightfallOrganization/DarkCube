/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetOwners;

public class HandlerGetOwners implements PacketHandler<PacketWrapperNodeGetOwners> {

	@Override
	public Packet handle(PacketWrapperNodeGetOwners packet) {
		return new PacketNodeWrapperOwners(
				DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(packet.getPServer()));
	}
}
