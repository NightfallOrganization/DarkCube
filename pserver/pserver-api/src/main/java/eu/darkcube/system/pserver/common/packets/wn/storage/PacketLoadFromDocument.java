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

public class PacketLoadFromDocument extends Packet {
    private final UniqueId id;
    private final Document data;

    public PacketLoadFromDocument(UniqueId id, Document data) {
        this.id = id;
        this.data = data;
    }

    public UniqueId id() {
        return id;
    }

    public Document data() {
        return data;
    }

    public static class Response extends Packet {

    }
}
