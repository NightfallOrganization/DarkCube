/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.argument.BooleanArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandWinter extends LobbyCommand {
    public CommandWinter() {
        super("winter", b -> b.then(Commands.argument("winter", BooleanArgument.booleanArgument()).executes(ctx -> {
            var winter = BooleanArgument.getBoolean(ctx, "winter");
            Lobby.getInstance().getDataManager().setWinter(winter);
            ctx.getSource().sendMessage(Component.text("Winter: " + winter));
            return 0;
        })));
    }
}
