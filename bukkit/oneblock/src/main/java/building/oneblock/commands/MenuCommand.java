/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.gui.MenuGUI;
import building.oneblock.util.Message;
import eu.darkcube.system.commandapi.v3.CommandSource;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {
    private MenuGUI menuGUI;

    public MenuCommand(MenuGUI menuGUI) {
        this.menuGUI = menuGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            menuGUI.openMenuGUI(player);
            return true;
        } else {
            CommandSource commandSender = CommandSource.create(sender);
            commandSender.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }
    }
}
