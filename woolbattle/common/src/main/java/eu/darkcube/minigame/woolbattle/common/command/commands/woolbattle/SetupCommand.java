package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup.EnterCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup.LeaveCommand;

public class SetupCommand extends WoolBattleCommand {
    public SetupCommand(CommonWoolBattleApi woolbattle) {
        super("setup", b -> b.then(new EnterCommand(woolbattle).builder()).then(new LeaveCommand(woolbattle).builder()));
    }
}
