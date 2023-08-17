/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.GameState;

import java.util.concurrent.atomic.AtomicInteger;

public final class DarkCubeBukkit {

    private static final AtomicInteger playingPlayers = new AtomicInteger();
    private static final AtomicInteger spectatingPlayers = new AtomicInteger();
    private static final AtomicInteger maxPlayingPlayers = new AtomicInteger(-1);
    private static volatile GameState gameState = GameState.UNKNOWN;
    private static volatile String displayName = InjectionLayer.boot().instance(ComponentInfo.class).componentName();
    private static volatile boolean autoConfigure = true;

    public static @NotNull GameState gameState() {
        return gameState;
    }

    public static void autoConfigure(boolean autoConfigure) {
        DarkCubeBukkit.autoConfigure = autoConfigure;
    }

    public static boolean autoConfigure() {
        return autoConfigure;
    }

    public static void gameState(@NotNull GameState gameState) {
        DarkCubeBukkit.gameState = gameState;
    }

    public static @NotNull AtomicInteger maxPlayingPlayers() {
        return maxPlayingPlayers;
    }

    public static void displayName(@NotNull String displayName) {
        DarkCubeBukkit.displayName = displayName;
    }

    public static @NotNull String displayName() {
        return displayName;
    }

    public static @NotNull AtomicInteger playingPlayers() {
        return playingPlayers;
    }

    public static @NotNull AtomicInteger spectatingPlayers() {
        return spectatingPlayers;
    }
}
