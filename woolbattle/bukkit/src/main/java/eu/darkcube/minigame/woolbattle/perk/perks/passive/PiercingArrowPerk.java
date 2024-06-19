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

public class PiercingArrowPerk extends Perk {
    public static final PerkName PIERCING_ARROW = new PerkName("PIERCING_ARROW");

    public PiercingArrowPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, PIERCING_ARROW, 0, 0, Item.PERK_PIERCING_ARROW, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        addListener(new PiercingArrowListener(woolbattle));
    }

    private static class PiercingArrowListener implements Listener {
        private final WoolBattleBukkit woolbattle;

        public PiercingArrowListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler public void handle(BowShootArrowEvent event) {
            if (event.user().perks().count(PIERCING_ARROW) != 0) {
                ArrowPerk.claimArrow(woolbattle, event.arrow(), event.user(), 2, 3);
            }
        }
    }
}
