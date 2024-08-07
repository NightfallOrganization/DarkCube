/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packets.wn;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.UniqueId;

public class PacketAccessLevel extends Packet {
    private final UniqueId id;

    public PacketAccessLevel(UniqueId id) {
        this.id = id;
    }

    public UniqueId id() {
        return id;
    }

    public static class Response extends Packet {
        private final AccessLevel accessLevel;

        public Response(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
        }

        public AccessLevel accessLevel() {
            return accessLevel;
        }
    }
}
