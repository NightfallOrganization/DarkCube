/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.player.PlayerChunkLoadEvent;
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
        System.out.println(lastChunk);
    }

    private static AtomicInteger count = new AtomicInteger();

    public static void playerChunkLoad(PlayerChunkLoadEvent event) {
        count.incrementAndGet();
    }

    public static void playerChunkUnload(PlayerChunkUnloadEvent event) {
        count.decrementAndGet();
        var instance = event.getInstance();
        if (instance == null) { // TODO wait for minestom to fix this
            var chunk = lastChunk.get(event.getPlayer());
            if (chunk != null) instance = chunk.getInstance();
        }
        if (instance == null) {
            System.out.println("Bad instance querying");
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
                instance.unloadChunk(chunk);
            } else if (playerChunk != null) {
                if (playerChunk.getViewers().isEmpty()) {
                    instance.unloadChunk(playerChunk);
                    var player = event.getPlayer();
                    MinecraftServer.getSchedulerManager().scheduleNextProcess(()->{
                        lastChunk.remove(player);
                    });
                } else {
                    lastChunk.put(event.getPlayer(), playerChunk);
                }
            }
        }
    }
}
