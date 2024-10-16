/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandImport;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListMaps;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListTeams;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandLoadGame;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandLoadWorld;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandUnloadGame;

public class CommandWoolBattle extends WBCommand {

    public CommandWoolBattle(WoolBattleBukkit woolbattle) {
        super("woolbattle", b -> b.then(new CommandTeam(woolbattle).builder()).then(new CommandCreateTeam(woolbattle).builder()).then(new CommandListTeams(woolbattle).builder()).then(new CommandCreateMap(woolbattle).builder()).then(new CommandMap(woolbattle).builder()).then(new CommandImport(woolbattle).builder()).then(new CommandListMaps(woolbattle).builder()).then(new CommandLoadWorld(woolbattle).builder()).then(new CommandLoadGame(woolbattle).builder()).then(new CommandUnloadGame(woolbattle).builder()).then(new CommandSetSpawn(woolbattle).builder()));
    }

}
