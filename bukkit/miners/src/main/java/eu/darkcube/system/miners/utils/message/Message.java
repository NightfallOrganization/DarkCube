/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils.message;

import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;

public enum Message implements BaseMessage {

    SCOREBOARD_HARDCORE,
    SCOREBOARD_TIME,
    SCOREBOARD_ABILITY,
    SCOREBOARD_NEEDED,
    SCOREBOARD_ONLINE,

    ABILITY_DIGGER,

    ;

    public static final String PREFIX_INVENTORY_ITEM = "INVENTORY_ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final String KEY_PREFIX = "MINERS_";
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
