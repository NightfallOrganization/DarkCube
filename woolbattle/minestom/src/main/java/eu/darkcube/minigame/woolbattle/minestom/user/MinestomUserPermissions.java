package eu.darkcube.minigame.woolbattle.minestom.user;

import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

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
        return player.hasPermission(permission);
    }
}