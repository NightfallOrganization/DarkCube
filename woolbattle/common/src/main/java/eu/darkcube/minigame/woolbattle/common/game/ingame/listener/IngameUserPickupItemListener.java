/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.BLOCK;
import static eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.sound;

import eu.darkcube.minigame.woolbattle.api.event.item.UserPickupItemEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class IngameUserPickupItemListener extends ConfiguredListener<UserPickupItemEvent> {
    private final CommonIngame ingame;

    public IngameUserPickupItemListener(CommonIngame ingame) {
        super(UserPickupItemEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(UserPickupItemEvent event) {
        var item = event.item();
        var material = item.material();
        var isWool = ingame.game().api().materialProvider().isWool(material);
        if (!isWool) {
            event.cancel();
            return;
        }
        var user = (CommonWBUser) event.user();
        var team = user.team();
        if (team == null) {
            return;
        }
        if (!team.canPlay()) {
            event.cancel();
            return;
        }
        var tryadd = item.amount();
        var added = user.addWool(tryadd, false);
        if (added != 0) {
            user.playSound(sound(key("entity.item.pickup"), BLOCK, 1, 1));
            var entity = event.entity();
            var missed = tryadd - added;
            if (missed > 0) {
                item.amount(missed);
                entity.sendPickupPacket(user, added);
                entity.item(item);
                entity.pickupDelay(4);
            } else {
                entity.sendPickupPacket(user, tryadd);
                entity.remove();
            }
        }
        event.cancel();
    }
}
