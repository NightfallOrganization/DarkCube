/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets.wn.storage;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketStoreToDocument extends Packet {
    private final UniqueId id;

    public PacketStoreToDocument(UniqueId id) {
        this.id = id;
    }

    public UniqueId id() {
        return id;
    }

    public static class Response extends Packet {
        private final Document data;

        public Response(Document data) {
            this.data = data;
        }

        public Document data() {
            return data;
        }
    }
}
