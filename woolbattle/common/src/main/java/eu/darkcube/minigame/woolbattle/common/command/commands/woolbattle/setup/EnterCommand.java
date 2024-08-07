/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.setup;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class EnterCommand extends WoolBattleCommand {
    public EnterCommand(CommonWoolBattleApi woolbattleApi) {
        super("enter", b -> b.requires(source -> {
            var sender = source.source();
            if (!(sender instanceof CommonWBUser user)) return false;
            var game = user.game();
            return game != null;
        }).executes(ctx -> {
            var source = ctx.getSource();
            var user = (CommonWBUser) source.sender();

            var woolbattle = woolbattleApi.woolbattle();
            woolbattle.setupMode().enter(user);
            return 0;
        }));
    }
}
