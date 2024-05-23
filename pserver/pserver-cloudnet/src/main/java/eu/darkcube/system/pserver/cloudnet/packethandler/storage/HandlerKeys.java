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
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketKeys;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketKeys.Response;

public class HandlerKeys implements PacketHandler<PacketKeys> {
    @Override
    public Packet handle(PacketKeys packet) throws Throwable {
        return new Response(PServerProvider.instance().pserver(packet.id()).get().storage().keys());
    }
}
