/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packets.nw;

import eu.darkcube.system.cloudnet.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerSnapshot;

public class PacketUpdate extends Packet {
    private final PServerSnapshot snapshot;

    public PacketUpdate(PServerSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public PServerSnapshot snapshot() {
        return snapshot;
    }
}
