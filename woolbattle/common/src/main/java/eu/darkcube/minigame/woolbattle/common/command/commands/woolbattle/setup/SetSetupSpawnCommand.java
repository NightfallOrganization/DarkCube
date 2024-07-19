package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class SetSetupSpawnCommand extends WoolBattleCommand {
    public SetSetupSpawnCommand(@NotNull CommonWoolBattleApi woolbattle) {
        super("setSetupSpawn", b -> b.requires(source -> source.sender().isPlayer()).executes(ctx -> {
            var source = ctx.getSource();
            var player = (CommonWBUser) source.sender();
            var location = player.location();
            if (location == null) {
                source.sendMessage(Messages.NOT_IN_A_WORLD);
                return 0;
            }
            var aligned = location.aligned();
            woolbattle.woolbattle().setupMode().spawnPoint(aligned);
            source.sendMessage(Messages.SET_SETUP_LOBBY_SPAWN);
            return 0;
        }));
    }
}
