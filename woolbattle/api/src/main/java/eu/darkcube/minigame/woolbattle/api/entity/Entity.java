/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;

public interface Entity {
    @NotNull
    Location location();

    void velocity(@NotNull Vector velocity);

    @NotNull
    MetaDataStorage metadata();

    @NotNull
    EntityType type();

    void remove();
}
