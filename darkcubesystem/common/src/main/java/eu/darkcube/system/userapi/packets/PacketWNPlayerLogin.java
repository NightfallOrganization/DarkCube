/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.packets;

import eu.darkcube.system.packetapi.Packet;

import java.util.UUID;

public class PacketWNPlayerLogin extends Packet {
    private final UUID playerId;
    private final String playerName;

    public PacketWNPlayerLogin(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    public static class Response extends Packet {
    }
}
