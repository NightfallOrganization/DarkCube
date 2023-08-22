/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomItemManager;
import eu.darkcube.system.aetheria.util.CustomSword;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GetItemCommand implements CommandExecutor {

    @Override public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player player) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 4, Component
                    .text()
                    .content("§f\uDAFF\uDFEFḊ")
                    .color(TextColor.color(140, 202, 255))
                    .build());

            inventory.setItem(0, CustomItemManager.getSwiftSword());
            inventory.setItem(1, CustomItemManager.getOrdinaryBag());
            inventory.setItem(2, CustomItemManager.getEnderBag());
            inventory.setItem(3, CustomItemManager.getRingOfHealing());
            inventory.setItem(4, CustomItemManager.getRingOfSpeed());
			inventory.setItem(5, CustomSword.getCustomSword(1)); // Setze das Level nach Belieben.

            player.openInventory(inventory);

        } else {
            s.sendMessage("§7Du bist kein Spieler!");
        }

        return false;
    }
}
