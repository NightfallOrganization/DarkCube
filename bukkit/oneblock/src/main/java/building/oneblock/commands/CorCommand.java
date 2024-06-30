/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.manager.player.CoreManager;
import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class CorCommand implements CommandExecutor {
    private final CoreManager coreManager;

    public CorCommand(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            int balance = coreManager.getCoreValue(player);
            user.sendMessage(Message.ONEBLOCK_COR_BALANCE, balance);
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("send") && sender instanceof Player) {
            return handleSend((Player) sender, args);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            return handleAdd(sender, args);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            return handleRemove(sender, args);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return handleSet(sender, args);
        }

        return false;
    }

    private boolean handleSend(Player sender, String[] args) {
        User user = UserAPI.instance().user(sender.getUniqueId());

        if (!sender.hasPermission("oneblock.cor.send.use")) {
            user.sendMessage(Message.DONT_HAVE_PERMISSION);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            user.sendMessage(Message.PLAYER_NOT_FOUND);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            user.sendMessage(Message.ONEBLOCK_INVALID_COR_NUMBER);
            return true;
        }

        if (amount <= 0) {
            user.sendMessage(Message.ONEBLOCK_COR_DONT_POSITIVE);
            return true;
        }

        int senderBalance = coreManager.getCoreValue(sender);
        if (amount > senderBalance) {
            user.sendMessage(Message.ONEBLOCK_COR_INCORRECT_INDICATION);
            return true;
        }

        coreManager.setCoreValue(sender, senderBalance - amount);
        coreManager.setCoreValue(target, coreManager.getCoreValue(target) + amount);

        user.sendMessage(Message.ONEBLOCK_COR_SENT, amount, target.getName());
        User usertarget = UserAPI.instance().user(target.getUniqueId());
        usertarget.sendMessage(Message.ONEBLOCK_COR_RECEIVED, amount, sender.getName());

        return true;
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (!sender.hasPermission("oneblock.cor.add.use")) {
            user.sendMessage(Message.DONT_HAVE_PERMISSION);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            user.sendMessage(Message.PLAYER_NOT_FOUND);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            user.sendMessage(Message.ONEBLOCK_INVALID_COR_NUMBER);
            return true;
        }

        if (amount <= 0) {
            user.sendMessage(Message.ONEBLOCK_COR_DONT_POSITIVE);
            return true;
        }

        int currentBalance = coreManager.getCoreValue(target);
        coreManager.setCoreValue(target, currentBalance + amount);

        user.sendMessage(Message.ONEBLOCK_COR_ADD, amount, target.getName());
        User usertarget = UserAPI.instance().user(target.getUniqueId());
        usertarget.sendMessage(Message.ONEBLOCK_COR_RECEIVED, amount, sender.getName());

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (!sender.hasPermission("oneblock.cor.remove.use")) {
            user.sendMessage(Message.DONT_HAVE_PERMISSION);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            user.sendMessage(Message.PLAYER_NOT_FOUND);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            user.sendMessage(Message.ONEBLOCK_INVALID_COR_NUMBER);
            return true;
        }

        if (amount <= 0) {
            user.sendMessage(Message.ONEBLOCK_COR_DONT_POSITIVE);
            return true;
        }

        int currentBalance = coreManager.getCoreValue(target);
        if (amount > currentBalance) {
            user.sendMessage(Message.ONEBLOCK_DOESENT_HAVE_ENOUGHT_COR);
            return true;
        }

        coreManager.setCoreValue(target, currentBalance - amount);

        user.sendMessage(Message.ONEBLOCK_COR_REMOVE, amount, target.getName());
        User usertarget = UserAPI.instance().user(target.getUniqueId());
        usertarget.sendMessage(Message.ONEBLOCK_COR_REMOVED, amount, sender.getName());

        return true;
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (!sender.hasPermission("oneblock.cor.set.use")) {
            user.sendMessage(Message.DONT_HAVE_PERMISSION);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            user.sendMessage(Message.PLAYER_NOT_FOUND);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            user.sendMessage(Message.ONEBLOCK_INVALID_COR_NUMBER);
            return true;
        }

        if (amount <= 0) {
            user.sendMessage(Message.ONEBLOCK_COR_DONT_POSITIVE);
            return true;
        }

        coreManager.setCoreValue(target, amount);

        user.sendMessage(Message.ONEBLOCK_COR_SET, amount, target.getName());
        User usertarget = UserAPI.instance().user(target.getUniqueId());
        usertarget.sendMessage(Message.ONEBLOCK_COR_SET_IT, amount, sender.getName());

        return true;
    }

}
