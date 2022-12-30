/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle;

import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.v3.CommandAPI;
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
		StatsLink.enabled = false;
		CloudNetLink.shouldDisplay = false;
		CommandAPI.getInstance().unregisterPrefixlessByPrefix("woolbattle");
		CommandAPI.getInstance().register(new ForceMapCommand());
		CommandAPI.getInstance().register(new SetLifesCommand());
		CommandAPI.getInstance().register(new SetTeamCommand());
		CommandAPI.getInstance().register(new TrollCommand());
		CommandAPI.getInstance().register(new TimerCommand());
		CommandAPI.getInstance().register(new ReviveCommand());
		System.out.println("§cDisabled woolbattle stats!");
	}

	@Override
	protected void unlink() {
	}
}
