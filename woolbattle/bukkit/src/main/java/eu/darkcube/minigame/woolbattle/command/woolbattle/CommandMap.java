/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.*;
import eu.darkcube.system.commandapi.v3.Commands;

public class CommandMap extends WBCommandExecutor {
    public CommandMap(WoolBattleBukkit woolbattle) {
        super("map", b -> b.then(Commands
                .argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                .then(Commands
                        .argument("map", MapArgument.mapArgument(woolbattle, MapArgument.ToStringFunction.function(ctx -> MapSizeArgument.mapSize(ctx, "mapSize"))))
                        .then(new CommandDisable().builder())
                        .then(new CommandEnable().builder())
                        .then(new CommandInfo().builder())
                        .then(new CommandSetDeathHeight().builder())
                        .then(new CommandSetIcon().builder())
                        .then(new CommandLoad(woolbattle).builder())
                        .then(new CommandDelete(woolbattle).builder()))));
    }
    //	public CommandMap() {
    //		super(WoolBattle.getInstance(), "map", new SubCommand[] {
    //				new CommandSetIcon(), new CommandEnable(), new CommandDisable(), new CommandInfo(),
    //				new CommandSetDeathHeight()
    //		}, "Map HauptCommand", CommandArgument.MAP);
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender paramCommandSender, String[] paramArrayOfString) {
    //		return false;
    //	}
    //
    //	@Override
    //	public List<String> onTabComplete(String[] args) {
    //		if (args.length == 1) {
    //			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
    //		}
    //		return super.onTabComplete(args);
    //	}

}
