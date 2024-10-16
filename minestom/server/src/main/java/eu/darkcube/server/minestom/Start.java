/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.server.minestom;

import java.nio.file.Path;

import eu.darkcube.server.minestom.console.ServerConsole;
import eu.darkcube.server.minestom.listener.ChunkUnloader;
import eu.darkcube.system.minestom.command.GameModeCommand;
import eu.darkcube.system.minestom.command.LoadedChunksCommand;
import eu.darkcube.system.minestom.command.PermissionProvider;
import eu.darkcube.system.minestom.command.StopCommand;
import eu.darkcube.system.server.cloudnet.CloudNetIntegration;
import eu.darkcube.system.server.version.ServerVersion;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.hollowcube.minestom.extensions.ExtensionBootstrap;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.instance.InstanceRegisterEvent;
import net.minestom.server.event.instance.InstanceUnregisterEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Start {
    public static void main(String[] args) {
        System.setProperty("org.jline.terminal.dumb", "true");
        // System.setProperty("minestom.extension.enabled", "true");
        System.setProperty("minestom.chunk-view-distance", "4");
        System.setProperty("minestom.entity-view-distance", "4");
        System.setProperty("minestom.experiment.pose-updates", "true");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LoggerFactory.getLogger("DarkCube").info("Starting Minestom Server...");

        try {
            CloudNetIntegration.init();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // BungeeCordProxy.enable();

        var server = ExtensionBootstrap.init();

        ServerVersion.version().provider().register(PermissionProvider.class, new MinestomPermissionProvider());
        var luckperms = LuckPermsMinestom.builder(Path.of("LuckPerms")).commandRegistry(CommandRegistry.minestom()).logger(LoggerFactory.getLogger("LuckPerms")).configurationAdapter(plugin -> new YamlConfigurationAdapter(plugin, plugin.resolveConfig("luckperms/config.yml"))).enable();

        MinecraftServer.setBrandName("DarkCube");

        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new LoadedChunksCommand());
        MinecraftServer.getCommandManager().register(new GameModeCommand());

        MinecraftServer.getGlobalEventHandler().addListener(PlayerChunkUnloadEvent.class, ChunkUnloader::playerChunkUnload);
        MinecraftServer.getGlobalEventHandler().addListener(EntityDespawnEvent.class, ChunkUnloader::entityDespawn);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, ChunkUnloader::playerDisconnect);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, ChunkUnloader::playerSpawn);
        MinecraftServer.getGlobalEventHandler().addListener(InstanceRegisterEvent.class, ChunkUnloader::instanceRegister);
        MinecraftServer.getGlobalEventHandler().addListener(InstanceUnregisterEvent.class, ChunkUnloader::instanceUnregister);
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

        server.start(System.getProperty("service.bind.host"), Integer.getInteger("service.bind.port"));
        ServerConsole.init();
    }
}
