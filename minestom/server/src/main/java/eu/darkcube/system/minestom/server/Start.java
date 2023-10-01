/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.server.chunk.NormalPriorityCalculator;
import eu.darkcube.system.minestom.server.instance.DarkCubeInstance;
import eu.darkcube.system.minestom.server.player.PlayerManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
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
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.async.AsyncUtils;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import net.minestom.server.world.biomes.BiomeParticle;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Start {
    public static void main(String[] args) {
        System.setProperty("minestom.chunk-view-distance", "32");
        System.setProperty("minestom.entity-view-distance", "32");
        var server = MinecraftServer.init();
        var priorityCount = MinecraftServer.getChunkViewDistance() + 1;
        System.out.println("Priority count: " + priorityCount);
        var priorityCalculator = new NormalPriorityCalculator(priorityCount);
        var playerManager = new PlayerManager(priorityCalculator);
        playerManager.register(MinecraftServer.getGlobalEventHandler());

        MinecraftServer.setBrandName("DarkCube");
        MinecraftServer.setTerminalPrompt(null);

        var command = new Command("stop", "exit");
        command.setDefaultExecutor((sender, context) -> {
            MinecraftServer.stopCleanly();

            System.exit(0);
        });
        MinecraftServer.getCommandManager().register(command);

        Biome customBiome = Biome
                .builder()
                .category(Biome.Category.FOREST)
                .name(NamespaceID.from("darkcube", "testbiome"))
                .effects(BiomeEffects
                        .builder()
                        .biomeParticle(new BiomeParticle(1F, new BiomeParticle.DustOption(1, 0, 1, 1)))
                        .fogColor(new Color(255, 0, 0).asRGB())
                        .build())
                .build();

        MinecraftServer.getBiomeManager().addBiome(customBiome);
//
        var dimensionType = DimensionType.builder(NamespaceID.from("darkcube", "fullbright")).ambientLight(2.0F).fixedTime(6000L)
//                .skylightEnabled(false)
//                .ceilingEnabled(true)
                .build();
        MinecraftServer.getDimensionTypeManager().addDimension(dimensionType);

//        var customBiome = Biome.PLAINS;

        var instance = new DarkCubeInstance(UUID.randomUUID(), dimensionType, priorityCount);
        MinecraftServer.getInstanceManager().registerInstance(instance);
        instance.enableAutoChunkLoad(false);
//        instanceContainer.enableAutoChunkLoad(false);

        instance.setGenerator(unit -> {
//            LockSupport.parkNanos(10_000_000);
            unit.modifier().fillBiome(customBiome);
            unit.modifier().fillHeight(0, 100, Block.STONE);
        });
        instance.setChunkLoader(new IChunkLoader() {
            @Override public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
                return AsyncUtils.empty();
            }

            @Override public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
                return AsyncUtils.empty();
            }

            @Override public boolean supportsParallelLoading() {
                return true;
            }

            @Override public boolean supportsParallelSaving() {
                return true;
            }
        });

        var loaded = new AtomicInteger();
        var playerloaded = new AtomicInteger();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerChunkUnloadEvent.class, event -> {
            playerloaded.decrementAndGet();
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
            sender.sendMessage(loaded.get() + " " + playerloaded.get() + " " + instance.getChunks().size());
        });
        MinecraftServer.getCommandManager().register(loadedCommand);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, event -> {
            Chunk chunk = instance.getChunkAt(event.getNewPosition());
            if (chunk == null) {
                System.out.println("Cancelled move");
                event.setCancelled(true);
                return;
            }
            Block to = chunk.getBlock(event.getNewPosition());

            if (to.registry().collisionShape().intersectBox(event.getNewPosition(), event.getPlayer().getBoundingBox())) {
                event.setCancelled(true);
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
            var player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 100, 0));
            player.setGameMode(GameMode.CREATIVE);
            player.setPermissionLevel(4);
            player.setFlyingSpeed(2);
        });

        server.start(System.getProperty("service.bind.host"), Integer.getInteger("service.bind.port"));
    }
}
