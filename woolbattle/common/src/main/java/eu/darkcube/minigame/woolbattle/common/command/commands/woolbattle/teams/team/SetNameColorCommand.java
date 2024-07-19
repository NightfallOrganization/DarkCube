package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.team;

import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonTeamConfigurationArgument;
import eu.darkcube.minigame.woolbattle.common.command.arguments.TextColorArgument;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.TeamCommand;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;

public class SetNameColorCommand extends WoolBattleCommand {
    public SetNameColorCommand(CommonWoolBattleApi woolbattle) {
        super("setNameColor", b -> b.then(argument("color", TextColorArgument.color()).executes(ctx -> {
            var team = CommonTeamConfigurationArgument.getTeamConfiguration(ctx, TeamCommand.TEAM_ARGUMENT_NAME);
            var color = TextColorArgument.getColor(ctx, "color");
            var teamRegistry = woolbattle.teamRegistry();
            team.nameStyle(Style.style(color));
            teamRegistry.updateConfiguration(team);
            return 0;
        })));
    }
}
