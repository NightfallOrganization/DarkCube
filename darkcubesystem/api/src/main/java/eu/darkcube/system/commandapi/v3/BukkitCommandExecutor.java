/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.logging.Logger;

public class BukkitCommandExecutor implements ICommandExecutor, ForwardingAudience {

    private static final Logger logger = Logger.getLogger("System");
    private final CommandSender sender;
    private final Audience audience;

    BukkitCommandExecutor(CommandSender sender) {
        this.sender = sender;
        CommandSender s = sender;
        while (s instanceof ProxiedCommandSender) s = ((ProxiedCommandSender) s).getCaller();
        this.audience = AdventureSupport.audienceProvider().sender(s);
        Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
    }

    @Override public @NotNull Iterable<? extends Audience> audiences() {
        return Collections.singleton(audience);
    }

    @Override public Language language() {
        if (sender instanceof Player) {
            return UserAPI.getInstance().getUser((Player) sender).language();
        }
        return Language.DEFAULT;
    }

    @Override public void language(Language language) {
        if (sender instanceof Player) {
            UserAPI.getInstance().getUser((Player) sender).language(language);
        }
        logger.warning("Can't set language of the console!");
    }

    @Override public String commandPrefix() {
        return sender instanceof Player ? "/" : ICommandExecutor.super.commandPrefix();
    }

    public CommandSender sender() {
        return sender;
    }
}
