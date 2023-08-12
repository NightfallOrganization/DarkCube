/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class ShearsPerk extends Perk {
    public static final PerkName SHEARS = new PerkName("SHEARS");

    public ShearsPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.SECONDARY_WEAPON, SHEARS, 0, 0, Item.DEFAULT_SHEARS, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, woolbattle));
    }
}
