/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketType;

public class HandlerType implements PacketHandler<PacketType> {
    @Override public Packet handle(PacketType packet) {
        return new PacketType.Response(NodePServerProvider.instance().pserver(packet.id()).join().type().join());
    }
}
