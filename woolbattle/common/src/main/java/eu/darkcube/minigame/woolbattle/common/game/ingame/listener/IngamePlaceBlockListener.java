/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.world.block.PlaceBlockEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameBlock;

public class IngamePlaceBlockListener extends ConfiguredListener<PlaceBlockEvent> {
    private final CommonIngame ingame;

    public IngamePlaceBlockListener(CommonIngame ingame) {
        super(PlaceBlockEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(PlaceBlockEvent event) {
        var user = event.user();
        var team = user.team();
        if (team == null || !team.canPlay()) {
            event.cancel();
            return;
        }
        var block = (CommonIngameBlock) event.block();
        var wool = ingame.game().api().materialProvider().woolFrom(event.item());
        if (wool == null) {
            // Not wool, cancel
            event.cancel();
            return;
        }
        if (!block.setWool(team.wool())) event.cancel();
    }
}
