/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.CustomSword;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AddItemLevelCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CustomSword customSword;

    public AddItemLevelCommand(JavaPlugin plugin, CustomSword customSword) {
        this.plugin = plugin;
        this.customSword = customSword;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Dieser Befehl kann nur von einem Spieler verwendet werden");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§7Verwendung: /additemlevel <level>");
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        try {
            int levelToAdd = Integer.parseInt(args[0]);
            customSword.increaseItemLevel(itemInHand, levelToAdd);
            player.sendMessage("§7Das Item-Level wurde erfolgreich erhöht!");
        } catch (NumberFormatException e) {
            player.sendMessage("§7Bitte gib eine gültige Zahl als Level ein");
        }

        return true;
    }
}
