/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GetGUI implements CommandExecutor {

    @Override public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player player) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 4, Component
                    .text()
                    .content("               Items")
                    .color(TextColor.color(140, 202, 255))
                    .build());

            inventory.setItem(31, CustomArmor.getGUI1());
            inventory.setItem(4, CustomArmor.getGUI2());
            inventory.setItem(0, CustomArmor.getS1());
            inventory.setItem(1, CustomArmor.getS2());
            inventory.setItem(2, CustomArmor.getS3());
            inventory.setItem(3, CustomArmor.getS4());
            player.openInventory(inventory);

        } else {
            s.sendMessage("§7Du bist kein Spieler!");
        }

        return false;
    }
}
