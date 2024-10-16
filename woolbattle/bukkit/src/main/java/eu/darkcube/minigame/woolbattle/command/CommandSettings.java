/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.inventory.SettingsInventory;
import eu.darkcube.minigame.woolbattle.user.WBUser;

public class CommandSettings extends WBCommand {
    public CommandSettings(WoolBattleBukkit woolbattle) {
        super("settings", b -> b.executes(ctx -> {
            WBUser user = WBUser.getUser(ctx.getSource().asPlayer());
            user.setOpenInventory(new SettingsInventory(woolbattle, user));
            return 0;
        }));
    }
}
