/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.listener;

import java.util.concurrent.ConcurrentHashMap;

import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Chunk;

public class ChunkUnloader {

    private static ConcurrentHashMap<Player, Chunk> lastChunk = new ConcurrentHashMap<>();

    public static void entityDespawn(EntityDespawnEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        lastChunk.put(player, player.getChunk());
        System.out.println("Despawn");
    }

    public static void playerSpawn(PlayerSpawnEvent event) {
        var player = event.getPlayer();
        var chunk = lastChunk.remove(player);
        if (chunk == null) return;
        if (chunk.getViewers().isEmpty()) {
            chunk.getInstance().unloadChunk(chunk);
        }
    }

    public static void playerDisconnect(PlayerDisconnectEvent event) {
        lastChunk.remove(event.getPlayer());
    }

    public static void playerChunkUnload(PlayerChunkUnloadEvent event) {
        var instance = event.getInstance();
        if (instance == null) { // TODO wait for minestom to fix this
            instance = lastChunk.get(event.getPlayer()).getInstance();
        }
        if (instance == null) {
            System.out.println("Bad instance shitting");
            return;
        }
        var playerChunk = event.getPlayer().getChunk();
        var chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
        if (chunk != null) {
            for (var player : instance.getPlayers()) {
                if (player != event.getPlayer() && chunk.getViewers().contains(player)) {
                    return;
                }
            }
            if (playerChunk != chunk) instance.unloadChunk(chunk);
        }
    }
}
