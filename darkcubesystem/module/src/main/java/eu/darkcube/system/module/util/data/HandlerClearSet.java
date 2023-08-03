/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.util.data;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataClearSet;

class HandlerClearSet implements PacketHandler<PacketWrapperNodeDataClearSet> {
    @Override
    public Packet handle(PacketWrapperNodeDataClearSet packet) {
        SynchronizedPersistentDataStorages.storage(packet.key()).loadFromJsonDocument(packet.data());
        return null;
    }
}
