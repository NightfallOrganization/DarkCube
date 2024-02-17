/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.link.cloudnet;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.event.ServiceInfoPropertiesConfigureEvent;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.server.util.DarkCubeServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {

    @EventListener public void handle(ServiceInfoPropertiesConfigureEvent event) {
        event.propertyHolder().writeProperty(DarkCubeServiceProperty.GAME_STATE, DarkCubeServer.gameState()).writeProperty(DarkCubeServiceProperty.PLAYING_PLAYERS, DarkCubeServer.playingPlayers().get()).writeProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS, DarkCubeServer.spectatingPlayers().get()).writeProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS, DarkCubeServer.maxPlayingPlayers().get()).writeProperty(DarkCubeServiceProperty.DISPLAY_NAME, DarkCubeServer.displayName()).writeProperty(DarkCubeServiceProperty.AUTOCONFIGURED, DarkCubeServer.autoConfigure()).writeProperty(DarkCubeServiceProperty.EXTRA, DarkCubeServer.extra());
    }

    @EventHandler public void handle(PlayerJoinEvent event) {
        if (DarkCubeServer.autoConfigure()) {
            DarkCubeServer.playingPlayers().incrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }

    @EventHandler public void handle(PlayerQuitEvent event) {
        if (DarkCubeServer.autoConfigure()) {
            DarkCubeServer.playingPlayers().decrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }
}
