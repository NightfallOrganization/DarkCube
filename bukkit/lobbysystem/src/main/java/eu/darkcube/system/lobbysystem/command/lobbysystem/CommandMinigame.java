/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.CommandSumo;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.CommandWoolBattle;

public class CommandMinigame extends LobbyCommand {

	public CommandMinigame() {
		super("minigame", b -> {
			b.then(new CommandWoolBattle().builder());
			b.then(new CommandSumo().builder());
		});
	}

}
