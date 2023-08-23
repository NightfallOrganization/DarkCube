/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomSwordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ResetDurabilityCommand implements CommandExecutor {

    private final CustomSwordManager customSwordManager;

    public ResetDurabilityCommand(CustomSwordManager customSwordManager) {
        this.customSwordManager = customSwordManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getPersistentDataContainer().has(customSwordManager.durabilityKey, PersistentDataType.INTEGER)) {
                int maxDurability = customSwordManager.getMaxDurabilityOfSword(item);
                meta.getPersistentDataContainer().set(customSwordManager.durabilityKey, PersistentDataType.INTEGER, maxDurability);
                item.setItemMeta(meta);
                player.sendMessage("§aDurability wurde zurückgesetzt!");
                customSwordManager.updateSwordLore(item, meta
                        .getPersistentDataContainer()
                        .getOrDefault(customSwordManager.itemLevelKey, PersistentDataType.INTEGER, 0));
                return true;
            }
        }

        player.sendMessage("§cHalte ein gültiges Custom Sword in der Hand, um die Durability zurückzusetzen!");
        return true;
    }
}
