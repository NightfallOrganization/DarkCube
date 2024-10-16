/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerk;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCooldown;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCost;
import eu.darkcube.minigame.woolbattle.command.troll.CommandToggle;

public class CommandTroll extends WBCommand {
    public CommandTroll(WoolBattleBukkit woolbattle) {
        super("troll", b -> b.then(new CommandSetPerk(woolbattle).builder()).then(new CommandSetPerkCooldown(woolbattle).builder()).then(new CommandSetPerkCost(woolbattle).builder()).then(new CommandToggle().builder()));
    }

}
