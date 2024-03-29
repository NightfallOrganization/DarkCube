package eu.darkcube.minigame.woolbattle.api.command;

import java.util.UUID;

import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface CommandSender extends Audience, CommandExecutor {
    boolean hasPermission(String permission);

    boolean isPlayer();

    @NotNull UUID playerUniqueId();
}
