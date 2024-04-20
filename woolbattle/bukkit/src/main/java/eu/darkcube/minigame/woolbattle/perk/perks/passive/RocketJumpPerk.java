/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.event.perk.other.DoubleJumpEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class RocketJumpPerk extends Perk {
    public static final PerkName ROCKET_JUMP = new PerkName("ROCKET_JUMP");

    public RocketJumpPerk() {
        super(ActivationType.PASSIVE, ROCKET_JUMP, 0, 0, Item.PERK_ROCKETJUMP, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        addListener(new ListenerRocketJump(this));
    }

    public static class ListenerRocketJump extends PerkListener {
        public ListenerRocketJump(Perk perk) {
            super(perk);
        }

        @EventHandler
        public void handle(DoubleJumpEvent event) {
            for (UserPerk ignored : event.user().perks().perks(perk().perkName())) {
                Vector velocity = event.velocity();
                velocity.setY(velocity.getY() * 1.25);
                event.velocity(velocity);
            }
        }
    }
}
