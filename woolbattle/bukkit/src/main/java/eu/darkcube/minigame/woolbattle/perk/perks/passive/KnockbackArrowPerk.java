/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KnockbackArrowPerk extends Perk {
    public static final PerkName KNOCKBACK_ARROW = new PerkName("KNOCKBACK_ARROW");

    public KnockbackArrowPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, KNOCKBACK_ARROW, 0, 0, Item.PERK_KNOCKBACK_ARROW, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        addListener(new KnockBackArrowListener(woolbattle));
    }

    private static class KnockBackArrowListener implements Listener {
        private final WoolBattleBukkit woolbattle;

        public KnockBackArrowListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler public void handle(BowShootArrowEvent event) {
            if (event.user().perks().count(KNOCKBACK_ARROW) != 0) {
                ArrowPerk.claimArrow(woolbattle, event.arrow(), event.user(), 4, 1);
            }
        }

    }
}
