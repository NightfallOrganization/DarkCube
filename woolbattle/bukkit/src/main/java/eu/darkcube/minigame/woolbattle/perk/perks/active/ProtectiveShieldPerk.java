/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;

public class ProtectiveShieldPerk extends Perk {
    public static final PerkName PROTECTIVE_SHIELD = new PerkName("PROTECTIVE_SHIELD");

    public ProtectiveShieldPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, PROTECTIVE_SHIELD, 15, 5, Item.PERK_PROTECTIVE_SHIELD, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_PROTECTIVE_SHIELD_COOLDOWN, woolbattle));
        addListener(new ListenerProtectiveShield(this, woolbattle));
    }

    public static class ListenerProtectiveShield extends BasicPerkListener {
        public ListenerProtectiveShield(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            perk.owner().projectileImmunityTicks(TimeUnit.SECOND.itoTicks(5));
            return true;
        }
    }
}
