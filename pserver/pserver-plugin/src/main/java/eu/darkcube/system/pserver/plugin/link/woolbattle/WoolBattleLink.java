/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.pserver.plugin.link.Link;
import eu.darkcube.system.pserver.plugin.link.woolbattle.command.*;

public class WoolBattleLink extends Link {

    public WoolBattleLink() throws Throwable {
        super();
    }

    @Override protected void link() throws Throwable {
        WoolBattleBukkit woolbattle = WoolBattleBukkit.instance();
        StatsLink.enabled = false;
        CloudNetLink.shouldDisplay = false;
        CommandAPI.instance().unregisterPrefixlessByPrefix("woolbattle");
        CommandAPI.instance().register(new ForceMapCommand(woolbattle));
        CommandAPI.instance().register(new SetLifesCommand(woolbattle));
        CommandAPI.instance().register(new SetTeamCommand(woolbattle));
        CommandAPI.instance().register(new TrollCommand(woolbattle));
        CommandAPI.instance().register(new TimerCommand(woolbattle));
        CommandAPI.instance().register(new ReviveCommand(woolbattle));
        System.out.println("§cDisabled woolbattle stats!");
    }

    @Override protected void unlink() {
    }
}
