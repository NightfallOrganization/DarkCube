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
import eu.darkcube.system.pserver.common.packets.wn.PacketState;
import eu.darkcube.system.pserver.common.packets.wn.PacketState.Response;

import java.util.concurrent.ExecutionException;

public class HandlerState implements PacketHandler<PacketState> {
    @Override
    public Packet handle(PacketState packet) throws ExecutionException, InterruptedException {
        return new Response(PServerProvider.instance().pserver(packet.id()).get().state().get());
    }
}
