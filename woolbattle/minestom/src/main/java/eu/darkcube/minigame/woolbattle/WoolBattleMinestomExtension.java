/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import net.minestom.server.extensions.Extension;

public class WoolBattleMinestomExtension extends Extension {

    private MinestomWoolBattle woolbattle;

    @Override
    public void initialize() {
        woolbattle = new MinestomWoolBattle();
        woolbattle.start();
        woolbattle.api().fullyLoadedFuture().complete(null);
    }

    @Override
    public void terminate() {
        woolbattle.stop();
    }
}
