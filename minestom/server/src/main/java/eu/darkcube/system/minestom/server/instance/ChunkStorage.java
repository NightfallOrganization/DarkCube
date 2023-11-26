/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import static net.minestom.server.utils.chunk.ChunkUtils.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.server.util.WeakLoadingCache;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.utils.async.AsyncUtils;

public class ChunkStorage {

    private final DarkCubeInstance instance;
    private final ConcurrentHashMap<Long, Chunk> chunks = new ConcurrentHashMap<>();
    private final WeakLoadingCache<Long, Chunk> cache;

    public ChunkStorage(DarkCubeInstance instance) {
        this.instance = instance;

        this.cache = new WeakLoadingCache<>(key -> null);
    }

    public ConcurrentHashMap<Long, Chunk> chunks() {
        return chunks;
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
        if (chunk != null) return CompletableFuture.completedFuture(chunk);
        if (optional && !instance.hasEnabledAutoChunkLoad()) return AsyncUtils.empty();
        return createChunk(chunkX, chunkZ);
    }

    private @NotNull CompletableFuture<@NotNull Chunk> createChunk(int chunkX, int chunkZ) {
        return null;
    }

    public @NotNull CompletableFuture<@Nullable Chunk> loadChunkOptional(int chunkX, int chunkZ) {
        return loadChunk(chunkX, chunkZ, true);
    }

    public void unloadChunk(@NotNull Chunk chunk) {
        chunks.remove(getChunkIndex(chunk.getChunkX(), chunk.getChunkZ()), chunk);
    }
}
