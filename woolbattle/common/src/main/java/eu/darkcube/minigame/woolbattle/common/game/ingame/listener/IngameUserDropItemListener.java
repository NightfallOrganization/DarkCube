/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.item.UserDropItemEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class IngameUserDropItemListener extends ConfiguredListener<UserDropItemEvent> {
    private final CommonIngame ingame;

    public IngameUserDropItemListener(CommonIngame ingame) {
        super(UserDropItemEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(UserDropItemEvent event) {
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
        var location = user.eyeLocation();
        if (location == null) return;
        var world = location.world();
        var removed = user.removeWool(item.amount(), false);
        if (removed > 0) {
            var wool = team.wool();
            var items = world.dropAt(location.x(), location.y(), location.z(), wool, removed);
            for (var itemEntity : items) {
                itemEntity.velocity(location.direction().div(5));
                itemEntity.pickupDelay(20);
                itemEntity.mergeDelay(50);
            }
        }
    }
}
