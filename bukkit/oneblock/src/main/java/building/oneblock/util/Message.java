/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.util;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

    ONLY_PLAYERS_CAN_USE,
    TIMER,
    PLAYER_NOT_FOUND,
    COMMAND_DAY_SET,
    COMMAND_NIGHT_SET,
    COMMAND_FEED,
    COMMAND_FED,
    COMMAND_FLY_ON,
    COMMAND_FLY_OFF,
    COMMAND_HAVE_FLY_ON,
    COMMAND_HAVE_FLY_OFF,
    COMMAND_GAMEMODE_CHANGE,
    COMMAND_GAMEMODE_SET,
    COMMAND_GODMODE_ON,
    COMMAND_GODMODE_OFF,
    COMMAND_GODMODE_ENABLED,
    COMMAND_GODMODE_DISABLED,
    COMMAND_GODMODE_SET_OFF,
    COMMAND_GODMODE_SET_ON,
    COMMAND_HEAL_GET,
    COMMAND_HEAL_SET,
    COMMAND_MAX_GET,
    COMMAND_MAX_SET,
    COMMAND_TELEPORT_WORLD,
    ONEBLOCK_WELCOME_WORLD,

    ONEBLOCK_CREATING_WORLD_ERROR,
    ONEBLOCK_CREATING_WORLD_1,
    ONEBLOCK_CREATING_WORLD_2,
    ONEBLOCK_WORLD_NOT_FOUND,
    ONEBLOCK_INVALID_COORDINATES,
    ONEBLOCK_INVALID_COR_NUMBER,
    ONEBLOCK_COR_DONT_POSITIVE,
    ONEBLOCK_COR_INCORRECT_INDICATION,
    ONEBLOCK_DOESENT_HAVE_ENOUGHT_COR,
    ONEBLOCK_COR_BALANCE,
    ONEBLOCK_COR_RECEIVED,
    ONEBLOCK_COR_SENT,
    ONEBLOCK_COR_ADD,
    ONEBLOCK_COR_REMOVE,
    ONEBLOCK_COR_SET,
    ONEBLOCK_COR_SET_IT,
    ONEBLOCK_COR_REMOVED,
    ONEBLOCK_WORLD_SUCCESSFUL_DELETED,
    ONEBLOCK_WORLD_ALREADY_LOADED,
    ONEBLOCK_WORLD_COULD_NOT_LOAD,
    ONEBLOCK_WORLD_SUCCESSFUL_LOADED,
    YOUR_ONEBLOCK_WORLD_SUCCESSFUL_LOADED,
    MINIMUM_REQUIREMENT,
    DONT_HAVE_PERMISSION;


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
