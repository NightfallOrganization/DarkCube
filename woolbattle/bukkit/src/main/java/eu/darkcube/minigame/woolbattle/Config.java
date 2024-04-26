/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.system.util.data.Key;

public class Config {

    public static Key SPAWN;
    public static Key MIN_PLAYER_COUNT;
    public static Key LOBBYDEATHLINE;

    static void load(WoolBattleBukkit woolbattle) {
        SPAWN = new Key(woolbattle, "spawn");
        MIN_PLAYER_COUNT = new Key(woolbattle, "min_player_count");
        LOBBYDEATHLINE = new Key(woolbattle, "lobbydeathline");
    }
}
