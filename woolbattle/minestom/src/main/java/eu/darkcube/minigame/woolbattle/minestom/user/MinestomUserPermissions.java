/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.luckperms.api.LuckPermsProvider;
import net.minestom.server.entity.Player;

public class MinestomUserPermissions implements UserPermissions {
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull CommonWBUser user;

    public MinestomUserPermissions(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonWBUser user) {
        this.woolbattle = woolbattle;
        this.user = user;
    }

    @Override
    public boolean hasPermission(String permission) {
        var player = woolbattle.player(user);
        return LuckPermsProvider.get().getPlayerAdapter(Player.class).getPermissionData(player).checkPermission(permission).asBoolean();
    }
}
