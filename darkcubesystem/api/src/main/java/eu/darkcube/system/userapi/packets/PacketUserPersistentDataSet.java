/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketUserPersistentDataSet extends Packet {
    private UUID uuid;
    private Document data;

    public PacketUserPersistentDataSet(UUID uuid, Document data) {
        this.uuid = uuid;
        this.data = data;
    }

    public Document getData() {
        return this.data;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }
}
