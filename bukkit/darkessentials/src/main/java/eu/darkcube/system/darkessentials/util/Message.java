/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

    SET_DAY,
    SET_NIGHT,
    FLY_ON,
    FLY_OFF,
    FLY_SET_ON,
    FLY_SET_OFF,
    FLY_SETTED_ON,
    FLY_SETTED_OFF,
    GAMEMODE_CHANGE,
    FEED,
    HEAL,
    MAX,
    TELEPORT,
    SPEED_WALK_SETTED_FROM,
    SPEED_FLY_SETTED_FROM,
    SPEED_WALK_SET,
    SPEED_FLY_SET,
    SPEED_WALK_SETTED,
    SPEED_FLY_SETTED,
    SPEED_RESET,
    CHAIN_SET,
    CHAIN_SETTED,
    CHAIN_UNSET,
    CHAIN_UNSETTED,
    CHAIN_CANT_DO_COMMAND,

    ;

    public static final String PREFIX_ITEM = "ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "ESSENTIALS_";

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
