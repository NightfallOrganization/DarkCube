/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.PacketStart;
import eu.darkcube.system.pserver.common.packets.wn.PacketStart.Response;

public class HandlerStart implements PacketHandler<PacketStart> {
    @Override
    public Packet handle(PacketStart packet) {
        return new Response(PServerProvider.instance().pserver(packet.id()).join().start().join());
    }
}
