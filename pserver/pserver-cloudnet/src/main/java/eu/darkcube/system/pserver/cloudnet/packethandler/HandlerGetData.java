/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperData;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetData;

public class HandlerGetData implements PacketHandler<PacketWrapperNodeGetData> {

	@Override
	public Packet handle(PacketWrapperNodeGetData packet) {
		return new PacketNodeWrapperData(
				PServerProvider.getInstance().getPServerData(packet.getPServerId()));
	}

}
