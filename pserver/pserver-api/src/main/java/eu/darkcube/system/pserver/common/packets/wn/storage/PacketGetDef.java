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
import eu.darkcube.system.util.data.Key;

public class PacketGetDef extends Packet {
    private final UniqueId id;
    private final Key key;
    private final Document def;

    public PacketGetDef(UniqueId id, Key key, Document def) {
        this.id = id;
        this.key = key;
        this.def = def;
    }

    public UniqueId id() {
        return id;
    }

    public Key key() {
        return key;
    }

    public Document def() {
        return def;
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
