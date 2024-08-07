/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;

public enum Message implements BaseMessage {
    ITEM_LORE_TEAMS_BOOK,
    ITEM_LORE_VOTING,
    ITEM_STICK,
    ITEM_WOOL,
    ITEM_HELM,
    ITEM_CHESTPLATE,
    ITEM_LEGGINGS,
    ITEM_BOOTS,
    ITEM_LORE_MAPS,
    BC_PLAYER_JOIN,
    BC_PLAYER_LEAVE,
    BC_TIMER_STOPPED,
    BC_GAME_START,
    BC_GAME_END,
    SCOREBOARD_HARDCORE_OFF,
    SCOREBOARD_TIME,
    SCOREBOARD_PLAYER_NEEDED,
    LIVES_SUFFIX_WHITE,
    LIVES_SUFFIX_BLACK;


    public static final String PREFIX_ITEM = "ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "SUMO_";

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

    public String convertToString(User user, Object... args) {
        Component component = getMessage(user, args);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public String convertToString(Player player, Object... args) {
        return convertToString(UserAPI.instance().user(player.getUniqueId()), args);
    }

}
