/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.Command;

public class CommandSetMap extends Command {

	public CommandSetMap() {
		super(WoolBattle.getInstance(), "setmap", new Command[0], "Setzt die Map",
						CommandArgument.MAP);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
//			return Arrays.toSortedStringList(Arrays.asList(0, 6, 12, 17, 22, 99), args[0]);
			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
			if (map == null) {
				sender.sendMessage("§cUnbekannte Map: " + args[0]);
				return true;
			}
			sender.sendMessage("§aNeue Map: §5" + map.getName());
			WoolBattle.getInstance().baseMap = map;
			if (WoolBattle.getInstance().getLobby().isEnabled()) {
				WoolBattle.getInstance().getUserWrapper().getUsers().forEach(p -> {
					WoolBattle.getInstance().setMap(p);
				});
			} else if (WoolBattle.getInstance().getIngame().isEnabled()) {
				WoolBattle.getInstance().setMap(map);
				for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
					user.getBukkitEntity().teleport(user.getTeam().getSpawn());
				}
			}
//			Integer lifes = null;
//			try {
//				lifes = Integer.parseInt(args[0]);
//				if (lifes < 0) {
//					lifes = 0;
//				} else if (lifes > 999) {
//					lifes = 999;
//				}
//			} catch (Exception ex) {
//			}
//			if (lifes == null) {
//				if (user != null)
//					sender.sendMessage(Message.ENTER_POSITIVE_NUMBER.getMessage(user));
//				else
//					sender.sendMessage(
//							Message.ENTER_POSITIVE_NUMBER.getMessage(Main.getInstance().getServerLanguage()));
//				return true;
//			}
//			Main.getInstance().baseLifes = lifes;
//			if (user != null) {
//				sender.sendMessage(Message.CHANGED_LIFES.getMessage(user, Integer.toString(lifes)));
//			} else {
//				sender.sendMessage(Message.CHANGED_LIFES.getMessage(Main.getInstance().getServerLanguage(),
//						Integer.toString(lifes)));
//			}
			return true;
		}
		return false;
	}
}
