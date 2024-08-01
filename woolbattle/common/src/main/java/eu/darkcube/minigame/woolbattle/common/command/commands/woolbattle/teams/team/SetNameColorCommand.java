package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.team;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.*;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.TeamCommand;

public class SetNameColorCommand extends WoolBattleCommand {
    public SetNameColorCommand(CommonWoolBattleApi woolbattle) {
        super("setNameColor", b -> b.then(argument("color", textColorArgument()).executes(ctx -> {
            var team = teamConfiguration(ctx, TeamCommand.TEAM_ARGUMENT_NAME);
            var color = textColor(ctx, "color");
            var teamRegistry = woolbattle.teamRegistry();
            team.nameColor(color);
            teamRegistry.updateConfiguration(team);
            return 0;
        })));
    }
}
