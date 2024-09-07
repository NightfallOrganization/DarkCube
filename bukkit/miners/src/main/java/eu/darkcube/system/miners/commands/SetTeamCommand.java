/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.commands;

import java.util.Collection;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.enums.Sounds;
import eu.darkcube.system.miners.utils.Team;
import eu.darkcube.system.miners.utils.message.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;

public class SetTeamCommand extends MinersCommand {

    public SetTeamCommand() {
        //@formatter:off
        super("setteam",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("team", StringArgumentType.string())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "team")))
                    )
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players, String input) throws CommandSyntaxException {
        for (Player player : players) {
            User user = UserAPI.instance().user(player.getUniqueId());

            gsa(input, player, user, Miners.getInstance().getTeamRed(), "red");
            gsa(input, player, user, Miners.getInstance().getTeamBlue(), "blue");
            gsa(input, player, user, Miners.getInstance().getTeamYellow(), "yellow");
            gsa(input, player, user, Miners.getInstance().getTeamGreen(), "green");
            gsa(input, player, user, Miners.getInstance().getTeamBlack(), "black");
            gsa(input, player, user, Miners.getInstance().getTeamWhite(), "white");
            gsa(input, player, user, Miners.getInstance().getTeamOrange(), "orange");
            gsa(input, player, user, Miners.getInstance().getTeamPurple(), "purple");
        }
        return 0;
    }

    private static void gsa(String input, Player player, User user, Team team, String teamName) {
        if (input.equals(teamName)) {
            team.addPlayer(player);
            user.sendMessage(Message.TEAM_JOIN, team.getName());
            Sounds.SOUND_SET.playSound(player);
        }
    }
}