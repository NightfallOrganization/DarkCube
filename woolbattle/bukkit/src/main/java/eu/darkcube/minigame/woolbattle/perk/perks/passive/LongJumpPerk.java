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

public class LongJumpPerk extends Perk {
    public static final PerkName LONG_JUMP = new PerkName("LONG_JUMP");

    public LongJumpPerk() {
        super(ActivationType.PASSIVE, LONG_JUMP, 0, 0, Item.PERK_LONGJUMP, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        addListener(new ListenerLongJump(this));
    }

    public static class ListenerLongJump extends PerkListener {

        public ListenerLongJump(Perk perk) {
            super(perk);
        }

        @EventHandler
        public void handle(DoubleJumpEvent event) {
            for (UserPerk ignored : event.user().perks().perks(perk().perkName())) {
                Vector velocity = event.velocity();
                double y = velocity.getY();
                velocity = velocity.multiply(5.2);
                velocity.setY(y * 1.06);
                event.velocity(velocity);
            }
        }
    }
}
