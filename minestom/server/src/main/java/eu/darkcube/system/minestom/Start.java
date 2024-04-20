/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom;

import eu.darkcube.system.minestom.command.GameModeCommand;
import eu.darkcube.system.minestom.command.LoadedChunksCommand;
import eu.darkcube.system.minestom.command.StopCommand;
import eu.darkcube.system.minestom.console.ServerConsole;
import eu.darkcube.system.minestom.listener.ChunkUnloader;
import eu.darkcube.system.minestom.util.CloudNetIntegration;
import net.hollowcube.minestom.extensions.ExtensionBootstrap;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.permission.Permission;

public class Start {
    public static void main(String[] args) {
        System.setProperty("org.jline.terminal.dumb", "true");
        // System.setProperty("minestom.extension.enabled", "true");
        System.setProperty("minestom.chunk-view-distance", "32");
        System.setProperty("minestom.entity-view-distance", "32");
        System.setProperty("minestom.experiment.pose-updates", "true");

        try {
            CloudNetIntegration.init();
        } catch (Throwable throwable) {
            // throwable.printStackTrace();
        }
        // BungeeCordProxy.enable();

        var server = ExtensionBootstrap.init();

        // Console must have every permission
        MinecraftServer.getCommandManager().getConsoleSender().addPermission(new Permission("*"));

        MinecraftServer.setBrandName("DarkCube");

        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new LoadedChunksCommand());
        MinecraftServer.getCommandManager().register(new GameModeCommand());

        MinecraftServer.getGlobalEventHandler().addListener(PlayerChunkUnloadEvent.class, ChunkUnloader::playerChunkUnload);
        MinecraftServer.getGlobalEventHandler().addListener(EntityDespawnEvent.class, ChunkUnloader::entityDespawn);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, ChunkUnloader::playerDisconnect);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, ChunkUnloader::playerSpawn);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, event -> {
            var instance = event.getInstance();
            var chunk = instance.getChunkAt(event.getNewPosition());

            if (chunk == null) {
                event.setCancelled(true);
                return;
            }
            var to = chunk.getBlock(event.getNewPosition());
            if (to.registry().collisionShape().intersectBox(event.getNewPosition(), event.getPlayer().getBoundingBox())) {
                event.setCancelled(true);
            }
        });

        // for (int i = 0; i < 5; i++) {
        //     var instance = MinecraftServer.getInstanceManager().createInstanceContainer(DimensionType.OVERWORLD);
        //     var s = i;
        //     instance.setGenerator(unit -> {
        //         unit.modifier().fillHeight(unit.absoluteStart().blockY(), 60 + s, Block.STONE);
        //     });
        //     instance.setChunkSupplier(LightingChunk::new);
        //     instance.setTimeRate(0);
        //     instance.setTime(12000);
        // }
        //
        // MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
        //     event.getPlayer().setRespawnPoint(new Pos(0, 64, 0));
        //     event.getPlayer().setPermissionLevel(4);
        //     event.setSpawningInstance(MinecraftServer.getInstanceManager().getInstances().stream().findFirst().orElseThrow());
        // });
        //
        // var command = new Command("randominstance");
        // command.setDefaultExecutor((sender, context) -> {
        //     var array = MinecraftServer.getInstanceManager().getInstances().toArray(new Instance[0]);
        //     var i = ThreadLocalRandom.current().nextInt(array.length);
        //     var instance = array[i];
        //     sender.asPlayer().setInstance(instance, sender.asPlayer().getRespawnPoint());
        // });
        // MinecraftServer.getCommandManager().register(command);
        // MinecraftServer.getBiomeManager().loadVanillaBiomes();
        // MinecraftServer.setCompressionThreshold(0);

        server.start(System.getProperty("service.bind.host"), Integer.getInteger("service.bind.port"));
        ServerConsole.init();
    }
}
