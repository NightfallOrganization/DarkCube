/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayersSet;

public class HandlerOnlinePlayersSet implements PacketHandler<PacketOnlinePlayersSet> {
    @Override
    public Packet handle(PacketOnlinePlayersSet packet) throws Throwable {
        NodePServerProvider.instance().pserver(packet.id()).get().setOnlinePlayers(packet.onlinePlayers());
        return null;
    }
}
