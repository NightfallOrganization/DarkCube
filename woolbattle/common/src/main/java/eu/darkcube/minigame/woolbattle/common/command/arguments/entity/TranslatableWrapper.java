/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.entity;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.Language;

public class TranslatableWrapper {
    public static Message text(Component text) {
        return new Messages.MessageWrapper(new BaseMessage.Transforming() {
            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                return text;
            }
        });
    }

    public static Message translatable(String translation) {
        return new Messages.MessageWrapper(new BaseMessage.Transforming() {
            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                return Component.translatable(translation);
            }
        });
    }

    public static Message translatable(String translation, Object... args) {
        return new Messages.MessageWrapper(new BaseMessage.Transforming() {
            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args2) {
                var cargs = new Component[args.length];
                for (var i = 0; i < args.length; i++) {
                    var arg = args[i];
                    Component component;
                    if (arg instanceof Component c) component = c;
                    else component = Component.text(String.valueOf(arg));
                    cargs[i] = component;
                }
                return Component.translatable(translation, cargs);
            }
        });
    }

    static Message translatableEscape(String key, Object... args) {
        for (int i = 0; i < args.length; ++i) {
            Object object = args[i];

            if (!isAllowedPrimitiveArgument(object) && !(object instanceof Component)) {
                args[i] = String.valueOf(object);
            }
        }

        return translatable(key, args);
    }

    public static boolean isAllowedPrimitiveArgument(@Nullable Object argument) {
        return argument instanceof Number || argument instanceof Boolean || argument instanceof String;
    }
}
