/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandGamemodeConnector;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetDailyReward;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetFisher;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetSumo;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetWoolBattle;

public class CommandNPC extends LobbyCommand {

	public CommandNPC() {
		super("npc", b -> b
				.then(new CommandSetWoolBattle().builder())
				.then(new CommandSetDailyReward().builder())
				.then(new CommandSetFisher().builder())
				.then(new CommandSetSumo().builder())
				.then(new CommandGamemodeConnector().builder()));
	}

}
