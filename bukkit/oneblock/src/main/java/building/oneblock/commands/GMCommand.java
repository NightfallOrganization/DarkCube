/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (args.length == 0 || args.length > 2) {
            player.sendMessage("§cUsage: /gm [value] [player]");
            return false; // Falsche Anzahl an Argumenten
        }

        GameMode gameMode;
        switch (args[0]) {
            case "0":
                gameMode = GameMode.SURVIVAL;
                break;
            case "1":
                gameMode = GameMode.CREATIVE;
                break;
            case "2":
                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                player.sendMessage("§cUsage: /gm [value] [player]");
                return false; // Ungültiger Spielmodus
        }

        if (args.length == 1) {
            player.setGameMode(gameMode);
            user.sendMessage(Message.COMMAND_GAMEMODE_CHANGE, gameMode.name());
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return true;
            }
            target.setGameMode(gameMode);
            User usertarget = UserAPI.instance().user(target.getUniqueId());
            usertarget.sendMessage(Message.COMMAND_GAMEMODE_CHANGE, gameMode.name());
            user.sendMessage(Message.COMMAND_GAMEMODE_SET, target.getName(), gameMode.name());
        }

        return true;
    }
}
