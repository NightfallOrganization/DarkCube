/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command;

import java.util.UUID;

import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface CommandSender extends Audience, CommandExecutor {
    @Override
    boolean hasPermission(@NotNull String permission);

    boolean isPlayer();

    @NotNull
    UUID playerUniqueId();
}
