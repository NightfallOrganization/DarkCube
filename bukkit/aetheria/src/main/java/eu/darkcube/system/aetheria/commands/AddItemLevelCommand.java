/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.items.CustomChestplateManager;
import eu.darkcube.system.aetheria.items.CustomSwordManager;
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
        CustomSwordManager customSwordManager = plugin.customSwordManager();
        CustomChestplateManager customChestplateManager = plugin.customChestplateManager();

        customChestplateManager.increaseItemLevel(item, levelToAdd);
        customSwordManager.increaseItemLevel(item, levelToAdd);
        customSwordManager.setSwordAttackDamage(item, customSwordManager.getSwordAttackDamage(item) + (levelToAdd * 2));  // Erhöhen Sie den Schaden entsprechend dem neuen Level

        player.sendMessage("§7Das Level wurde erfolgreich §aerhöht§7!");

        return true;
    }
}
