package eu.darkcube.minigame.woolbattle.common.command.commands;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.GameCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.LobbyCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.MigrateMapsCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.SetupCommand;

public class WoolBattleRootCommand extends WoolBattleCommand {
    public WoolBattleRootCommand(CommonWoolBattleApi woolbattle) {
        super("woolbattle", b -> b.then(new MigrateMapsCommand(woolbattle).builder()).then(new SetupCommand(woolbattle).builder()).then(new GameCommand(woolbattle).builder()).then(new LobbyCommand(woolbattle).builder()));
    }
}
