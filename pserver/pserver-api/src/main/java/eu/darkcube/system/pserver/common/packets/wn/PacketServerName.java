/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketServerName extends Packet {

    private final UniqueId id;

    public PacketServerName(UniqueId id) {
        this.id = id;
    }

    public UniqueId id() {
        return id;
    }

    public static class Response extends Packet {
        private final String serverName;

        public Response(String serverName) {
            this.serverName = serverName;
        }

        public String serverName() {
            return serverName;
        }
    }
}
