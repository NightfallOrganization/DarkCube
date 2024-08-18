/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.user;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface UserFactory {
    @NotNull
    CommonWBUser create(@NotNull UUID uniqueId, @Nullable CommonGame game);
}
