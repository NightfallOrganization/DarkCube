/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class DoubleJumpPerk extends Perk {
    public static final PerkName DOUBLE_JUMP = new PerkName("DOUBLE_JUMP");

    public DoubleJumpPerk(CommonGame game) {
        super(ActivationType.DOUBLE_JUMP, DOUBLE_JUMP, new Cooldown(Cooldown.Unit.TICKS, 63), 5, Items.PERK_DOUBLE_JUMP, DoubleJumpUserPerk::new);
    }

    public static class DoubleJumpUserPerk extends CooldownUserPerk {
        public DoubleJumpUserPerk(WBUser owner, Perk perk, int id, int perkSlot, Game game) {
            super(owner, perk, id, perkSlot, game, Items.PERK_DOUBLE_JUMP_COOLDOWN);
        }

        @Override
        public void cooldown(int cooldown) {
            // float max = perk().cooldown().cooldown();
            // owner().getBukkitEntity().setFoodLevel(7 + (Math.round((max - (float) cooldown) / max * 13)));
            // if (cooldown <= 0 && cooldown() > 0) {
            //     if (!owner().getBukkitEntity().getAllowFlight()) owner().getBukkitEntity().setAllowFlight(true);
            // }
            super.cooldown(cooldown);
        }
    }
}
