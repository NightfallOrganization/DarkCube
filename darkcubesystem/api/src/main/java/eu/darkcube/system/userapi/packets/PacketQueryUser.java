/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketQueryUser extends Packet {

    private final UUID uuid;

    public PacketQueryUser(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public static class Result extends Packet {
        private final String name;
        private final Document data;

        public Result(String name, Document data) {
            this.name = name;
            this.data = data;
        }

        public Document getData() {
            return data;
        }

        public String getName() {
            return name;
        }
    }
}
