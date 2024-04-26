/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.packethandler;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.packets.nw.PacketRemoveOwner;

public class HandlerRemoveOwner implements PacketHandler<PacketRemoveOwner> {
    @Override public Packet handle(PacketRemoveOwner packet) throws Throwable {
        InjectionLayer
                .boot()
                .instance(EventManager.class)
                .callEvent(new PServerRemoveOwnerEvent(PServerProvider.instance().pserver(packet.id()).get(), packet.owner()));
        return null;
    }
}
