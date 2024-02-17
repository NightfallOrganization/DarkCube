/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.wrapper.event.ServiceInfoPropertiesConfigureEvent;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.server.util.DarkCubeServer;

public class ServiceListener {
    @EventListener public void handle(ServiceInfoPropertiesConfigureEvent event) {
        event
                .propertyHolder()
                .writeProperty(DarkCubeServiceProperty.GAME_STATE, DarkCubeServer.gameState())
                .writeProperty(DarkCubeServiceProperty.PLAYING_PLAYERS, DarkCubeServer.playingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS, DarkCubeServer.spectatingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS, DarkCubeServer.maxPlayingPlayers().get())
                .writeProperty(DarkCubeServiceProperty.DISPLAY_NAME, DarkCubeServer.displayName())
                .writeProperty(DarkCubeServiceProperty.AUTOCONFIGURED, DarkCubeServer.autoConfigure())
                .writeProperty(DarkCubeServiceProperty.EXTRA, DarkCubeServer.extra());
    }
}
