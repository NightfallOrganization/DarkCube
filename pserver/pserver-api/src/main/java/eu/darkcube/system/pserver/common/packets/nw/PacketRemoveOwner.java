/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packets.nw;

import java.util.UUID;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketRemoveOwner extends Packet {
    private final UniqueId id;
    private final UUID owner;

    public PacketRemoveOwner(UniqueId id, UUID owner) {
        this.id = id;
        this.owner = owner;
    }

    public UniqueId id() {
        return id;
    }

    public UUID owner() {
        return owner;
    }
}
