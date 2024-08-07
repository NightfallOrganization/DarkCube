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
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.nw.PacketUpdate;

public class HandlerUpdate implements PacketHandler<PacketUpdate> {
    @Override
    public Packet handle(PacketUpdate packet) throws Throwable {
        InjectionLayer.boot().instance(EventManager.class).callEvent(new PServerUpdateEvent(PServerProvider.instance().pserver(packet.snapshot().uniqueId()).get()));
        return null;
    }
}
