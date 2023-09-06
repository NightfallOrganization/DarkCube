/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.items.CustomPickaxeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MyItemDurabilityCommand implements CommandExecutor {

    private CustomPickaxeManager pickaxeManager;

    public MyItemDurabilityCommand(CustomPickaxeManager pickaxeManager) {
        this.pickaxeManager = pickaxeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern verwendet werden!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem == null || !heldItem.hasItemMeta()) {
            player.sendMessage("§cDas gehaltene Item hat keine Haltbarkeitsdaten!");
            return true;
        }

        ItemMeta meta = heldItem.getItemMeta();

        if (!meta.getPersistentDataContainer().has(pickaxeManager.durabilityKey, PersistentDataType.INTEGER)) {
            player.sendMessage("§cDas gehaltene Item hat keine benutzerdefinierte Haltbarkeitsdaten!");
            return true;
        }

        int durability = meta.getPersistentDataContainer().get(pickaxeManager.durabilityKey, PersistentDataType.INTEGER);
        player.sendMessage("§7Die Haltbarkeit des Items beträgt: §a" + durability);

        return true;
    }
}
