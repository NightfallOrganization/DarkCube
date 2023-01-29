/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandLoadWorld extends SubCommand {

	public CommandLoadWorld() {
		super(WoolBattle.getInstance(), "loadWorld", new Command[0], "Nutzt eine Welt für WoolBattle", CommandArgument.WORLD);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length != 1)
				return false;
			String worldName = args[0];
			if (!new File(WoolBattle.getInstance().getServer().getWorldContainer(), worldName).exists()
					&& !new File(WoolBattle.getInstance().getServer().getWorldContainer().getParent(), worldName).exists()) {
				p.sendMessage("§cDiese Welt existiert nicht");
				return true;
			}
			YamlConfiguration cfg = WoolBattle.getInstance().getConfig("worlds");
			List<String> worlds = cfg.getStringList("worlds");
			if (worlds.contains(worldName)) {
				p.sendMessage("§cDiese Welt existiert bereits");
				return true;
			}
			worlds.add(worldName);
			cfg.set("worlds", worlds);
			WoolBattle.getInstance().saveConfig(cfg);
			WoolBattle.getInstance().loadWorld(worldName);
			p.sendMessage("§aWelt geladen");
			return true;
		}
		return false;
	}

}
