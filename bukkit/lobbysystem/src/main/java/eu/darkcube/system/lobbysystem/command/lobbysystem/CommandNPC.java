/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.*;

public class CommandNPC extends LobbyCommandExecutor {

	public CommandNPC() {
		super("npc", b -> b
				.then(new CommandSetWoolBattle().builder())
				.then(new CommandSetDailyReward().builder())
				.then(new CommandSetFisher().builder())
				.then(new CommandSetSumo().builder())
				.then(new CommandGamemodeConnector().builder()));
	}

}
