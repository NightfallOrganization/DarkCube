/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.manager.WorldSlotManager;
import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MySlotsCommand implements CommandExecutor {

    private WorldSlotManager worldSlotManager;

    public MySlotsCommand(WorldSlotManager worldSlotManager) {
        this.worldSlotManager = worldSlotManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (sender == null) {
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        // Lädt die Slots für den aktuellen Spieler
        worldSlotManager.loadSlots(player);

        // Erstellt eine Nachricht für den Spieler
        StringBuilder message = new StringBuilder(" \n");
        for (int i = 0; i < WorldSlotManager.MAX_SLOTS; i++) {
            message.append("§7Slot ").append(i + 1).append(": §e").append(worldSlotManager.getWorld(i)).append("\n");
        }

        // Sendet die zusammengestellte Nachricht an den Spieler
        player.sendMessage(message.toString());
        return true;
    }
}
