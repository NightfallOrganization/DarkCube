/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.util.data.Key;

import java.util.UUID;

public class PacketWNUserPersistentDataRemove extends Packet {
    private UUID uuid;
    private Key key;

    public PacketWNUserPersistentDataRemove(UUID uuid, Key key) {
        this.uuid = uuid;
        this.key = key;
    }

    public Key key() {
        return this.key;
    }

    public UUID uniqueId() {
        return this.uuid;
    }
}
