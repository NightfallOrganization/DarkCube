/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners;

import eu.darkcube.minigame.woolbattle.api.event.game.UserQuitGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;

public class MinestomIngameQuitGameListener extends ConfiguredListener<UserQuitGameEvent> {
    private final MinestomWoolBattle woolbattle;

    public MinestomIngameQuitGameListener(MinestomWoolBattle woolbattle) {
        super(UserQuitGameEvent.class);
        this.woolbattle = woolbattle;
    }

    @Override
    public void accept(UserQuitGameEvent event) {
        var user = (CommonWBUser) event.user();
        var player = woolbattle.player(user);
        var packet = player.getRemovePlayerToList();
        for (var wbUser : event.game().users()) {
            var u = (CommonWBUser) wbUser;
            var p = woolbattle.player(u);
            if (u.canSee(user)) {
                p.sendPacketDirect(packet);
            }

            if (u != user) {
                if (user.canSee(u)) {
                    player.sendPacketDirect(p.getRemovePlayerToList());
                }
            }
        }
    }
}
