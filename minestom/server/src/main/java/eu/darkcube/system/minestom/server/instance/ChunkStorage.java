/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import static net.minestom.server.utils.chunk.ChunkUtils.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.instance.InstanceChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.GeneratorImpl;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.network.packet.server.play.UnloadChunkPacket;
import net.minestom.server.utils.async.AsyncUtils;
import net.minestom.server.utils.chunk.ChunkUtils;

public class ChunkStorage {

    private final DarkCubeInstance instance;
    private final ConcurrentHashMap<Long, Chunk> chunks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CompletableFuture<Chunk>> loadingChunks = new ConcurrentHashMap<>();
    private final ExecutorService loaderService = Executors.newWorkStealingPool();

    public ChunkStorage(DarkCubeInstance instance) {
        this.instance = instance;
    }

    public Collection<Chunk> chunks() {
        return chunks.values();
    }

    public @Nullable Chunk getChunk(int chunkX, int chunkZ) {
        return chunks.get(getChunkIndex(chunkX, chunkZ));
    }

    public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(int chunkX, int chunkZ) {
        return loadChunk(chunkX, chunkZ, false);
    }

    private @NotNull CompletableFuture<Chunk> loadChunk(int chunkX, int chunkZ, boolean optional) {
        var key = getChunkIndex(chunkX, chunkZ);
        var chunk = chunks.get(key);
        // check if the chunk already exists
        if (chunk != null) return CompletableFuture.completedFuture(chunk);
        // check if we should even generate chunks
        if (optional && !instance.hasEnabledAutoChunkLoad()) return AsyncUtils.empty();
        var future = new CompletableFuture<Chunk>();
        var prev = loadingChunks.putIfAbsent(key, future);
        // if someone else is already loading this chunk, we just wait for them to finish
        if (prev != null) return prev;
        chunk = chunks.get(key);
        // someone might have finished chunk loading right now, so the putIfAbsent didn't catch the future
        if (chunk != null) {
            loadingChunks.remove(key, future);
            future.complete(chunk);
            return future;
        }
        var entry = createChunk(getChunkCoordX(key), getChunkCoordZ(key), future);
        return entry.future();
    }

    private @NotNull Entry createChunk(int chunkX, int chunkZ, CompletableFuture<Chunk> future) {
        var key = getChunkIndex(chunkX, chunkZ);
        var chunk = instance.getChunkSupplier().createChunk(instance, chunkX, chunkZ);
        var entry = new Entry(chunk, future);
        var loader = instance.chunkLoader();
        if (loader.supportsParallelLoading()) {
            loaderService.submit(() -> retrieve(chunkX, chunkZ, key, entry, loader));
        } else {
            retrieve(chunkX, chunkZ, key, entry, loader);
        }
        return entry;
    }

    private void retrieve(int chunkX, int chunkZ, long chunkKey, Entry entry, IChunkLoader loader) {
        loader.loadChunk(instance, chunkX, chunkZ).thenCompose(chunk -> {
            if (chunk != null) return CompletableFuture.completedFuture(chunk);
            return generateChunk(chunkX, chunkZ, entry);
        }).thenAccept(chunk -> {
            cacheChunk(chunk);
            EventDispatcher.call(new InstanceChunkLoadEvent(instance, chunk));
            var future = loadingChunks.remove(chunkKey);
            if (future != entry.future()) throw new AssertionError("Bad future modification");
            future.complete(chunk);
        }).exceptionally(throwable -> {
            MinecraftServer.getExceptionManager().handleException(throwable);
            return null;
        });
    }

    private CompletableFuture<Chunk> generateChunk(int chunkX, int chunkZ, Entry entry) {
        var generator = instance.generator();
        var chunk = entry.chunk();
        if (generator != null && chunk.shouldGenerate()) {
            var future = new CompletableFuture<Chunk>();
            loaderService.submit(() -> {

            });
            return future;
        }
        processFork(chunk, Set.of());
        return CompletableFuture.completedFuture(chunk);
    }

    private void processFork(Chunk chunk, Collection<GeneratorImpl.SectionModifierImpl> modifiers) {
        for (GeneratorImpl.SectionModifierImpl modifier : modifiers) {
            var section = chunk.getSectionAt(modifier.start().blockY());
            var currentBlocks = section.blockPalette();
            // -1 is necessary because forked units handle explicit changes by changing AIR 0 to 1
            modifier.blockPalette().getAllPresent((x, y, z, value) -> currentBlocks.set(x, y, z, value - 1));
            applyGenerationData(chunk, modifier);
        }
    }

    private void applyGenerationData(Chunk chunk, GeneratorImpl.SectionModifierImpl modifier) {
        var cache = modifier.cache();
        if (cache.isEmpty()) return;
        final int height = modifier.start().blockY();
        Int2ObjectMaps.fastForEach(cache, entry -> {
            final int index = entry.getIntKey();
            final Block block = entry.getValue();
            final int x = ChunkUtils.blockIndexToChunkPositionX(index);
            final int y = ChunkUtils.blockIndexToChunkPositionY(index) + height;
            final int z = ChunkUtils.blockIndexToChunkPositionZ(index);
            chunk.setBlock(x, y, z, block);
        });
    }

    private void cacheChunk(@NotNull Chunk chunk) {
        this.chunks.put(getChunkIndex(chunk), chunk);
        chunk.setLoaded(true);
        chunk.onLoad();
        var dispatcher = MinecraftServer.process().dispatcher();
        dispatcher.createPartition(chunk);
    }

    public @NotNull CompletableFuture<@Nullable Chunk> loadChunkOptional(int chunkX, int chunkZ) {
        return loadChunk(chunkX, chunkZ, true);
    }

    public void unloadChunk(@NotNull Chunk chunk) {
        if (!isLoaded(chunk)) return;
        var chunkX = chunk.getChunkX();
        var chunkZ = chunk.getChunkZ();
        var key = getChunkIndex(chunkX, chunkZ);
        var entry = chunks.get(key);
        if (entry == null) return;
        chunk.sendPacketToViewers(new UnloadChunkPacket(chunkX, chunkZ));
        EventDispatcher.call(new InstanceChunkUnloadEvent(instance, chunk));
        // Remove all entities in chunk
        instance.getEntityTracker().chunkEntities(chunkX, chunkZ, EntityTracker.Target.ENTITIES).forEach(Entity::remove);
        instance.chunkLoader().unloadChunk(chunk);
        chunk.setLoaded(false);
        if (chunks.remove(key) != chunk) throw new AssertionError("Bad chunk management");
        var dispatcher = MinecraftServer.process().dispatcher();
        dispatcher.deletePartition(chunk);
    }

    private record Entry(Chunk chunk, CompletableFuture<Chunk> future) {
    }
}
