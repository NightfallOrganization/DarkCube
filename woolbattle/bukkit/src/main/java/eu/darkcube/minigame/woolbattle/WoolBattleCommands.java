/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.command.*;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandExecutor;

import java.util.ArrayList;
import java.util.List;

public class WoolBattleCommands {
    private final List<CommandExecutor> commands = new ArrayList<>();

    public WoolBattleCommands(WoolBattleBukkit woolbattle) {
        commands.add(new CommandDisableStats());
        commands.add(new CommandFix());
        commands.add(new CommandIsStats());
        commands.add(new CommandSetMap(woolbattle));
        commands.add(new CommandSettings(woolbattle));
        commands.add(new CommandTimer());
        commands.add(new CommandTroll());
        commands.add(new CommandVoteLifes(woolbattle));
        commands.add(new CommandWoolBattle(woolbattle));
        commands.add(new CommandSetTeam(woolbattle));
        commands.add(new CommandSetLifes(woolbattle));
        commands.add(new CommandRevive());
    }

    public void enableAll() {
        for (CommandExecutor command : commands) {
            CommandAPI.instance().register(command);
        }
    }

    public void disableAll() {
        for (CommandExecutor command : commands) {
            CommandAPI.instance().unregister(command);
        }
    }
}
