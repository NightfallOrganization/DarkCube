package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.team;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.woolColorArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonTeamConfigurationArgument;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.TeamCommand;

public class SetWoolColorCommand extends WoolBattleCommand {
    public SetWoolColorCommand(CommonWoolBattleApi woolbattle) {
        super("setWoolColor", b -> b.then(argument("color", woolColorArgument()).executes(ctx -> {
            var team = CommonTeamConfigurationArgument.getTeamConfiguration(ctx, TeamCommand.TEAM_ARGUMENT_NAME);
            var color = WoolBattleArguments.woolColor(ctx, "color");
            var teamRegistry = woolbattle.teamRegistry();
            team.woolColor(color);
            teamRegistry.updateConfiguration(team);
            return 0;
        })));
    }
}
