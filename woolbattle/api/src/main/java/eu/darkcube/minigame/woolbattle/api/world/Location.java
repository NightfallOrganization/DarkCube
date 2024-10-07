/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record Location(@NotNull World world, double x, double y, double z, float yaw, float pitch) implements Position.Directed {
    public Location(@NotNull World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public Location(@NotNull World world, @NotNull Position.Directed position) {
        this(world, position.x(), position.y(), position.z(), position.yaw(), position.pitch());
    }

    @Override
    public @NotNull Location clone() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public @NotNull Location aligned() {
        return new Location(world, simple().aligned());
    }

    @Override
    public @NotNull Location add(double x, double y, double z) {
        return new Location(world, this.x + x, this.y + y, this.z + z, yaw, pitch);
    }

    @Override
    public @NotNull Location sub(double x, double y, double z) {
        return new Location(world, this.x - x, this.y - y, this.z - z, yaw, pitch);
    }

    @Override
    public @NotNull Location add(@NotNull Vector vec) {
        return (Location) Directed.super.add(vec);
    }

    @Override
    public @NotNull Location sub(@NotNull Vector vec) {
        return (Location) Directed.super.sub(vec);
    }

    @Override
    public @NotNull Location withDirection(@NotNull Vector dir) {
        return new Location(world, Directed.super.withDirection(dir));
    }

    @Override
    public String toString() {
        return "Location{" + "world=" + world.key() + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
    }
}
