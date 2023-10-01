/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.player;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.network.player.PlayerConnection;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DarkCubePlayer extends Player {
    private static final Key PLAYER_TICKET_MANAGER = Key.key("darkcube", "player_ticket_manager");
    private final ConcurrentHashMap<Key, Object> metadata = new ConcurrentHashMap<>();

    public DarkCubePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public PlayerTicketManager playerTicketManager() {
        return (PlayerTicketManager) metadata.get(PLAYER_TICKET_MANAGER);
    }

    void playerTicketManager(PlayerTicketManager playerTicketManager) {
        metadata.put(PLAYER_TICKET_MANAGER, playerTicketManager);
    }

    @Override protected void sendChunkUpdates(Chunk newChunk) {
        super.sendChunkUpdates(newChunk);
        if (currentChunk != null && newChunk != null) {
            if (currentChunk.getChunkX() != newChunk.getChunkX() || currentChunk.getChunkZ() != newChunk.getChunkZ()) {
                playerTicketManager().move(newChunk.getChunkX(), newChunk.getChunkZ());
            }
        }
    }
}
