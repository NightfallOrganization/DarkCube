/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface Projectile extends Entity {
    @Nullable
    WBUser shooter();

    void shooter(@Nullable WBUser shooter);

    boolean frozen();

    void freeze();

    void unfreeze();
}
