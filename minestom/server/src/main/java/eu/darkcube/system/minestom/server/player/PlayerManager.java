/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.player;

import eu.darkcube.system.minestom.server.instance.DarkCubeInstance;
import eu.darkcube.system.minestom.server.util.PriorityCalculator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.player.PlayerSettingsChangeEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class PlayerManager {

    private final EventNode<Event> node = EventNode.all("player_manager");

    public PlayerManager(PriorityCalculator priorityCalculator) {
        node.addListener(PlayerSpawnEvent.class, event -> {
            var player = (DarkCubePlayer) event.getPlayer();
        });
        node.addListener(PlayerSettingsChangeEvent.class, event -> {
            var player = (DarkCubePlayer) event.getPlayer();
            player.playerTicketManager().resize(actualViewDistance(player));
        });
        node.addListener(AddEntityToInstanceEvent.class, event -> {
            var instance = (DarkCubeInstance) event.getInstance();
            var entity = event.getEntity();
            if (!(entity instanceof DarkCubePlayer player)) return;
            player.getAcquirable().sync(e -> {
                var position = player.getPosition();
                var radius = actualViewDistance(player);
                var ticketManager = new PlayerTicketManager(instance.chunkManager(), priorityCalculator, radius, player);
                ticketManager.move(position.chunkX(), position.chunkZ());
                var chunkManager = ticketManager.chunkManager();
                player.playerTicketManager(ticketManager);

                // We make sure the chunk where the player spawns is loaded and generated. The PlayerTicketManager will take care that the chunk remains loaded after we release our temporary ticket.
                var requireResult = chunkManager.require(position.chunkX(), position.chunkZ(), 0);
                requireResult.future().join();
                chunkManager.release(position.chunkX(), position.chunkZ(), requireResult.ticket());
            });
        });
    }

    private int actualViewDistance(DarkCubePlayer player) {
        return Math.min(player.getSettings().getViewDistance(), MinecraftServer.getChunkViewDistance());
//        return MinecraftServer.getChunkViewDistance();
    }

    public void register(EventNode<? super Event> root) {
        root.addChild(node);
        MinecraftServer.getConnectionManager().setPlayerProvider(DarkCubePlayer::new);
    }
}
