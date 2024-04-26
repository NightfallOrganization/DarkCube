/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.pserver.plugin.link.Link;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.ForceMapCommand;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.ReviveCommand;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.SetLifesCommand;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.SetTeamCommand;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.TimerCommand;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.TrollCommand;

public class WoolBattleLink extends Link {

    public WoolBattleLink() throws Throwable {
        super();
    }

    @Override
    protected void link() throws Throwable {
        var api = WoolBattleBukkit.instance();
        StatsLink.enabled = false;
        CommandAPI.instance().unregisterPrefixlessByPrefix("woolbattle");
        CommandAPI.instance().register(new ForceMapCommand(api));
        CommandAPI.instance().register(new SetLifesCommand(api));
        CommandAPI.instance().register(new SetTeamCommand(api));
        CommandAPI.instance().register(new TrollCommand(api));
        CommandAPI.instance().register(new TimerCommand(api));
        CommandAPI.instance().register(new ReviveCommand(api));
        System.out.println("§cDisabled woolbattle stats!");
    }

    @Override
    protected void unlink() {
    }
}
