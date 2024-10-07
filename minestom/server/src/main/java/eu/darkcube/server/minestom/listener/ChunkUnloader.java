/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.server.minestom.listener;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import eu.darkcube.server.minestom.instance.DoNotSave;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.instance.InstanceRegisterEvent;
import net.minestom.server.event.instance.InstanceUnregisterEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.chunk.ChunkUtils;

public class ChunkUnloader {

    private static ConcurrentHashMap<Player, Chunk> lastChunk = new ConcurrentHashMap<>();
    private static Map<Instance, SaveTasks> saveTaskMap = new ConcurrentHashMap<>();

    public static void entityDespawn(EntityDespawnEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        lastChunk.put(player, Objects.requireNonNull(player.getChunk()));
    }

    public static void playerSpawn(PlayerSpawnEvent event) {
        var player = event.getPlayer();
        var chunk = lastChunk.remove(player);
        if (chunk == null) return;
        if (chunk.getViewers().isEmpty()) {
            saveAndUnload(chunk.getInstance(), chunk, event.getPlayer());
        }
    }

    public static void instanceRegister(InstanceRegisterEvent event) {
        var instance = event.getInstance();
        if (instance instanceof DoNotSave) return;
        saveTaskMap.put(instance, new SaveTasks());
    }

    public static void instanceUnregister(InstanceUnregisterEvent event) {
        var instance = event.getInstance();
        if (instance instanceof DoNotSave) return;
        saveTaskMap.remove(instance).await();
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
        var player = event.getPlayer();
        var playerChunk = player.getChunk();
        var chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
        if (chunk != null) {
            if (!chunk.getViewers().isEmpty()) {
                for (var instancePlayer : instance.getPlayers()) {
                    if (instancePlayer != player && chunk.getViewers().contains(instancePlayer)) {
                        return;
                    }
                }
            }
            if (playerChunk != chunk) {
                saveAndUnload(instance, chunk, player);
            } else {
                if (shouldUnload(playerChunk)) {
                    saveAndUnload(instance, playerChunk, player);
                    MinecraftServer.getSchedulerManager().scheduleNextProcess(() -> lastChunk.remove(player));
                } else {
                    lastChunk.put(player, playerChunk);
                }
            }
        }
    }

    private static boolean shouldUnload(Chunk chunk) {
        if (!chunk.isLoaded()) return false;
        var viewers = chunk.getViewers();
        return viewers.isEmpty();
    }

    private static void saveAndUnload(Instance instance, Chunk chunk, Player origin) {
        if (!(instance instanceof InstanceContainer container)) return;
        if (instance instanceof DoNotSave) {
            instance.unloadChunk(chunk);
            return;
        }
        var tasks = saveTaskMap.get(instance);
        if (tasks == null) {
            System.out.println("Bad unload");
            instance.unloadChunk(chunk);
            return;
        }
        var loader = container.getChunkLoader();
        if (loader.supportsParallelSaving()) {
            if (!instance.isRegistered()) {
                return;
            }
            if (!chunk.isLoaded()) {
                return;
            }
            var future = new CompletableFuture<Void>();
            tasks.tasks.add(future);
            var pool = ForkJoinPool.commonPool();
            var idx = ChunkUtils.getChunkIndex(chunk);
            if (!tasks.saving.add(idx)) {
                System.out.println("DUPLICATE SAVE " + chunk.getChunkX() + " " + chunk.getChunkZ() + " - " + origin.getUsername());
                tasks.tasks.remove(future);
                return;
            }
            pool.execute(() -> {
                try {
                    synchronized (chunk) {
                        loader.saveChunk(chunk).whenComplete((_, t) -> {
                            if (t != null) {
                                tasks.tasks.remove(future);
                                tasks.saving.remove(idx);
                                future.completeExceptionally(t);
                                return;
                            }
                            MinecraftServer.getSchedulerManager().scheduleNextProcess(() -> {
                                synchronized (chunk) {
                                    if (shouldUnload(chunk)) {
                                        instance.unloadChunk(chunk);
                                    }
                                }
                            });
                            tasks.tasks.remove(future);
                            tasks.saving.remove(idx);
                            future.complete(null);
                        });
                    }
                } catch (Throwable t) {
                    System.out.println("Failed to save chunk: " + t.getClass().getName() + ": " + t.getMessage());
                }
            });
        } else {
            throw new IllegalStateException("Loader not parallel");
        }
    }

    private static class SaveTasks {
        private volatile boolean waiting = false;
        private volatile boolean done = false;
        private Collection<CompletableFuture<Void>> tasks = ConcurrentHashMap.newKeySet();
        private Collection<Long> saving = ConcurrentHashMap.newKeySet();

        public void await() {
            System.out.println("Wait for tasks");
            waiting = true;
            while (!tasks.isEmpty()) {
                for (var task : tasks) {
                    task.join();
                }
            }
            done = true;
        }
    }
}
