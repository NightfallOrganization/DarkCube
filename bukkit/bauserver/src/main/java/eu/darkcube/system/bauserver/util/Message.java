/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.util;

import java.io.IOException;
import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;

public enum Message implements BaseMessage {
    NOT_A_PLAYER_HEAD,
    SPECIFY_HEAD_NAME,
    NO_TEXTURE,
    SHIFT_RIGHT_TO_REMOVE,
    ADDED_HEAD,
    DATABASE_UPDATED,
    DATABASE_UPDATE_FAILED,
    BACK,
    SEARCH,
    DATABASE_UPDATING;

    private static final String PREFIX = "BAUSERVER_";
    private static final Function<String, String> KEY_MODIFIER = s -> PREFIX + s;

    public static void register() {
        for (var language : Language.values()) {
            register(language);
        }
    }

    private static void register(Language language) {
        try {
            language.registerLookup(Message.class.getClassLoader(), "messages_" + language.getLocale().toLanguageTag() + ".properties", KEY_MODIFIER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull String getPrefixModifier() {
        return PREFIX;
    }

    @Override
    public @NotNull String key() {
        return name();
    }
}
