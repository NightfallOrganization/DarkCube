package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;

public class LeaveCommand extends WoolBattleCommand {
    public LeaveCommand(CommonWoolBattleApi woolbattleApi) {
        super("leave", b -> b.requires(source -> {
            var sender = source.sender();
            if (!(sender instanceof CommonWBUser user)) return false;
            return user.game() == null;
        }).executes(ctx -> {
            var source = ctx.getSource();
            var user = (CommonWBUser) source.sender();
            var woolbattle = woolbattleApi.woolbattle();
            woolbattle.setupMode().leave(user);
            source.sendMessage(Messages.LEFT_SETUP_MODE);
            return 0;
        }));
    }
}
