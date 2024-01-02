/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.lobby.LobbyTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand implements CommandExecutor {

    private final Sumo plugin;
    private final LobbyTimer lobbyTimer;

    public TimerCommand(Sumo plugin, LobbyTimer lobbyTimer) {
        this.plugin = plugin;
        this.lobbyTimer = lobbyTimer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("§7Verwendung: /timer <sekunden>");
            return true;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§7Bitte geben Sie eine gültige Zahl ein");
            return true;
        }

        lobbyTimer.setTimer(seconds);
        player.sendMessage("§7Timer wurde auf §b" + seconds + "§7 Sekunden gesetzt.");
        return true;
    }
}
