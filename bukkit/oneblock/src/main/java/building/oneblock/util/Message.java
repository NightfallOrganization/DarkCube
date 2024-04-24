/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.util;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

    TIMER,
    ONLY_PLAYERS_CAN_USE,
    COMMAND_DAY_SET,
    COMMAND_NIGHT_SET,
    COMMAND_FEED,
    COMMAND_FED,
    PLAYER_NOT_FOUND;

    public static final String PREFIX_ITEM = "ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "LOBBY_";

    private final String key;

    Message() {
        key = name();
    }

    @Override public String getPrefixModifier() {
        return KEY_PREFIX;
    }

    @Override public String key() {
        return key;
    }

}
