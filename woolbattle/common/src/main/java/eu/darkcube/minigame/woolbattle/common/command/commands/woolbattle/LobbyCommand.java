package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.lobby.SetSpawnCommand;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyCommand extends WoolBattleCommand {
    public LobbyCommand(@NotNull CommonWoolBattleApi woolbattle) {
        super("lobby", b -> b.then(new SetSpawnCommand(woolbattle).builder()));
    }
}
