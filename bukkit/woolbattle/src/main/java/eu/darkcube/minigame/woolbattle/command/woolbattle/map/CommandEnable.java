/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandEnable extends SubCommand {

	public CommandEnable() {
		super(WoolBattle.getInstance(), "enable", new Command[0], "Aktiviert die Map");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			Map map = WoolBattle.getInstance().getMapManager().getMap(getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
				return true;
			}
			if(map.isEnabled()) {
				sender.sendMessage("§cDiese Map ist bereits aktiviert!");
				return true;
			}
			map.enable();
			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' aktiviert!");
			return true;
		}
		return false;
	}

}
