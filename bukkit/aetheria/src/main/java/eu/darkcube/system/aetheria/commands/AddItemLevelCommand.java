/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.items.CustomAxeManager;
import eu.darkcube.system.aetheria.items.CustomChestplateManager;
import eu.darkcube.system.aetheria.items.CustomSwordManager;
import eu.darkcube.system.aetheria.items.CustomPickaxeManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddItemLevelCommand implements CommandExecutor {

    private final Aetheria plugin;

    public AddItemLevelCommand(Aetheria plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von einem Spieler ausgeführt werden");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("§cVerwendung: /additemlevel <level>");
            return true;
        }

        int levelToAdd;
        try {
            levelToAdd = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cUngültige Nummer. Bitte geben Sie eine gültige Levelzahl ein");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        Material itemType = item.getType();
        CustomSwordManager customSwordManager = plugin.customSwordManager();
        CustomChestplateManager customChestplateManager = plugin.customChestplateManager();
        CustomPickaxeManager customPickaxeManager = plugin.customPickaxeManager();
        CustomAxeManager customAxeManager = plugin.customAxeManager();

        if (itemType == Material.NETHERITE_SWORD || itemType == Material.DIAMOND_SWORD || itemType == Material.IRON_SWORD || itemType == Material.STONE_SWORD || itemType == Material.WOODEN_SWORD || itemType == Material.GOLDEN_SWORD) {
            customSwordManager.increaseItemLevel(item, levelToAdd);
            customSwordManager.setSwordAttackDamage(item, customSwordManager.getSwordAttackDamage(item) + (levelToAdd * 2));  // Erhöhen Sie den Schaden entsprechend dem neuen Level

            // Aktualisieren Sie die Lore des Schwertes basierend auf den neuen Werten
            int newLevel = customSwordManager.getSwordAttackDamage(item) / 2;  // Hier erhalten Sie das aktualisierte Level basierend auf dem erhöhten Schaden
            customSwordManager.updateSwordLore(item, newLevel);
        }

        // Hier könnten Sie dann prüfen, ob es sich um eine Brustplatte handelt und dann die `increaseItemLevel`-Methode für die Brustplatte aufrufen
        else if (itemType == Material.NETHERITE_CHESTPLATE || itemType == Material.DIAMOND_CHESTPLATE || itemType == Material.IRON_CHESTPLATE || itemType == Material.CHAINMAIL_CHESTPLATE || itemType == Material.GOLDEN_CHESTPLATE) {
            customChestplateManager.increaseItemLevel(item, levelToAdd);
            // Andere Aktionen für die Brustplatte
        }

        else if (itemType == Material.NETHERITE_PICKAXE || itemType == Material.DIAMOND_PICKAXE || itemType == Material.IRON_PICKAXE || itemType == Material.GOLDEN_PICKAXE || itemType == Material.WOODEN_PICKAXE || itemType == Material.STONE_PICKAXE) {
            customPickaxeManager.increaseItemLevel(item, levelToAdd);
            // Andere Aktionen für die Brustplatte
        }

        else if (itemType == Material.NETHERITE_AXE || itemType == Material.DIAMOND_AXE || itemType == Material.IRON_AXE || itemType == Material.GOLDEN_AXE || itemType == Material.WOODEN_AXE || itemType == Material.STONE_AXE) {
            customAxeManager.increaseItemLevel(item, levelToAdd);
            // Andere Aktionen für die Brustplatte
        }

        player.sendMessage("§7Das Level wurde erfolgreich §aerhöht§7!");

        return true;
    }
}
