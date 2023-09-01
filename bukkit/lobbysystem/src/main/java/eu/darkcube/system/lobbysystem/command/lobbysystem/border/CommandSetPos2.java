/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.BooleanArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.DataManager;
import org.bukkit.Location;

public class CommandSetPos2 extends LobbyCommand {

    public CommandSetPos2() {
        super("setPos2", b -> b
                .executes(ctx -> cmd(ctx, ctx.getSource().asPlayer().getLocation()))
                .then(Commands
                        .argument("makenice", BooleanArgument.booleanArgument())
                        .executes(ctx -> cmd(ctx, BooleanArgument.getBoolean(ctx, "makenice") ? Locations.getNiceLocation(ctx
                                .getSource()
                                .asPlayer()
                                .getLocation()) : ctx.getSource().asPlayer().getLocation()))));
    }

    private static int cmd(CommandContext<CommandSource> ctx, Location loc) {
        DataManager data = Lobby.getInstance().getDataManager();
        Border border = data.getBorder();
        border = new Border(border.getShape(), border.getRadius(), border.getLoc1(), loc);
        data.setBorder(border);
        ctx.getSource().sendMessage(Component.text("Position 2 gesetzt!").color(NamedTextColor.GREEN));
        return 0;
    }

}
