/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface Registry<T> {

    static <T> Registry<T> create() {
        return new Registry<>() {
            private final Map<Key, T> byKey = new HashMap<>();

            @Override
            public @NotNull Collection<Key> keySet() {
                return byKey.keySet();
            }

            @Override
            public @NotNull T get(@NotNull Key key) {
                var n = getNullable(key);
                if (n == null) throw new NullPointerException("Entry " + key + " not found");
                return n;
            }

            @Override
            public @Nullable T getNullable(@NotNull Key key) {
                return byKey.get(key);
            }

            @Override
            public void register(@NotNull Key key, @NotNull T value) {
                byKey.put(key, value);
            }
        };
    }

    @NotNull
    Collection<Key> keySet();

    @NotNull
    T get(@NotNull Key key);

    @NotNull
    default Optional<T> getOptional(@NotNull Key key) {
        return Optional.ofNullable(getNullable(key));
    }

    @Nullable
    T getNullable(@NotNull Key key);

    void register(@NotNull Key key, @NotNull T value);
}
