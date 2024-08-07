/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Locale;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EnumArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.GameMode;

public class GameModeCommand extends DarkCommand {

    public GameModeCommand() {
        super("gamemode", new String[]{"gm"}, builder -> {
            builder.then(Commands.argument("gamemode", EnumArgument.enumArgument(GameMode.values(), GameModeCommand::toStrings)).executes(context -> {
                GameMode gamemode = context.getArgument("gamemode", GameMode.class);
                gameMode(context, gamemode);
                return 0;
            }));
        });
    }

    private static String[] toStrings(GameMode gamemode) {
        return new String[]{gamemode.name().toLowerCase(Locale.ROOT), Integer.toString(gamemode.getValue())};
    }

    private static void gameMode(CommandContext<CommandSource> context, GameMode gamemode) throws CommandSyntaxException {
        context.getSource().asPlayer().setGameMode(gamemode);
        context.getSource().sendMessage(Message.GAMEMODE_CHANGE, gamemode);
    }

}
