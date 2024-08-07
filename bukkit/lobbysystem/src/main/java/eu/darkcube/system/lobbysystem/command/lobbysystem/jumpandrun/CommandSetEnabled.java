/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.argument.BooleanArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandSetEnabled extends LobbyCommand {
    public CommandSetEnabled() {
        super("setEnabled", b -> b.then(Commands.argument("enabled", BooleanArgument.booleanArgument()).executes(ctx -> {
            var s = BooleanArgument.getBoolean(ctx, "enabled");
            Lobby.getInstance().getDataManager().setJumpAndRunEnabled(s);
            ctx.getSource().sendMessage(Component.text("JumpAndRun Enabled: " + s));
            return 0;
        })));
    }
}
