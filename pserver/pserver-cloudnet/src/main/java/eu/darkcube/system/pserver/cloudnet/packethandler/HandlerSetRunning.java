/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerExecutor;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketSetRunning;

public class HandlerSetRunning implements PacketHandler<PacketSetRunning> {
    @Override public Packet handle(PacketSetRunning packet) throws Throwable {
        NodePServerProvider.instance().pserver(packet.id()).thenAccept(NodePServerExecutor::setRunning);
        return null;
    }
}
