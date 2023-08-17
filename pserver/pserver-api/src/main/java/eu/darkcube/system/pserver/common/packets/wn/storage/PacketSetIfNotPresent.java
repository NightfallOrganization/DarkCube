/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn.storage;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;

public class PacketSetIfNotPresent extends Packet {
    private final UniqueId id;
    private final Key key;
    private final Document data;

    public PacketSetIfNotPresent(UniqueId id, Key key, Document data) {
        this.id = id;
        this.key = key;
        this.data = data;
    }

    public UniqueId id() {
        return id;
    }

    public Key key() {
        return key;
    }

    public Document data() {
        return data;
    }

    public static class Response extends Packet {

    }
}
