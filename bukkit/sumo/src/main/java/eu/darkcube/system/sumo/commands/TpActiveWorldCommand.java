/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.ruler.MainRuler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpActiveWorldCommand implements CommandExecutor {

    private MainRuler mainRuler;

    public TpActiveWorldCommand(MainRuler mainRuler) {
        this.mainRuler = mainRuler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Nur Spieler können diesen Command nutzen");
            return true;
        }

        Player player = (Player) sender;
        if (mainRuler.getActiveWorld() != null) {
            Location targetLocation = new Location(mainRuler.getActiveWorld(), 0.5, 102, 0.5);

            player.teleport(targetLocation);
            sender.sendMessage("§7Teleporting to §bActive World");
        } else {
            sender.sendMessage("§7Active World: §bNone");
        }

        return true;
    }
}
