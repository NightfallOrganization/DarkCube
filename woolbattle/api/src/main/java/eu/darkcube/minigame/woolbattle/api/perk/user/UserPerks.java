/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk.user;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;

public interface UserPerks {
    void reloadFromStorage();

    int count(PerkName perkName);

    Collection<UserPerk> perks();

    Collection<UserPerk> perks(ActivationType type);

    Collection<UserPerk> perks(PerkName perkName);

    UserPerk perk(int id);

    WBUser user();
}
