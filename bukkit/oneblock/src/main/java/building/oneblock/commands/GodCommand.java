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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            toggleGodMode((Player) sender);
            return true;
        } else if (args.length == 1) {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            Player target = Bukkit.getServer().getPlayer(args[0]);

            if (target != null) {
                toggleGodModeforPlayer(target, sender);
                return true;
            } else {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return false;
            }
        } else {
            sender.sendMessage("§cUsage: /god [player]");
            return false;
        }
    }

    private void toggleGodMode(Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());

        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            user.sendMessage(Message.COMMAND_GODMODE_OFF);
        } else {
            player.setInvulnerable(true);
            user.sendMessage(Message.COMMAND_GODMODE_ON);
        }
    }

    private void toggleGodModeforPlayer(Player player, CommandSender sender) {
        Player target = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());
        User targetuser = UserAPI.instance().user(target.getUniqueId());

        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            user.sendMessage(Message.COMMAND_GODMODE_OFF);

            if (sender != player) {
                targetuser.sendMessage(Message.COMMAND_GODMODE_DISABLED, player.getName());
            }

        } else {
            player.setInvulnerable(true);
            user.sendMessage(Message.COMMAND_GODMODE_ON);
            if (sender != player) {
                targetuser.sendMessage(Message.COMMAND_GODMODE_ENABLED, player.getName());
            }
        }
    }

}
