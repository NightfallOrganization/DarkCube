/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.internal;

import eu.darkcube.system.packetapi.Packet;

public class PacketDeclareProtocolVersion extends Packet {
    private final String serverName;
    private final int protocolVersion;

    public PacketDeclareProtocolVersion(String serverName, int protocolVersion) {
        this.serverName = serverName;
        this.protocolVersion = protocolVersion;
    }

    public String serverName() {
        return serverName;
    }

    public int protocolVersion() {
        return protocolVersion;
    }
}
