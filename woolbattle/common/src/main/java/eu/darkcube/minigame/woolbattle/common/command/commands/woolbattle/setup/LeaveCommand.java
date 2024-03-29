package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;

public class LeaveCommand extends WoolBattleCommand {
    public LeaveCommand(CommonWoolBattleApi woolbattleApi) {
        super("leave", b -> b.requires(source -> {
            var sender = source.sender();
            if (!sender.isPlayer()) return false;
            var id = sender.playerUniqueId();
            System.out.println(woolbattleApi.woolbattle().setupMode().users());
            for (var user : woolbattleApi.woolbattle().setupMode().users()) {
                System.out.println(user.uniqueId());
                if (user.uniqueId().equals(id)) {
                    return true;
                }
            }
            return false;
        }).executes(ctx -> {
            var source = ctx.getSource();
            var sender = source.sender();
            var uniqueId = sender.playerUniqueId();
            CommonWBUser user = null;
            for (var u : woolbattleApi.woolbattle().setupMode().users()) {
                if (u.uniqueId().equals(uniqueId)) {
                    user = u;
                    break;
                }
            }
            if (user == null) {
                // Can't happen, but handle this safely
                source.sendMessage(Messages.NO_PLAYER);
                return 0;
            }
            var woolbattle = woolbattleApi.woolbattle();
            woolbattle.setupMode().leave(user);
            source.sendMessage(Messages.LEFT_SETUP_MODE);
            return 0;
        }));
    }
}
