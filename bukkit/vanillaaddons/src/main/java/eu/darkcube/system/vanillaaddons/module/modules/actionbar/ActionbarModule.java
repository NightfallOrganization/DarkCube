/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules.actionbar;

import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.vanillaaddons.module.Module;

public class ActionbarModule implements Module {
    private final ActionbarCommand actionbarCommand = new ActionbarCommand();

    @Override public void onEnable() {
        CommandAPI.instance().register(actionbarCommand);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(actionbarCommand);
    }
}
