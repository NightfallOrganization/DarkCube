package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;

import java.util.Locale;

public class GameModeCommand extends DarkCommand {

    public GameModeCommand() {
        super("gamemode", new String[]{"gm"}, builder-> {
            builder.then(Commands.argument("gamemode", EnumArgument.enumArgument(GameMode.values(), GameModeCommand::toStrings)).executes(context -> {
                GameMode gamemode = context.getArgument("gamemode", GameMode.class);
                gameMode(context, gamemode);
                return 0;
            }));
        });
    }

    private static String[] toStrings(GameMode gamemode) {
        return new String[] {
                gamemode.name().toLowerCase(Locale.ROOT),
                Integer.toString(gamemode.getValue())
        };
    }

    private static void gameMode(CommandContext<CommandSource> context, GameMode gamemode) throws CommandSyntaxException {
        context.getSource().asPlayer().setGameMode(gamemode);
        context.getSource().sendMessage(Component.text("§7[§eDark§6Cube§7] GameMode geändert zu §e" + gamemode));
    }

}
