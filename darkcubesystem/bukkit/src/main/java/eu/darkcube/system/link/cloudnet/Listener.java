/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link.cloudnet;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.event.ServiceInfoPropertiesConfigureEvent;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.DarkCubeServiceProperty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {

    @EventListener public void handle(ServiceInfoPropertiesConfigureEvent event) {
        event
                .propertyHolder()
                .writeProperty(DarkCubeServiceProperty.GAME_STATE, DarkCubeBukkit.gameState())
                .writeProperty(DarkCubeServiceProperty.PLAYING_PLAYERS, DarkCubeBukkit.playingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS, DarkCubeBukkit.spectatingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS, DarkCubeBukkit.maxPlayingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.DISPLAY_NAME, DarkCubeBukkit.displayName())
                .writeProperty(DarkCubeServiceProperty.AUTOCONFIGURED, DarkCubeBukkit.autoConfigure())
                .writeProperty(DarkCubeServiceProperty.EXTRA, DarkCubeBukkit.extra());
    }

    @EventHandler public void handle(PlayerJoinEvent event) {
        if (DarkCubeBukkit.autoConfigure()) {
            DarkCubeBukkit.playingPlayers().incrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }

    @EventHandler public void handle(PlayerQuitEvent event) {
        if (DarkCubeBukkit.autoConfigure()) {
            DarkCubeBukkit.playingPlayers().decrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }

}
