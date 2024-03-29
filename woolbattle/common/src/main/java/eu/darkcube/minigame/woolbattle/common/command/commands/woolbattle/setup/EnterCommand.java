package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;

public class EnterCommand extends WoolBattleCommand {
    public EnterCommand(CommonWoolBattleApi woolbattleApi) {
        super("enter", b -> b.requires(source -> {
            var sender = source.source();
            if (!sender.isPlayer()) return false;
            var id = sender.playerUniqueId();
            for (var game : woolbattleApi.games().games()) {
                for (var user : game.users()) {
                    if (id.equals(user.uniqueId())) {
                        return true;
                    }
                }
            }
            return false;
        }).executes(ctx -> {
            var source = ctx.getSource();
            var sender = source.sender();
            var uniqueId = sender.playerUniqueId();
            CommonWBUser user = null;
            games:
            for (var game : woolbattleApi.games().games()) {
                for (var u : game.users()) {
                    if (uniqueId.equals(u.uniqueId())) {
                        user = u;
                        break games;
                    }
                }
            }
            if (user == null) {
                // Can't happen, but handle this safely
                source.sendMessage(Messages.NO_PLAYER);
                return 0;
            }

            var woolbattle = woolbattleApi.woolbattle();
            woolbattle.setupMode().enter(user);
            System.out.println(woolbattle.setupMode().users());
            return 0;
        }));
    }
}
