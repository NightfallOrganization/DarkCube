/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.game;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

public interface Game {
    @Api GamePhase phase();

    @ApiStatus.Internal
    void enableNextPhase();

    @Api Collection<WBUser> users();

    @Api PerkRegistry perkRegistry();
}
