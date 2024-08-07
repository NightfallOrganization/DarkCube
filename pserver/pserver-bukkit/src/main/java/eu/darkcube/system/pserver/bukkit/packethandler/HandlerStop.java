/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.cloudnet.packetapi.PacketHandler;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.nw.PacketStop;

public class HandlerStop implements PacketHandler<PacketStop> {
    @Override
    public Packet handle(PacketStop packet) throws Throwable {
        InjectionLayer.boot().instance(EventManager.class).callEvent(new PServerStopEvent(PServerProvider.instance().pserver(packet.snapshot().uniqueId()).get()));
        return null;
    }
}
