/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.server.chunk.ChunkManager;
import eu.darkcube.system.minestom.server.player.DarkCubePlayer;
import eu.darkcube.system.minestom.server.util.BinaryOperations;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.server.play.UnloadChunkPacket;
import net.minestom.server.world.DimensionType;

import java.util.Collection;
import java.util.UUID;

public class DarkCubeInstance extends InstanceContainer {
    private final ChunkManager<Chunk> chunkManager;

    public DarkCubeInstance(UUID uniqueId, DimensionType dimensionType, int priorityCount) {
        this(uniqueId, dimensionType, null, priorityCount);
    }

    public DarkCubeInstance(UUID uniqueId, DimensionType dimensionType, IChunkLoader loader, int priorityCount) {
        super(uniqueId, dimensionType, loader);
        ChunkManager.Callbacks<Chunk> callbacks = new ChunkManager.Callbacks<>() {
            @Override public void loaded(int chunkX, int chunkY) {
            }

            @Override public void unloaded(int chunkX, int chunkY) {
                var chunk = getChunk(chunkX, chunkY);
                if (chunk == null) return;
                unloadChunk(chunk);
            }

            @Override public void generated(int chunkX, int chunkY, Chunk chunk) {
                chunk.sendChunk();
            }
        };
        Long2ObjectFunction<Chunk> generator = key -> loadChunk(BinaryOperations.x(key), BinaryOperations.y(key)).join();
        this.chunkManager = new ChunkManager<>(callbacks, priorityCount, generator);
    }

    @Override public synchronized InstanceContainer copy() {
        throw new UnsupportedOperationException();
    }

    @Override public Chunk getChunk(int chunkX, int chunkZ) {
        return super.getChunk(chunkX, chunkZ);
    }

    @Override public synchronized void unloadChunk(@NotNull Chunk chunk) {
        super.unloadChunk(chunk);
    }

    @Override public @NotNull Collection<@NotNull Chunk> getChunks() {
        return super.getChunks();
    }

    public ChunkManager<Chunk> chunkManager() {
        return chunkManager;
    }
}
