/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

import java.util.UUID;

public class PacketNWUserPersistentDataRemove extends Packet {
    private final UUID uniqueId;
    private final Key key;

    public PacketNWUserPersistentDataRemove(UUID uniqueId, Key key) {
        this.uniqueId = uniqueId;
        this.key = key;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public Key key() {
        return key;
    }
}
