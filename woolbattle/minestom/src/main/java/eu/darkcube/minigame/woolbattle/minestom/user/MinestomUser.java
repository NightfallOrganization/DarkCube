/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.DefaultMinestomEntity;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import net.minestom.server.thread.Acquired;

public class MinestomUser extends CommonWBUser implements DefaultMinestomEntity {
    private final MinestomWoolBattle woolbattle;

    public MinestomUser(@NotNull MinestomWoolBattle woolbattle, @NotNull User user, @Nullable CommonGame game) {
        super(woolbattle.api(), user, game);
        this.woolbattle = woolbattle;
    }

    @Override
    public Acquired<? extends MinestomPlayer> lock() {
        var player = woolbattle.player(this);
        return player.acquirable().lock();
    }

    @Override
    public @NotNull MinestomWoolBattle woolbattle() {
        return woolbattle;
    }

    @Override
    public void kick() {
        var lock = lock();
        lock.get().kick("You were kicked from WoolBattle");
        lock.unlock();
    }
}
