/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserFactory;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.UserAPI;

public class MinestomUserFactory implements UserFactory {
    private final @NotNull MinestomWoolBattle woolbattle;

    public MinestomUserFactory(@NotNull MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull CommonWBUser create(@NotNull UUID uniqueId, @Nullable CommonGame game) {
        var user = UserAPI.instance().user(uniqueId);
        return new MinestomUser(woolbattle, user, game);
    }
}
