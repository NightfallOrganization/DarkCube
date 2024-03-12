/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.Objects;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class Key {

    private final @NotNull String plugin;
    private final @NotNull String key;

    public Key(@NotNull Named named, @NotNull String key) {
        this.plugin = named.getName();
        this.key = key;
    }

    public Key(@NotNull String plugin, @NotNull String key) {
        this.plugin = plugin;
        this.key = key;
    }

    public static Key fromString(@NotNull String string) {
        var a = string.split(":", 2);
        return new Key(a[0], a[1]);
    }

    public @NotNull String key() {
        return this.key;
    }

    public @NotNull String plugin() {
        return this.plugin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, plugin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        var other = (Key) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.plugin, other.plugin);
    }

    @Override
    public String toString() {
        return plugin + ":" + key;
    }

    public interface Named {
        String getName();
    }
}
