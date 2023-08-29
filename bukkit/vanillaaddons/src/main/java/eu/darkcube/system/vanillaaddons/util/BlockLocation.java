/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

import java.util.Objects;

public record BlockLocation(NamespacedKey world, int x, int y, int z) {

    @NotNull public Block block() {
        return Objects.requireNonNull(Bukkit.getWorld(world)).getBlockAt(x, y, z);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockLocation that = (BlockLocation) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world);
    }

    @Override public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
