/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FastArrowPerk extends Perk {
    public static final PerkName FAST_ARROW = new PerkName("FAST_ARROW");

    public FastArrowPerk() {
        super(ActivationType.PASSIVE, FAST_ARROW, 0, 0, Item.PERK_FAST_ARROW, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        addListener(new FastArrowListener());
    }

    private static class FastArrowListener implements Listener {
        @EventHandler
        public void handle(BowShootArrowEvent event) {
            for (UserPerk ignored : event.user().perks().perks(FAST_ARROW)) {
                event.arrow().setVelocity(event.arrow().getVelocity().multiply(1.6));
            }
        }
    }
}
