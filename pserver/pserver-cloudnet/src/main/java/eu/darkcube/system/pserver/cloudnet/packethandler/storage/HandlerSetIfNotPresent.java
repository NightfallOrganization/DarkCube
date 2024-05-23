/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler.storage;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.pserver.cloudnet.NodePServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketSetIfNotPresent;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketSetIfNotPresent.Response;

public class HandlerSetIfNotPresent implements PacketHandler<PacketSetIfNotPresent> {
    @Override
    public Packet handle(PacketSetIfNotPresent packet) throws Throwable {
        NodePServerProvider.instance().pserver(packet.id()).get().storage().setIfNotPresent(packet.key(), packet.data());
        return new Response();
    }
}
