/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.server;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.GameState;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public interface ServerInformation {

    Predicate<ServerInformation> ONLINE_FILTER = new OnlineFilter();
    java.util.Comparator<ServerInformation> COMPARATOR = new Comparator();

    static Predicate<ServerInformation> filterByTask(@NotNull String taskName) {
        return information -> information.taskName().equals(taskName);
    }

    static Predicate<ServerInformation> filterByTask(@NotNull Collection<String> taskNames) {
        return information -> taskNames.contains(information.taskName());
    }

    int onlinePlayers();

    int maxPlayers();

    int spectators();

    UUID uniqueId();

    @Nullable GameState gameState();

    boolean online();

    String taskName();

    @Nullable Component displayName();

    CompletableFuture<State> connectPlayer(UUID uuid);

    record State(boolean success, Throwable cause) {
    }

    class OnlineFilter implements Predicate<ServerInformation> {
        private OnlineFilter() {
        }

        @Override public boolean test(ServerInformation serverInformation) {
            return serverInformation.online();
        }
    }

    class Comparator implements java.util.Comparator<ServerInformation> {
        private Comparator() {
        }

        @Override public int compare(ServerInformation o1, ServerInformation o2) {
            GameState state = o1.gameState();
            GameState otherState = o2.gameState();
            if (state == null) {
                if (otherState != null) return 1;
                return comparePlayers(o1, o2);
            }

            return switch (state) {
                case LOBBY -> {
                    if (otherState != GameState.LOBBY) yield -1;
                    yield comparePlayers(o1, o2);
                }
                case INGAME -> {
                    if (otherState == GameState.LOBBY) yield 1;
                    if (otherState != GameState.INGAME) yield -1;
                    yield comparePlayers(o1, o2);
                }
                default -> {
                    if (otherState != GameState.UNKNOWN) yield 1;
                    yield comparePlayers(o1, o2);
                }
            };
        }

        private int comparePlayers(ServerInformation o1, ServerInformation o2) {
            int c = -Integer.compare(o1.onlinePlayers(), o2.onlinePlayers());
            if (c != 0) return c;
            return Integer.compare(o1.maxPlayers(), o2.maxPlayers());
        }
    }
}
