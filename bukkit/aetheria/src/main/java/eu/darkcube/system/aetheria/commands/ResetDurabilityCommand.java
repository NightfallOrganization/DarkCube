/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.items.CustomAxeManager;
import eu.darkcube.system.aetheria.items.CustomChestplateManager;
import eu.darkcube.system.aetheria.items.CustomPickaxeManager;
import eu.darkcube.system.aetheria.items.CustomSwordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ResetDurabilityCommand implements CommandExecutor {

    private final CustomSwordManager customSwordManager;
    private final CustomAxeManager customAxeManager;
    private final CustomChestplateManager customChestplateManager;
    private final CustomPickaxeManager customPickaxeManager;

    public ResetDurabilityCommand(CustomSwordManager customSwordManager, CustomAxeManager customAxeManager, CustomChestplateManager customChestplateManager, CustomPickaxeManager customPickaxeManager) {
        this.customSwordManager = customSwordManager;
        this.customAxeManager = customAxeManager;
        this.customChestplateManager = customChestplateManager;
        this.customPickaxeManager = customPickaxeManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            switch (item.getType()) {
                case NETHERITE_SWORD:
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

                    break;
                case NETHERITE_AXE:
                        if (meta.getPersistentDataContainer().has(customAxeManager.durabilityKey, PersistentDataType.INTEGER)) {
                            int maxDurability = customAxeManager.getMaxDurabilityOfAxe(item);
                            meta.getPersistentDataContainer().set(customAxeManager.durabilityKey, PersistentDataType.INTEGER, maxDurability);
                            item.setItemMeta(meta);
                            player.sendMessage("§aDurability wurde zurückgesetzt!");
                            customAxeManager.updateAxeLore(item, meta
                                    .getPersistentDataContainer()
                                    .getOrDefault(customAxeManager.itemLevelKey, PersistentDataType.INTEGER, 0));
                            return true;
                        }

                    break;
                case NETHERITE_PICKAXE:
                    if (meta.getPersistentDataContainer().has(customPickaxeManager.durabilityKey, PersistentDataType.INTEGER)) {
                        int maxDurability = customPickaxeManager.getMaxDurabilityOfPickaxe(item);
                        meta.getPersistentDataContainer().set(customPickaxeManager.durabilityKey, PersistentDataType.INTEGER, maxDurability);
                        item.setItemMeta(meta);
                        player.sendMessage("§aDurability wurde zurückgesetzt!");
                        customPickaxeManager.updatePickaxeLore(item, meta
                                .getPersistentDataContainer()
                                .getOrDefault(customPickaxeManager.itemLevelKey, PersistentDataType.INTEGER, 0));
                        return true;
                    }

                    break;
                case NETHERITE_CHESTPLATE:
                    if (meta.getPersistentDataContainer().has(customChestplateManager.durabilityKey, PersistentDataType.INTEGER)) {
                        int maxDurability = customChestplateManager.getMaxDurabilityOfChestplate(item);
                        meta.getPersistentDataContainer().set(customChestplateManager.durabilityKey, PersistentDataType.INTEGER, maxDurability);
                        item.setItemMeta(meta);
                        player.sendMessage("§aDurability wurde zurückgesetzt!");
                        customChestplateManager.updateChestplateLore(item, meta
                                .getPersistentDataContainer()
                                .getOrDefault(customChestplateManager.itemLevelKey, PersistentDataType.INTEGER, 0));
                        return true;
                    }

                    break;
                default:
                    player.sendMessage("§cHalte ein gültiges Custom Item in der Hand, um die Durability zurückzusetzen!");
                    return true;
            }

            return true;
        }

        player.sendMessage("§cHalte ein gültiges Custom Item in der Hand, um die Durability zurückzusetzen!");
        return true;
    }
}
