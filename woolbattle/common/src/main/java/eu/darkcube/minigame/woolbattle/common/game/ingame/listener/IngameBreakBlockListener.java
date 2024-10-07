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

import eu.darkcube.minigame.woolbattle.api.event.world.block.BreakBlockEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameBlock;

public class IngameBreakBlockListener extends ConfiguredListener<BreakBlockEvent> {
    private final CommonIngame ingame;

    public IngameBreakBlockListener(CommonIngame ingame) {
        super(BreakBlockEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(BreakBlockEvent event) {
        var user = event.user();
        var team = user.team();
        if (team == null || !team.canPlay()) {
            event.cancel();
            return;
        }
        var block = (CommonIngameBlock) event.block();
        var wool = ingame.game().api().materialProvider().woolFrom(block);
        if (wool == null) {
            // Not wool, cancel
            event.cancel();
            return;
        }
        block.changeBlockDamage(block.maxBlockDamage());
        var tryAdd = user.woolBreakAmount();
        var added = user.addWool(tryAdd, false);
        if (added != 0) {
            user.playSound(sound(key("entity.item.pickup"), BLOCK, 1, 1));
        }
    }
}
