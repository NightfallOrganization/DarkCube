/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.packethandler.storage;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketClearCache;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketClearCache.Response;

public class HandlerClearCache implements PacketHandler<PacketClearCache> {
    @Override
    public Packet handle(PacketClearCache packet) throws Throwable {
        PServerProvider.instance().pserver(packet.id()).get().storage().clearCache();
        return new Response();
    }
}
