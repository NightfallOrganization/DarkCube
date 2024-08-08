/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.message;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

    HALLS_RESET,
    TIMER_IS_OVER,
    TIMER_IS_OVER_SECOUND,
    ZINA_GET_MONEY,
    LEVEL_TO_LOW,
    ZINA_NO_WOOL,
    ONE_MINUTE_LEFT,
    SECOUND_LEFT,
    ZENUM_OWN,

    ;

    public static final String PREFIX_ITEM = "ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "WOOLMANIA_";

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
