/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame;

import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattlemodern.CommandAddTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattlemodern.CommandListTasks;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattlemodern.CommandRemoveTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattlemodern.CommandSetSpawn;

public class CommandWoolBattleModern extends LobbyCommand {

    public CommandWoolBattleModern() {
        super("woolbattlemodern", b -> {
            b.then(new CommandAddTask().builder()).then(new CommandListTasks().builder()).then(new CommandRemoveTask().builder()).then(new CommandSetSpawn().builder());
        });
    }

}
