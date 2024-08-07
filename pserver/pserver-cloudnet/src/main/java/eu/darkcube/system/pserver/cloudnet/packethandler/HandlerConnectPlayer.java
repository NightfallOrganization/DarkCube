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
import eu.darkcube.system.pserver.common.packets.wn.PacketConnectPlayer;
import eu.darkcube.system.pserver.common.packets.wn.PacketConnectPlayer.Response;

import java.util.concurrent.ExecutionException;

public class HandlerConnectPlayer implements PacketHandler<PacketConnectPlayer> {
    @Override
    public Packet handle(PacketConnectPlayer packet) throws ExecutionException, InterruptedException {
        return new Response(NodePServerProvider.instance().pserver(packet.id()).get().connectPlayer(packet.player()).get());
    }
}
