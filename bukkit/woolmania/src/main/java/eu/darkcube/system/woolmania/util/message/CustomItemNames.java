/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.message;

import static eu.darkcube.system.woolmania.util.message.Message.KEY_MODIFIER;

import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;

public enum CustomItemNames implements BaseMessage {

    FOOD_CARROT,
    FOOD_MELON,
    FOOD_STEAK,
    FOOD_DIAMOND,
    ITEM_NAME_WHITE_WOOL,
    ITEM_NAME_ORANGE_WOOL,
    ITEM_NAME_MAGENTA_WOOL,
    ITEM_NAME_LIGHT_BLUE_WOOL,
    ITEM_NAME_YELLOW_WOOL,
    ITEM_NAME_LIME_WOOL,
    ITEM_NAME_PINK_WOOL,
    ITEM_NAME_GRAY_WOOL,
    ITEM_NAME_LIGHT_GRAY_WOOL,
    ITEM_NAME_CYAN_WOOL,
    ITEM_NAME_PURPLE_WOOL,
    ITEM_NAME_BLUE_WOOL,
    ITEM_NAME_BROWN_WOOL,
    ITEM_NAME_GREEN_WOOL,
    ITEM_NAME_RED_WOOL,
    ITEM_NAME_BLACK_WOOL,
    GADGET_WOOLGRENADE,
    ITEM_SHEARS,


    ;

    private final String key;
    public static final String PREFIX_CUSTOM_ITEM = "CUSTOM_ITEM_";
    public static final Function<String, String> CUSTOM_ITEM_MODIFIER = s -> KEY_MODIFIER.apply(PREFIX_CUSTOM_ITEM + s);

    CustomItemNames() {
        key = name();
    }

    @Override
    public String getPrefixModifier() {
        return Message.KEY_PREFIX + PREFIX_CUSTOM_ITEM;
    }

    @Override
    public String key() {
        return key;
    }

    public net.kyori.adventure.text.Component getBukkit(Player player, Object... replacements) {
        return KyoriAdventureSupport.adventureSupport().convert(getMessage(UserAPI.instance().user(player.getUniqueId()), replacements));
    }

    public static Component getMessage(String messageKey, Language language, Object... replacements) {
        return language.getMessage(PREFIX_CUSTOM_ITEM + messageKey, replacements);
    }
}
