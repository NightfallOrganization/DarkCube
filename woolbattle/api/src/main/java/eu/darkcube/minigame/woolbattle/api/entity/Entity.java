/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.util.data.MetaDataStorage;

public interface Entity {
    @UnknownNullability
    Location location();

    @UnknownNullability
    Location eyeLocation();

    double eyeHeight();

    void velocity(@NotNull Vector velocity);

    @UnknownNullability
    Vector velocity();

    @NotNull
    MetaDataStorage metadata();

    @NotNull
    EntityType<?> type();

    boolean isAlive();

    void remove();

    default double distanceTo(Position pos) {
        return location().distanceTo(pos);
    }

    default double distanceToSqr(Position pos) {
        return location().distanceToSqr(pos);
    }

    @NotNull
    Component getName();

    @NotNull
    BoundingBox boundingBox();

    @NotNull
    String toString();
}
