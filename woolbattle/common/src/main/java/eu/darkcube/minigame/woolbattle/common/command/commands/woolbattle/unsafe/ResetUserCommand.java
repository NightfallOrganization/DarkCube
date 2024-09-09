/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.unsafe;

import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.system.commandapi.argument.UUIDArgument.getUUID;
import static eu.darkcube.system.commandapi.argument.UUIDArgument.uuid;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.userapi.UserAPI;

public class ResetUserCommand extends WoolBattleCommand {
    public ResetUserCommand(CommonWoolBattleApi woolbattle) {
        super("resetUser", b -> b.then(argument("uuid", uuid()).executes(ctx -> {
            var uuid = getUUID(ctx, "uuid");
            var user = UserAPI.instance().user(uuid);

            for (var key : user.persistentData().keys()) {
                if (key.namespace().equals(woolbattle.namespace())) {
                    user.persistentData().remove(key);
                }
            }

            for (var game : woolbattle.games().games()) {
                for (var u : game.users()) {
                    if (u.uniqueId().equals(user.uniqueId())) {
                        u.recalculateAllValues();
                    }
                }
            }
            return 0;
        })));
    }
}
