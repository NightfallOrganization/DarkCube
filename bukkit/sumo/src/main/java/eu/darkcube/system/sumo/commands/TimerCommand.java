/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.other.StartingTimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TimerCommand implements CommandExecutor {

    private final StartingTimer startingTimer;

    public TimerCommand(StartingTimer startingTimer) {
        this.startingTimer = startingTimer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            try {
                int newTime = Integer.parseInt(args[0]);
                startingTimer.setTimer(newTime);
                sender.sendMessage("§7Timer wurde auf §b" + newTime + " §7Sekunden gesetzt.");
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage("§cBitte eine gültige Zahl eingeben!");
                return false;
            }
        } else {
            sender.sendMessage("§7Usage: /timer <Sekunden>");
            return false;
        }
    }
}
