/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattle;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.instance.InstanceChunkUnloadEvent;
import net.minestom.server.event.player.PlayerChunkLoadEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class WoolBattleMinestom {

    public void start() {
        System.setProperty("minestom.chunk-view-distance", "32");
        System.setProperty("minestom.entity-view-distance", "32");
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setBrandName("WoolBattle");
        MinecraftServer.setTerminalPrompt(null);

        Command command = new Command("stop", "exit");
        command.setDefaultExecutor((sender, context) -> {
            MinecraftServer.stopCleanly();

            System.exit(0);
        });
        MinecraftServer.getCommandManager().register(command);

//        Biome customBiome = Biome
//                .builder()
//                .category(Biome.Category.FOREST)
//                .name(NamespaceID.from("woolbattle", "woolbattle"))
//                .effects(BiomeEffects.builder().biomeParticle(new BiomeParticle(1F, new BiomeParticle.DustOption(1, 0, 1, 1)))
//                        .fogColor(new Color(255, 0, 0).asRGB())
//                        .build())
//                .build();
//
//        MinecraftServer.getBiomeManager().addBiome(customBiome);
//
        DimensionType dimensionType = DimensionType
                .builder(NamespaceID.from("woolbattle", "woolbattle"))
                .ambientLight(2.0F)
                .fixedTime(6000L)
//                .skylightEnabled(false)
//                .ceilingEnabled(true)
                .build();
        MinecraftServer.getDimensionTypeManager().addDimension(dimensionType);

        Biome customBiome = Biome.PLAINS;
//        DimensionType dimensionType = DimensionType.OVERWORLD;

        InstanceContainer instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(dimensionType);
//        instanceContainer.enableAutoChunkLoad(false);

        instanceContainer.setGenerator(unit -> {
            LockSupport.parkNanos(100);
            unit.modifier().fillBiome(customBiome);
            unit.modifier().fillHeight(0, 100, Block.STONE);
        });
        instanceContainer.setChunkLoader(new IChunkLoader() {
            @Override public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
                return CompletableFuture.completedFuture(null);
            }

            @Override public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
                return CompletableFuture.completedFuture(null);
            }

            @Override public boolean supportsParallelLoading() {
                return true;
            }

            @Override public boolean supportsParallelSaving() {
                return true;
            }
        });

//        Chunk chunk = instanceContainer.loadChunk(0, 0).join();
//
//        instanceContainer.setChunkLoader(new IChunkLoader() {
//            @Override public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
//                return CompletableFuture.completedFuture(chunk.copy(instance, chunkX, chunkZ));
//            }
//
//            @Override public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
//                return CompletableFuture.completedFuture(null);
//            }
//        });

//        InstanceContainer instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer();

        AtomicInteger loaded = new AtomicInteger();
        AtomicInteger playerloaded = new AtomicInteger();

        Instance instance = MinecraftServer.getInstanceManager().createSharedInstance(instanceContainer);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerChunkUnloadEvent.class, event -> {
            playerloaded.decrementAndGet();
            Chunk chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
            if (chunk != null) {
                for (Player player : instance.getPlayers()) {
                    if (chunk.equals(player.getChunk())) {
                        return;
                    }
                }
                instance.unloadChunk(chunk);
            } else {
                System.out.println("Chunk was null");
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerChunkLoadEvent.class, event -> {
            playerloaded.incrementAndGet();
        });
        MinecraftServer.getGlobalEventHandler().addListener(InstanceChunkLoadEvent.class, event -> {
            loaded.incrementAndGet();
        });
        MinecraftServer.getGlobalEventHandler().addListener(InstanceChunkUnloadEvent.class, event -> {
            loaded.decrementAndGet();
        });
        Command loadedCommand = new Command("loadedchunks");
        loadedCommand.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(loaded.get() + " " + playerloaded.get());
        });
        MinecraftServer.getCommandManager().register(loadedCommand);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, event -> {
            if (!instance.isChunkLoaded(event.getNewPosition())) {
                instance.loadChunk(event.getNewPosition()).thenAccept(c -> {
                    System.out.println("Loaded chunk");
                }).join();
                return;
            }
            Chunk chunk = instance.getChunkAt(event.getNewPosition());
            if (chunk == null) {
                event.setCancelled(true);
                return;
            }
            Block to = chunk.getBlock(event.getNewPosition());

            if (to.registry().collisionShape().intersectBox(event.getNewPosition(), event.getPlayer().getBoundingBox())) {
                event.setCancelled(true);
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(instance);
            event.getPlayer().setRespawnPoint(new Pos(0, 100, 0));
            event.getPlayer().setGameMode(GameMode.CREATIVE);
            event.getPlayer().setPermissionLevel(4);
            event.getPlayer().setFlyingSpeed(10);
        });

        server.start(System.getProperty("service.bind.host"), Integer.getInteger("service.bind.port"));
    }

}
