/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.listener;

import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.MinecraftServer;
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
        @Nullable var instance = event.getInstance();
        if (instance == null) { // TODO wait for minestom to fix this
            var chunk = lastChunk.get(event.getPlayer());
            if (chunk != null) instance = chunk.getInstance();
        }
        if (instance == null) {
            System.out.println("Something went wrong when unloading chunks");
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
            if (playerChunk != chunk) {
                instance.saveChunkToStorage(chunk);
                instance.unloadChunk(chunk);
            } else {
                if (playerChunk.getViewers().isEmpty()) {
                    instance.saveChunkToStorage(playerChunk);
                    instance.unloadChunk(playerChunk);
                    var player = event.getPlayer();
                    MinecraftServer.getSchedulerManager().scheduleNextProcess(() -> lastChunk.remove(player));
                } else {
                    lastChunk.put(event.getPlayer(), playerChunk);
                }
            }
        }
    }
}
