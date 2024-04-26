/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinestomPlayer extends Player {
    private @Nullable CommonWBUser user;

    public MinestomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public @Nullable CommonWBUser user() {
        return user;
    }

    public void user(@Nullable CommonWBUser user) {
        this.user = user;
    }
}
