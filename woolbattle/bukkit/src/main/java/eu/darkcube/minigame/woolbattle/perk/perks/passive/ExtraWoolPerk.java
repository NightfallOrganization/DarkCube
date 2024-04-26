/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;

public class ExtraWoolPerk extends Perk {
    public static final PerkName EXTRA_WOOL = new PerkName("EXTRA_WOOL");

    public ExtraWoolPerk() {
        super(ActivationType.PASSIVE, EXTRA_WOOL, 0, 0, Item.PERK_EXTRA_WOOL, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
    }
}
