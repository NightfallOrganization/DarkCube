/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.minigame.woolbattle.command.CommandDisableStats;
import eu.darkcube.minigame.woolbattle.command.CommandFix;
import eu.darkcube.minigame.woolbattle.command.CommandIsStats;
import eu.darkcube.minigame.woolbattle.command.CommandRevive;
import eu.darkcube.minigame.woolbattle.command.CommandSetLifes;
import eu.darkcube.minigame.woolbattle.command.CommandSetMap;
import eu.darkcube.minigame.woolbattle.command.CommandSetTeam;
import eu.darkcube.minigame.woolbattle.command.CommandSettings;
import eu.darkcube.minigame.woolbattle.command.CommandStart;
import eu.darkcube.minigame.woolbattle.command.CommandTimer;
import eu.darkcube.minigame.woolbattle.command.CommandTroll;
import eu.darkcube.minigame.woolbattle.command.CommandVoteLifes;
import eu.darkcube.minigame.woolbattle.command.CommandWoolBattle;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;

public class WoolBattleCommands {
    private final List<Command> commands = new ArrayList<>();

    public WoolBattleCommands(WoolBattleBukkit woolbattle) {
        commands.add(new CommandDisableStats());
        commands.add(new CommandFix(woolbattle));
        commands.add(new CommandIsStats());
        commands.add(new CommandSetMap(woolbattle));
        commands.add(new CommandSettings(woolbattle));
        commands.add(new CommandTimer(woolbattle));
        commands.add(new CommandStart(woolbattle));
        commands.add(new CommandTroll(woolbattle));
        commands.add(new CommandVoteLifes(woolbattle));
        commands.add(new CommandWoolBattle(woolbattle));
        commands.add(new CommandSetTeam(woolbattle));
        commands.add(new CommandSetLifes(woolbattle));
        commands.add(new CommandRevive(woolbattle));
    }

    public void enableAll() {
        for (Command command : commands) {
            CommandAPI.instance().register(command);
        }
    }

    public void disableAll() {
        for (Command command : commands) {
            CommandAPI.instance().unregister(command);
        }
    }
}
