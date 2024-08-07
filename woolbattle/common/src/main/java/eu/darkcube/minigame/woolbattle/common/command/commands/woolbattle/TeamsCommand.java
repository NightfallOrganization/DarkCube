package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.TeamCommand;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class TeamsCommand extends WoolBattleCommand {
    public TeamsCommand(@NotNull CommonWoolBattleApi woolbattle) {
        super("teams", b -> b.then(new TeamCommand(woolbattle).builder()));
    }
}
