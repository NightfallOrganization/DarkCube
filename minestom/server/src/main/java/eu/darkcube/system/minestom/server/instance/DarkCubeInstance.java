/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.utils.chunk.ChunkSupplier;
import net.minestom.server.world.DimensionType;

public class DarkCubeInstance extends Instance {

    private static final AnvilLoader DEFAULT_LOADER = new AnvilLoader("world");
    private final ChunkStorage chunkStorage = new ChunkStorage(this);
    private @NotNull ChunkSupplier chunkSupplier = DynamicChunk::new;
    private @NotNull IChunkLoader chunkLoader = DEFAULT_LOADER;
    private @Nullable Generator generator;
    private boolean autoChunkLoading = false;

    public DarkCubeInstance(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
        super(uniqueId, dimensionType);
    }

    @Override public void setBlock(int x, int y, int z, @NotNull Block block, boolean doBlockUpdates) {

    }

    @Override public boolean placeBlock(BlockHandler.@NotNull Placement placement, boolean doBlockUpdates) {
        return false;
    }

    @Override
    public boolean breakBlock(@NotNull Player player, @NotNull Point blockPosition, @NotNull BlockFace blockFace, boolean doBlockUpdates) {
        return false;
    }

    @Override public @NotNull CompletableFuture<@NotNull Chunk> loadChunk(int chunkX, int chunkZ) {
        return chunkStorage.loadChunk(chunkX, chunkZ);
    }

    @Override public @NotNull CompletableFuture<@Nullable Chunk> loadOptionalChunk(int chunkX, int chunkZ) {
        return chunkStorage.loadChunkOptional(chunkX, chunkZ);
    }

    @Override public void unloadChunk(@NotNull Chunk chunk) {
        chunkStorage.unloadChunk(chunk);
    }

    @Override public @Nullable Chunk getChunk(int chunkX, int chunkZ) {
        return chunkStorage.getChunk(chunkX, chunkZ);
    }

    @Override public @NotNull CompletableFuture<Void> saveInstance() {
        return chunkLoader.saveInstance(this);
    }

    @Override public @NotNull CompletableFuture<Void> saveChunkToStorage(@NotNull Chunk chunk) {
        return chunkLoader.saveChunk(chunk);
    }

    @Override public @NotNull CompletableFuture<Void> saveChunksToStorage() {
        return chunkLoader.saveChunks(getChunks());
    }

    @Override public void setChunkSupplier(@NotNull ChunkSupplier chunkSupplier) {
        this.chunkSupplier = chunkSupplier;
    }

    @Override public @NotNull ChunkSupplier getChunkSupplier() {
        return chunkSupplier;
    }

    @Override public @Nullable Generator generator() {
        return this.generator;
    }

    @Override public void setGenerator(@Nullable Generator generator) {
        this.generator = generator;
    }

    @Override public @NotNull Collection<@NotNull Chunk> getChunks() {
        return chunkStorage.chunks();
    }

    @Override public void enableAutoChunkLoad(boolean enable) {
        this.autoChunkLoading = enable;
    }

    @Override public boolean hasEnabledAutoChunkLoad() {
        return this.autoChunkLoading;
    }

    @Override public boolean isInVoid(@NotNull Point point) {
        return point.y() < getDimensionType().getMinY() - 64;
    }

    public void chunkLoader(@Nullable IChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader == null ? DEFAULT_LOADER : chunkLoader;
    }

    public @NotNull IChunkLoader chunkLoader() {
        return chunkLoader;
    }
}
