/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler.storage;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketSet;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketSet.Response;

public class HandlerSet implements PacketHandler<PacketSet> {
    @Override public Packet handle(PacketSet packet) {
//        NodePServerProvider.instance().pserver(packet.id()).thenAccept(ps -> {
//            ps.storage().append(packet.data());
//        }).join();
        NodePServerProvider.instance().pserver(packet.id()).join().storage().append(packet.data());
        return new Response();
    }
}
