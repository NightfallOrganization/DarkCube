/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.items.CustomSwordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemLevelCommand implements CommandExecutor {

    private final CustomSwordManager swordManager;

    public ItemLevelCommand(CustomSwordManager swordManager) {
        this.swordManager = swordManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Dieser Befehl kann nur von Spielern verwendet werden");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) {
            player.sendMessage("§7Du hältst keinen gültigen Gegenstand");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        int level = meta.getPersistentDataContainer().getOrDefault(swordManager.itemLevelKey, PersistentDataType.INTEGER, 0);

        player.sendMessage("§7Das Level deines Gegenstandes ist: §a" + level);
        return true;
    }
}
