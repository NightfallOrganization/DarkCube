/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.system.annotations.Api;

@Api
public interface LobbySystemLink {
    @Api
    boolean enabled();

    @Api
    void update();
}
