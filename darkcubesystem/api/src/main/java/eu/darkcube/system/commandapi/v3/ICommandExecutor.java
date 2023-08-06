/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.sun.corba.se.impl.ior.GenericTaggedComponent;
import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.util.Language;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public interface ICommandExecutor extends Audience {

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    @Deprecated default Language getLanguage() {
        return language();
    }

    Language language();

    @Deprecated default void setLanguage(Language language) {
        language(language);
    }

    void language(Language language);

    default String commandPrefix() {
        return "";
    }

    @Deprecated
    default String getCommandPrefix() {
        return commandPrefix();
    }

    static ICommandExecutor create(CommandSender sender){
        return new BukkitCommandExecutor(sender);
    }
}
