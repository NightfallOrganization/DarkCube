/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketRemoveOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketRemoveOwner.Response;

public class HandlerRemoveOwner implements PacketHandler<PacketRemoveOwner> {
    @Override public Packet handle(PacketRemoveOwner packet) {
        return new Response(PServerProvider.instance().pserver(packet.id()).join().removeOwner(packet.owner()).join());
    }
}
