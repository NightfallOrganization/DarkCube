/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.command;

import java.util.UUID;
import java.util.logging.Logger;

import eu.darkcube.minigame.woolbattle.api.command.CommandSender;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import net.luckperms.api.LuckPermsProvider;
import net.minestom.server.entity.Player;

public class MinestomCommandSender implements CommandSender, ForwardingAudience.Single {
    private static final Logger LOGGER = Logger.getLogger("MinestomCommandSender");
    private final net.minestom.server.command.CommandSender sender;
    private final Audience audience;

    public MinestomCommandSender(@NotNull net.minestom.server.command.CommandSender sender) {
        this.sender = sender;
        this.audience = MinestomAdventureSupport.adventureSupport().audienceProvider().audience(sender);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public @NotNull UUID playerUniqueId() {
        if (!(sender instanceof Player player)) throw new IllegalStateException("Not a player!");
        sender.identity().uuid();
        return player.getUuid();
    }

    @Override
    public @NotNull Audience audience() {
        return audience;
    }

    @Override
    public Language language() {
        if (sender instanceof Player player) {
            return UserAPI.instance().user(player.getUuid()).language();
        }
        return Language.DEFAULT;
    }

    @Override
    public void language(Language language) {
        if (sender instanceof Player player) {
            UserAPI.instance().user(player.getUuid()).language(language);
            return;
        }
        LOGGER.warning("Can't set language of console!");
    }
}
