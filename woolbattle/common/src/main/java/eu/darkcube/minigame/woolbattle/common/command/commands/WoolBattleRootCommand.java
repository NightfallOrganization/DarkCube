/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.GameCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.LobbyCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.MapsCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.MigrateMapDataCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.MigrateMapsCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.SetupCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.TeamsCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.UnsafeCommand;

public class WoolBattleRootCommand extends WoolBattleCommand {
    public WoolBattleRootCommand(CommonWoolBattleApi woolbattle) {
        // @formatter:off
        super("woolbattle", b -> b
                .then(new MigrateMapsCommand(woolbattle).builder())
                .then(new SetupCommand(woolbattle).builder())
                .then(new GameCommand(woolbattle).builder())
                .then(new LobbyCommand(woolbattle).builder())
                .then(new TeamsCommand(woolbattle).builder())
                .then(new MapsCommand(woolbattle).builder())
                .then(new MigrateMapDataCommand(woolbattle).builder())
                .then(new UnsafeCommand(woolbattle).builder())
        );
        // @formatter:on
    }
}
