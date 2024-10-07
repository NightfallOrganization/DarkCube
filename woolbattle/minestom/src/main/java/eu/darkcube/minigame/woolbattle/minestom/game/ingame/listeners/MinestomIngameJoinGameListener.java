/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;

public class MinestomIngameJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final MinestomWoolBattle woolbattle;

    public MinestomIngameJoinGameListener(MinestomWoolBattle woolbattle) {
        super(UserJoinGameEvent.class);
        this.woolbattle = woolbattle;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        var user = (CommonWBUser) event.user();
        var player = woolbattle.player(user);
        var packet = player.getAddPlayerToList();
        for (var wbUser : event.game().users()) {
            var u = (CommonWBUser) wbUser;
            var p = woolbattle.player(u);
            if (u.canSee(user)) {
                p.sendPacketDirect(packet);
            }

            if (u != user) {
                if (user.canSee(u)) {
                    player.sendPacketDirect(p.getAddPlayerToList());
                }
            }
        }
        player.postJoin();
    }
}
