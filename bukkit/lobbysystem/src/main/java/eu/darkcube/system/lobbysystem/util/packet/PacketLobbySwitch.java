/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.packet;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketLobbySwitch extends Packet {

    private final UUID uuid;
    private final Document location;

    public PacketLobbySwitch(UUID uuid, Document location) {
        this.uuid = uuid;
        this.location = location;
    }

    public Document getLocation() {
        return location;
    }

    public UUID getUniqueId() {
        return uuid;
    }
}
