/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.util.Language;
import org.bukkit.command.CommandSender;

public interface ICommandExecutor extends Audience {

    static ICommandExecutor create(CommandSender sender) {
        return new BukkitCommandExecutor(sender);
    }

    default void sendMessage(BaseMessage message, Object... components) {
        this.sendMessage(message.getMessage(this, components));
    }

    /**
     * @deprecated {@link #language()}
     */
    @Deprecated(forRemoval = true) default Language getLanguage() {
        return language();
    }

    /**
     * @deprecated {@link #language(Language)}
     */
    @Deprecated(forRemoval = true) default void setLanguage(Language language) {
        language(language);
    }

    Language language();

    void language(Language language);

    default String commandPrefix() {
        return "";
    }

    @Deprecated(forRemoval = true) default String getCommandPrefix() {
        return commandPrefix();
    }
}
