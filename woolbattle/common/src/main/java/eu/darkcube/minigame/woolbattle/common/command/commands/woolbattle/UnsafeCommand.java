/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.unsafe.ResetUserCommand;

public class UnsafeCommand extends WoolBattleCommand {
    public UnsafeCommand(CommonWoolBattleApi woolbattle) {
        // @formatter:off
        super("unsafe", b -> b
                .then(new ResetUserCommand(woolbattle).builder())
        );
        // @formatter:on
    }
}
