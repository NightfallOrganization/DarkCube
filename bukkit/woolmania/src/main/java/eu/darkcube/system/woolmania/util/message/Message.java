/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.message;

import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;

public enum Message implements BaseMessage {

    HALL,
    HALLS_RESET,
    TIMER_IS_OVER,
    TIMER_IS_OVER_SECOUND,
    ZINA_GET_MONEY,
    LEVEL_TO_LOW,
    LEVEL_UP,
    ZINA_NO_WOOL,
    ONE_MINUTE_LEFT,
    SECOUND_LEFT,
    ZENUM_OWN_YOURSELF,
    ZENUM_OWN,
    ZENUM_SET_YOURSELF,
    ZENUM_SET_OTHER,
    ZENUM_SETTED,
    ZENUM_ADD_YOURSELF,
    ZENUM_ADD_OTHER,
    ZENUM_ADDED,
    ZENUM_REMOVE_YOURSELF,
    ZENUM_REMOVE_OTHER,
    ZENUM_REMOVED,
    ZENUM_SEND_YOURSELF,
    ZENUM_SEND_OTHER,
    ZENUM_SENDED,
    ZENUM_NOT_ENOUGH,
    LEVEL_RESET,
    LEVEL_RESET_OWN,
    LEVEL_RESETTED,
    SCOREBOARD_LEVEL,
    SCOREBOARD_ZENUM,
    SCOREBOARD_WORLD,
    SCOREBOARD_FARMED,
    SCOREBOARD_BOOSTER,
    BOOSTER_SET_OWN,
    BOOSTER_SET,
    BOOSTER_SETTED,
    BOOSTER_SET_OWN_NONE,
    BOOSTER_SET_NONE,
    BOOSTER_SETTED_NONE,
    ITEM_BUY_COST,
    ITEM_BUY_BOUGHT,
    ITEM_BUY_SELECTED,
    SOUND_BUYED,
    SOUND_SELECTED,
    FOOD_BUYED,
    NO_MONEY,
    SOUND_RESETTED,
    SOUND_RESET_OWN,
    SOUND_RESET,
    NOT_IN_HALL,
    NOT_OFFHAND,

    ;

    public static final String PREFIX_INVENTORY_ITEM = "INVENTORY_ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "WOOLMANIA_";
    public static final Function<String, String> KEY_MODIFIER = s -> KEY_PREFIX + s;
    public static final Function<String, String> INVENTORY_ITEM_MODIFIER = s -> KEY_MODIFIER.apply(PREFIX_INVENTORY_ITEM + s);

    private final String key;

    Message() {
        key = name();
    }

    @Override
    public String getPrefixModifier() {
        return KEY_PREFIX;
    }

    @Override
    public String key() {
        return key;
    }

    public net.kyori.adventure.text.Component getBukkit(Player player, Object... replacements) {
        return KyoriAdventureSupport.adventureSupport().convert(getMessage(UserAPI.instance().user(player.getUniqueId()), replacements));
    }

    public static Component getMessage(String messageKey, Language language, Object... replacements) {
        return language.getMessage(KEY_PREFIX + messageKey, replacements);
    }
}
